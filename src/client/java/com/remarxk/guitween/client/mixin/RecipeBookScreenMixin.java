package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.anim.ContainerItemTween;
import com.remarxk.guitween.client.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.client.util.Tuple;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

/**
 * 工作台/熔炉等继承 RecipeBookScreen，且 RecipeBookScreen 重写了 render 并不调用
 * HandledScreen.render，因此 HandledScreen 上的 renderMoveItem 不会触发。
 * 这里在 RecipeBookScreen.render 的 renderCursorStack 调用前同样绘制移动中的物品。
 * 说明：x/y/handler 是 HandledScreen 的继承字段，RecipeBookScreen 上不能 @Shadow，
 * 改为通过 HandledScreen mixin 注入的 Access 接口与 ScreenHandlerProvider 获取。
 */
@Mixin(RecipeBookScreen.class)
public abstract class RecipeBookScreenMixin {
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/RecipeBookScreen;renderCursorStack(Lnet/minecraft/client/gui/DrawContext;II)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void guitween$renderMoveItem(DrawContext guiGraphics, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        ContainerItemTween tween = GUITweenUtility.getMoveItemTween();

        if (System.getProperty("guitween.debug.movetween") != null) {
            GUITween.LOGGER.info("[MoveTweenDbg] renderMoveItem(recipeBook) enter screen={} moveEnabled={} toMap={} fake={}",
                    this.getClass().getSimpleName(), GUITweenClient.CONFIG.isEnableMoveItem(), tween.getToMap().size(), tween.getFakeItemCount());
        }

        if (!GUITweenClient.CONFIG.isEnableMoveItem()) {
            tween.clearAllFakeItem();
            return;
        }

        HashMap<Integer, HashMap<Integer, ContainerItemTween.Single>> toMap = tween.getToMap();
        if (toMap.isEmpty()) {
            tween.clearAllFakeItem();
            return;
        }

        ScreenHandler handler = ((ScreenHandlerProvider<?>) this).getScreenHandler();
        AbstractContainerScreenMixinAccess access = (AbstractContainerScreenMixinAccess) this;
        int guiX = access.gUITween$getX();
        int guiY = access.gUITween$getY();

        toMap.forEach((to, fromList) -> {
            int targetX;
            int targetY;
            if (to != -1) {
                Slot toSlot = handler.getSlot(to);
                targetX = toSlot.x + guiX;
                targetY = toSlot.y + guiY;
            }
            else {
                int i2 = handler.getCursorStack().isEmpty() ? 8 : 16;
                targetX = mouseX - 8;
                targetY = mouseY - i2;
            }

            for (ContainerItemTween.Single single : fromList.values()) {
                Slot fromSlot = handler.getSlot(single.from);
                Tuple<Integer, Integer> pos = tween.getMoveTweenValue(fromSlot.x + guiX, fromSlot.y + guiY, targetX, targetY, single, to, handler);
                if (pos != null) {
                    guiGraphics.drawItem(single.itemStack, pos.getA(), pos.getB());
                    guiGraphics.drawStackOverlay(MinecraftClient.getInstance().textRenderer, single.itemStack, pos.getA(), pos.getB());
                }
            }
        });

        tween.removeUnuseFakeItem(handler);
        tween.removeStaleEmptyFake(handler);
    }
}
