package com.remarxk.guitween.client.mixin.jei;

import com.remarxk.guitween.client.compat.CompatUtility;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BookmarkOverlay.class)
public class BookmarkOverlayMixin {
    @Unique
    private boolean gUITween$inTween;

    @Inject(
            method = "drawForeground",
            at = @At(
                    value = "HEAD"
            ),
            require = 0
    )
    public void drawForegroundBefore(MinecraftClient minecraft, DrawContext guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        CompatUtility.JeiTween jeiTween = CompatUtility.getJeiLeftTween();
        if (!jeiTween.inTween)
            return;

        gUITween$inTween = true;

        Matrix3x2fStack poseStack = guiGraphics.getMatrices();
        poseStack.pushMatrix();
        poseStack.translate(jeiTween.dx, jeiTween.dy);
    }

    @Inject(
            method = "drawForeground",
            at = @At(
                    value = "TAIL"
            ),
            require = 0
    )
    public void drawForegroundAfter(MinecraftClient minecraft, DrawContext guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!gUITween$inTween) {
            return;
        }

        gUITween$inTween = false;

        Matrix3x2fStack poseStack = guiGraphics.getMatrices();
        poseStack.popMatrix();
    }
}
