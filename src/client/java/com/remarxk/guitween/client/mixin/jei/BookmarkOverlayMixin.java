package com.remarxk.guitween.client.mixin.jei;

import com.remarxk.guitween.client.compat.CompatUtility;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BookmarkOverlay.class)
public class BookmarkOverlayMixin {
    @Unique
    private boolean gUITween$inTween;

    @Inject(
            method = "drawBackground",
            at = @At(
                    value = "HEAD"
            )
    )
    public void drawBackgroundBefore(DrawContext guiGraphics, CallbackInfo ci) {
        CompatUtility.JeiTween jeiTween = CompatUtility.getJeiLeftTween();
        if (!jeiTween.inTween)
            return;

        gUITween$inTween = true;

        MatrixStack poseStack = guiGraphics.getMatrices();
        poseStack.push();
        poseStack.translate(jeiTween.dx, jeiTween.dy, 0);
    }

    @Inject(
            method = "drawBackground",
            at = @At(
                    value = "TAIL"
            )
    )
    public void drawBackgroundAfter(DrawContext guiGraphics, CallbackInfo ci) {
        if (!gUITween$inTween) {
            return;
        }

        gUITween$inTween = false;

        MatrixStack poseStack = guiGraphics.getMatrices();
        poseStack.pop();
    }

    @Inject(
            method = "drawForeground",
            at = @At(
                    value = "HEAD"
            )
    )
    public void drawForegroundBefore(MinecraftClient minecraft, DrawContext guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        CompatUtility.JeiTween jeiTween = CompatUtility.getJeiLeftTween();
        if (!jeiTween.inTween)
            return;

        gUITween$inTween = true;

        MatrixStack poseStack = guiGraphics.getMatrices();
        poseStack.push();
        poseStack.translate(jeiTween.dx, jeiTween.dy, 0);
    }

    @Inject(
            method = "drawForeground",
            at = @At(
                    value = "TAIL"
            )
    )
    public void drawForegroundAfter(MinecraftClient minecraft, DrawContext guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!gUITween$inTween) {
            return;
        }

        gUITween$inTween = false;

        MatrixStack poseStack = guiGraphics.getMatrices();
        poseStack.pop();
    }

    @Inject(
            method = "drawOnForeground",
            at = @At(
                    value = "HEAD"
            )
    )
    public void drawOnForegroundBefore(DrawContext guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        CompatUtility.JeiTween jeiTween = CompatUtility.getJeiLeftTween();
        if (!jeiTween.inTween)
            return;

        gUITween$inTween = true;

        MatrixStack poseStack = guiGraphics.getMatrices();
        poseStack.push();
        poseStack.translate(jeiTween.dx, jeiTween.dy, 0);
    }

    @Inject(
            method = "drawOnForeground",
            at = @At(
                    value = "TAIL"
            )
    )
    public void drawOnForegroundAfter(DrawContext guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (!gUITween$inTween) {
            return;
        }

        gUITween$inTween = false;

        MatrixStack poseStack = guiGraphics.getMatrices();
        poseStack.pop();
    }
}
