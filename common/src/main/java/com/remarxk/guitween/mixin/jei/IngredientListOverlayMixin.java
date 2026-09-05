package com.remarxk.guitween.mixin.jei;

import com.remarxk.guitween.compat.CompatUtility;
import mezz.jei.gui.overlay.IngredientListOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
            method = "drawBackground",
            at = @At(
                    value = "HEAD"
            ),
            require = 0
    )
    public void drawBackgroundBefore(GuiGraphicsExtractor guiGraphics, CallbackInfo ci) {
        gUITween$startTween(guiGraphics);
    }

    @Inject(
            method = "drawBackground",
            at = @At(
                    value = "TAIL"
            ),
            require = 0
    )
    public void drawBackgroundAfter(GuiGraphicsExtractor guiGraphics, CallbackInfo ci) {
        gUITween$endTween(guiGraphics);
    }

    @Inject(
            method = "drawForeground",
            at = @At(
                    value = "HEAD"
            ),
            require = 0
    )
    public void drawForegroundBefore(Minecraft minecraft, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        gUITween$startTween(guiGraphics);
    }

    @Inject(
            method = "drawForeground",
            at = @At(
                    value = "TAIL"
            ),
            require = 0
    )
    public void drawForegroundAfter(Minecraft minecraft, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        gUITween$endTween(guiGraphics);
    }

    @Unique
    private void gUITween$startTween(GuiGraphicsExtractor guiGraphics) {
        CompatUtility.JeiTween jeiTween = CompatUtility.getJeiRightTween();
        if (jeiTween.inTween) {
            gUITween$inTween = true;

            Matrix3x2fStack poseStack = guiGraphics.pose();
            poseStack.pushMatrix();
            poseStack.translate(jeiTween.dx, jeiTween.dy);
        }
    }

    @Unique
    private void gUITween$endTween(GuiGraphicsExtractor guiGraphics) {
        if (!gUITween$inTween) {
            return;
        }

        gUITween$inTween = false;

        Matrix3x2fStack poseStack = guiGraphics.pose();
        poseStack.popMatrix();
    }
}
