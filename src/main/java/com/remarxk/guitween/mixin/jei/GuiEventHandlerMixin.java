package com.remarxk.guitween.mixin.jei;

import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.util.TweenUtil;
import mezz.jei.gui.events.GuiEventHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiEventHandler.class)
public class GuiEventHandlerMixin {
    @Unique
    private float gUITween$dx;
    @Unique
    private float gUITween$dy;

    @Inject(
            method = "drawForContainerScreen",
            at = @At(
                    value = "HEAD"
            )
    )
    private void drawMainContentsBefore(AbstractContainerScreen<?> screen, GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (!(screen instanceof AbstractContainerScreenMixinAccess access)) {
            return;
        }

        if (access.getGUITween$inTween()) {
            GUITweenUtility.popAlpha();

            float moveProgress = access.getGUITween$openTick() / GUITweenConfig.window.moveDuration.get().floatValue();

            gUITween$dx = TweenUtil.tween(GUITweenConfig.window.moveX.get().floatValue(), 0, moveProgress, GUITweenConfig.window.moveEase.get());
            gUITween$dy = TweenUtil.tween(GUITweenConfig.window.moveY.get().floatValue(), 0, moveProgress, GUITweenConfig.window.moveEase.get());

            Matrix3x2fStack matrix3x2fStack = guiGraphics.pose();
            matrix3x2fStack.translate(-gUITween$dx, -gUITween$dy);
        }
    }

    @Inject(
            method = "drawForContainerScreen",
            at = @At(
                    value = "TAIL"
            )
    )
    private void drawMainContentsAfter(AbstractContainerScreen<?> screen, GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (!(screen instanceof AbstractContainerScreenMixinAccess access)) {
            return;
        }

        if (access.getGUITween$inTween()) {
            float gradientProgress = access.getGUITween$openTick() / GUITweenConfig.window.gradientDuration.get().floatValue();

            Matrix3x2fStack poseStack = guiGraphics.pose();

            // 动画变换
            poseStack.pushMatrix();
            poseStack.translate(gUITween$dx, gUITween$dy);  // 上移

            float alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, gradientProgress, GUITweenConfig.window.gradientEase.get());
            GUITweenUtility.pushAlpha(alpha);
        }
    }
}
