package com.remarxk.guitween.mixin.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.util.TweenUtil;
import mezz.jei.gui.overlay.IngredientListOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = IngredientListOverlay.class, remap = false)
public class IngredientListOverlayMixin {
    @Unique
    private boolean gUITween$inTween;

    @Inject(
            method = "drawScreen",
            at = @At(
                    value = "HEAD"
            )
    )
    public void drawScreenBefore(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (GUITweenUtility.openScreenName == null)
            return;

        if (!GUITween.CONFIG.isEnableJeiRight())
            return;

        float totalTick = Math.max(GUITween.CONFIG.jeiRightMoveDuration, 1);
        float progress = GUITweenUtility.openScreenTick / totalTick;

        if (progress > 1){
            return;
        }

        gUITween$inTween = true;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        float dx = TweenUtil.tween(GUITween.CONFIG.jeiRightMoveX, 0, progress, GUITween.CONFIG.jeiRightMoveEase.get());
        float dy = TweenUtil.tween(GUITween.CONFIG.jeiRightMoveY, 0, progress, GUITween.CONFIG.jeiRightMoveEase.get());

        poseStack.translate(dx, dy , 0);
    }

    @Inject(
            method = "drawScreen",
            at = @At(
                    value = "TAIL"
            )
    )
    public void drawScreenAfter(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!gUITween$inTween) {
            return;
        }

        gUITween$inTween = false;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.popPose();
    }
}
