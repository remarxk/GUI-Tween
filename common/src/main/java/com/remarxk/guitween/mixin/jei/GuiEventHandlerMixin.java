//package com.remarxk.guitween.mixin.jei;
//
//import com.remarxk.guitween.GUITweenUtility;
//import com.remarxk.guitween.compat.CompatUtility;
//import mezz.jei.gui.events.GuiEventHandler;
//import net.minecraft.client.gui.GuiGraphicsExtractor;
//import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//import org.joml.Matrix3x2fStack;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(GuiEventHandler.class)
//public class GuiEventHandlerMixin {
//    @Inject(
//            method = "drawForContainerScreen",
//            at = @At(
//                    value = "HEAD"
//            )
//    )
//    private void drawMainContentsBefore(AbstractContainerScreen<?> screen, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
//        CompatUtility.OpenTween openTween = CompatUtility.getOpenTween();
//
//        if (openTween.inTween) {
//            GUITweenUtility.popAlpha();
//
//            // 取消动画
//            Matrix3x2fStack matrix3x2fStack = guiGraphics.pose();
//            matrix3x2fStack.translate(-openTween.dx, -openTween.dy);
//        }
//    }
//
//    @Inject(
//            method = "drawForContainerScreen",
//            at = @At(
//                    value = "TAIL"
//            )
//    )
//    private void drawMainContentsAfter(AbstractContainerScreen<?> screen, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
//        CompatUtility.OpenTween openTween = CompatUtility.getOpenTween();
//
//        if (openTween.inTween) {
//            // 取消动画
//            Matrix3x2fStack matrix3x2fStack = guiGraphics.pose();
//            matrix3x2fStack.translate(openTween.dx, openTween.dy);
//
//            GUITweenUtility.pushAlpha(openTween.alpha);
//        }
//    }
//}
