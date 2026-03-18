package com.remarxk.guitween.mixin.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.TweenUtil;
import mezz.jei.gui.overlay.IngredientListOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = IngredientListOverlay.class)
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

        if (!GUITweenConfig.isEnableJeiRight())
            return;

        float totalTick = Math.max(GUITweenConfig.window.jeiRightMoveDuration.get().floatValue(), 1);
        float progress = GUITweenUtility.openScreenTick / totalTick;

        if (progress > 1){
            return;
        }

        gUITween$inTween = true;

        Matrix3x2fStack poseStack = guiGraphics.pose();
        poseStack.pushMatrix();

        float dx = TweenUtil.tween(GUITweenConfig.window.jeiRightMoveX.get().floatValue(), 0, progress, GUITweenConfig.window.jeiRightMoveEase.get());
        float dy = TweenUtil.tween(GUITweenConfig.window.jeiRightMoveY.get().floatValue(), 0, progress, GUITweenConfig.window.jeiRightMoveEase.get());

        poseStack.translate(dx, dy);
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

        Matrix3x2fStack poseStack = guiGraphics.pose();
        poseStack.popMatrix();
    }
}
