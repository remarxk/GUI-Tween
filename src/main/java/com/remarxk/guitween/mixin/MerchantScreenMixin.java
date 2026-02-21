package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends AbstractContainerScreen<MerchantMenu> {
    public MerchantScreenMixin(MerchantMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/trading/MerchantOffers;isEmpty()Z",
                    shift = At.Shift.BEFORE
            ),
            remap = false
    )
    public void renderSlotBefore(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci){
        AbstractContainerScreenMixinAccess access = (AbstractContainerScreenMixinAccess) this;
        if (!access.getGUITween$inTween())
            return;

        float openTick = access.getGUITween$openTick();
        float moveProgress = openTick / GUITween.CONFIG.windowMoveDuration;
        float gradientProgress = openTick / GUITween.CONFIG.windowMoveDuration;

        if (moveProgress >= 1 && gradientProgress >= 1)
            return;

        float dx = TweenUtil.tween(GUITween.CONFIG.windowMoveX, 0, moveProgress, GUITween.CONFIG.windowMoveEase.get());
        float dy = TweenUtil.tween(GUITween.CONFIG.windowMoveY, 0, moveProgress, GUITween.CONFIG.windowMoveEase.get());

        float alpha = TweenUtil.tween(0.05f, 1, gradientProgress, GUITween.CONFIG.windowGradientEase.get());
        GUITweenUtility.pushAlpha(alpha);

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        poseStack.translate(dx, dy, 0);
    }

    @Inject(
            method = "render",
            at = @At(value = "TAIL")
    )
    public void renderAfter(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        AbstractContainerScreenMixinAccess access = (AbstractContainerScreenMixinAccess) this;
        if (access.getGUITween$inTween()) {
            access.setGUITween$inTween(false);

            GUITweenUtility.popAlpha();

            PoseStack poseStack = guiGraphics.pose();
            poseStack.popPose();

            access.setGUITween$openTick(access.getGUITween$openTick() + GUITweenUtility.getDeltaTicks());
        }
    }
}
