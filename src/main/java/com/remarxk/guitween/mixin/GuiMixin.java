package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.HotbarChangeListener;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "renderSlot", at = @At("HEAD"))
    public void renderHotbarBefore(GuiGraphics pGuiGraphics, int pX, int pY, float pPartialTick, Player pPlayer, ItemStack pStack, int pSeed, CallbackInfo ci){
        if (!GUITween.CONFIG.enable)
            return;

        if (HotbarChangeListener.lastSelected + 1 != pSeed)
            return;

        float centerX = pX + 8;
        float centerY = pY + 8;

        float scale;
        if (HotbarChangeListener.animTick < GUITween.CONFIG.holdItemScaleDuration) {
            float progress = (float) HotbarChangeListener.animTick / GUITween.CONFIG.holdItemScaleDuration;
            scale = TweenUtil.tween(1, GUITween.CONFIG.holdItemScale, progress, GUITween.CONFIG.holdItemScaleEase.get());
        }
        else {
            float progress = (float) (HotbarChangeListener.animTick - GUITween.CONFIG.holdItemScaleDuration) / GUITween.CONFIG.holdItemRestoreDuration;
            scale = TweenUtil.tween(GUITween.CONFIG.holdItemScale, 1, progress, GUITween.CONFIG.holdItemRestoreEase.get());
        }

        HotbarChangeListener.animTick++;

        PoseStack poseStack = pGuiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, 0);
        poseStack.scale(scale, scale, 1.0F);
        poseStack.translate(-centerX, -centerY, 0);
    }

    @Inject(method = "renderSlot", at = @At("TAIL"))
    public void renderHotbarAfter(GuiGraphics pGuiGraphics, int pX, int pY, float pPartialTick, Player pPlayer, ItemStack pStack, int pSeed, CallbackInfo ci){
        if (!GUITween.CONFIG.enable)
            return;

        if (HotbarChangeListener.lastSelected + 1 != pSeed)
            return;

        PoseStack poseStack = pGuiGraphics.pose();
        poseStack.popPose();
    }
}
