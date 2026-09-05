package com.remarxk.guitween.mixin.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.compat.CompatUtility;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BookmarkOverlay.class, remap = false)
public class BookmarkOverlayMixin {
    @Unique
    private boolean gUITween$inTween;

    @Inject(
            method = "drawScreen",
            at = @At(
                    value = "HEAD"
            )
    )
    public void drawScreenBefore(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        CompatUtility.JeiTween jeiTween = CompatUtility.getJeiLeftTween();
        if (!jeiTween.inTween)
            return;

        gUITween$inTween = true;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(jeiTween.dx, jeiTween.dy, 0);
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

    @Inject(
            method = "drawOnForeground",
            at = @At(
                    value = "HEAD"
            ),
            require = 0
    )
    public void drawOnForegroundBefore(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        CompatUtility.JeiTween jeiTween = CompatUtility.getJeiLeftTween();
        if (!jeiTween.inTween)
            return;

        gUITween$inTween = true;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(jeiTween.dx, jeiTween.dy, 0);
    }

    @Inject(
            method = "drawOnForeground",
            at = @At(
                    value = "TAIL"
            ),
            require = 0
    )
    public void drawOnForegroundAfter(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (!gUITween$inTween) {
            return;
        }

        gUITween$inTween = false;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.popPose();
    }
}
