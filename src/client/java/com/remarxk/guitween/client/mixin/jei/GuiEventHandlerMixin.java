package com.remarxk.guitween.client.mixin.jei;

import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.client.util.TweenUtil;
import mezz.jei.gui.events.GuiEventHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
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
    private void drawMainContentsBefore(HandledScreen<?> screen, DrawContext guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (!(screen instanceof AbstractContainerScreenMixinAccess access)) {
            return;
        }

        if (access.getGUITween$inTween()) {
            GUITweenUtility.popAlpha();

            float moveProgress = access.getGUITween$openTick() / GUITweenClient.CONFIG.windowMoveDuration;

            gUITween$dx = TweenUtil.tween(GUITweenClient.CONFIG.windowMoveX, 0, moveProgress, GUITweenClient.CONFIG.windowMoveEase.get());
            gUITween$dy = TweenUtil.tween(GUITweenClient.CONFIG.windowMoveY, 0, moveProgress, GUITweenClient.CONFIG.windowMoveEase.get());

            // 取消动画
            Matrix3x2fStack matrix3x2fStack = guiGraphics.getMatrices();
            matrix3x2fStack.translate(-gUITween$dx, -gUITween$dy);
        }
    }

    @Inject(
            method = "drawForContainerScreen",
            at = @At(
                    value = "TAIL"
            )
    )
    private void drawMainContentsAfter(HandledScreen<?> screen, DrawContext guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (!(screen instanceof AbstractContainerScreenMixinAccess access)) {
            return;
        }

        if (access.getGUITween$inTween()) {
            float gradientProgress = access.getGUITween$openTick() / GUITweenClient.CONFIG.windowGradientDuration;

            Matrix3x2fStack poseStack = guiGraphics.getMatrices();

            // 还原动画
            poseStack.translate(gUITween$dx, gUITween$dy);

            float alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, gradientProgress, GUITweenClient.CONFIG.windowGradientEase.get());
            GUITweenUtility.pushAlpha(alpha);
        }
    }
}
