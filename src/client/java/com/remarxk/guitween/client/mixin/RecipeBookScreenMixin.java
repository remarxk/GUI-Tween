package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.anim.ContainerItemTween;
import com.remarxk.guitween.client.util.Tuple;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

/**
 * 工作台/熔炉等继承 RecipeBookScreen，且 RecipeBookScreen 重写了 render 并不调用
 * HandledScreen.render，因此 HandledScreen 上的 renderMoveItem 不会触发。
 * 这里在 RecipeBookScreen.render 的 renderCursorStack 调用前同样绘制移动中的物品。
 */
@Mixin(RecipeBookScreen.class)
public abstract class RecipeBookScreenMixin {
    @Shadow
    protected int x;

    @Shadow
    protected int y;

    @Shadow
    @Final
    protected ScreenHandler handler;

    @Shadow
    private void drawItem(DrawContext guiGraphics, ItemStack stack, int x, int y, String amountText) {
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;renderCursorStack(Lnet/minecraft/client/gui/DrawContext;II)V",
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

        toMap.forEach((to, fromList) -> {
            int targetX;
            int targetY;
            if (to != -1) {
                Slot toSlot = handler.getSlot(to);
                targetX = toSlot.x + this.x;
                targetY = toSlot.y + this.y;
            }
            else {
                int i2 = handler.getCursorStack().isEmpty() ? 8 : 16;
                targetX = mouseX - 8;
                targetY = mouseY - i2;
            }

            for (ContainerItemTween.Single single : fromList.values()) {
                Slot fromSlot = handler.getSlot(single.from);
                Tuple<Integer, Integer> pos = tween.getMoveTweenValue(fromSlot.x + this.x, fromSlot.y + this.y, targetX, targetY, single, to, handler);
                if (pos != null) {
                    drawItem(guiGraphics, single.itemStack, pos.getA(), pos.getB(), null);
                }
            }
        });

        tween.removeUnuseFakeItem(handler);
        tween.removeStaleEmptyFake(handler);
    }
}
