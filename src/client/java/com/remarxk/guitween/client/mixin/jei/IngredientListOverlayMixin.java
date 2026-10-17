package com.remarxk.guitween.client.mixin.jei;

import com.remarxk.guitween.client.compat.CompatUtility;
import mezz.jei.gui.overlay.IngredientListOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
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
    public void drawBackgroundBefore(DrawContext guiGraphics, CallbackInfo ci) {
        gUITween$startTween(guiGraphics);
    }

    @Inject(
            method = "drawBackground",
            at = @At(
                    value = "TAIL"
            ),
            require = 0
    )
    public void drawBackgroundAfter(DrawContext guiGraphics, CallbackInfo ci) {
        gUITween$endTween(guiGraphics);
    }

    @Inject(
            method = "drawForeground",
            at = @At(
                    value = "HEAD"
            ),
            require = 0
    )
    public void drawForegroundBefore(MinecraftClient minecraft, DrawContext guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        gUITween$startTween(guiGraphics);
    }

    @Inject(
            method = "drawForeground",
            at = @At(
                    value = "TAIL"
            ),
            require = 0
    )
    public void drawForegroundAfter(MinecraftClient minecraft, DrawContext guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        gUITween$endTween(guiGraphics);
    }

    @Unique
    private void gUITween$startTween(DrawContext guiGraphics) {
        CompatUtility.JeiTween jeiTween = CompatUtility.getJeiRightTween();
        if (jeiTween.inTween) {
            gUITween$inTween = true;

            Matrix3x2fStack poseStack = guiGraphics.getMatrices();
            poseStack.pushMatrix();
            poseStack.translate(jeiTween.dx, jeiTween.dy);
        }
    }

    @Unique
    private void gUITween$endTween(DrawContext guiGraphics) {
        if (!gUITween$inTween) {
            return;
        }

        gUITween$inTween = false;

        Matrix3x2fStack poseStack = guiGraphics.getMatrices();
        poseStack.popMatrix();
    }
}
