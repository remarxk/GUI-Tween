//package com.remarxk.guitween.mixin.jei;
//
//import com.remarxk.guitween.compat.CompatUtility;
//import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiGraphicsExtractor;
//import org.joml.Matrix3x2fStack;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(value = BookmarkOverlay.class)
//public class BookmarkOverlayMixin {
//    @Unique
//    private boolean gUITween$inTween;
//
//    @Inject(
//            method = "drawScreen",
//            at = @At(
//                    value = "HEAD"
//            )
//    )
//    public void drawScreenBefore(Minecraft minecraft, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
//        CompatUtility.JeiTween jeiTween = CompatUtility.getJeiLeftTween();
//        if (jeiTween.inTween) {
//            gUITween$inTween = true;
//
//            Matrix3x2fStack poseStack = guiGraphics.pose();
//            poseStack.pushMatrix();
//            poseStack.translate(jeiTween.dx, jeiTween.dy);
//        }
//    }
//
//    @Inject(
//            method = "drawScreen",
//            at = @At(
//                    value = "TAIL"
//            )
//    )
//    public void drawScreenAfter(Minecraft minecraft, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
//        if (!gUITween$inTween) {
//            return;
//        }
//
//        gUITween$inTween = false;
//
//        Matrix3x2fStack poseStack = guiGraphics.pose();
//        poseStack.popMatrix();
//    }
//}
