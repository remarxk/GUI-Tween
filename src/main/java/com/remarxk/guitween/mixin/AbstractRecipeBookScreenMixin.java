package com.remarxk.guitween.mixin;

import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.anim.ContainerItemTween;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.util.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

/**
 * 工作台/熔炉等继承 AbstractRecipeBookScreen，且它重写了 render 并不调用
 * AbstractContainerScreen.render，因此 AbstractContainerScreen 上的 renderMoveItem 不会触发。
 * 这里在 AbstractRecipeBookScreen.render 的 renderCarriedItem 调用前同样绘制移动中的物品。
 * 说明：menu/guiLeft/guiTop 为 AbstractContainerScreen 的成员，子类上不能 @Shadow，
 * 通过 AbstractContainerScreen mixin 注入的 Access 接口获取。
 */
@Mixin(AbstractRecipeBookScreen.class)
public abstract class AbstractRecipeBookScreenMixin {
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/AbstractRecipeBookScreen;renderCarriedItem(Lnet/minecraft/client/gui/GuiGraphics;II)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void guitween$renderMoveItem(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        ContainerItemTween tween = GUITweenUtility.getMoveItemTween();

        if (!GUITweenConfig.isEnableMoveItem()) {
            tween.clearAllFakeItem();
            return;
        }

        HashMap<Integer, HashMap<Integer, ContainerItemTween.Single>> toMap = tween.getToMap();
        if (toMap.isEmpty()) {
            tween.clearAllFakeItem();
            return;
        }

        AbstractContainerScreenMixinAccess access = (AbstractContainerScreenMixinAccess) this;
        AbstractContainerMenu menu = access.gUITween$getMenu();
        int guiLeft = access.gUITween$getGuiLeft();
        int guiTop = access.gUITween$getGuiTop();

        toMap.forEach((to, fromList) -> {
            int targetX;
            int targetY;
            if (to != -1) {
                Slot toSlot = menu.getSlot(to);
                targetX = toSlot.x + guiLeft;
                targetY = toSlot.y + guiTop;
            }
            else {
                int i2 = menu.getCarried().isEmpty() ? 8 : 16;
                targetX = mouseX - 8;
                targetY = mouseY - i2;
            }

            for (ContainerItemTween.Single single : fromList.values()) {
                Slot fromSlot = menu.getSlot(single.from);
                Tuple<Integer, Integer> pos = tween.getMoveTweenValue(fromSlot.x + guiLeft, fromSlot.y + guiTop, targetX, targetY, single, to, menu);
                if (pos != null) {
                    guiGraphics.renderItem(single.itemStack, pos.getA(), pos.getB());
                    guiGraphics.renderItemDecorations(Minecraft.getInstance().font, single.itemStack, pos.getA(), pos.getB());
                }
            }
        });

        tween.removeUnuseFakeItem(menu);
        tween.removeStaleEmptyFake(menu);
    }
}
