package com.remarxk.guitween.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.compat.CompatUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractContainerEventHandler implements Renderable {
    @Inject(
            method = "extractBackground",
            at = @At(
                    value = "TAIL"
            )
    )
    private void renderBackgroundAfter(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (!((Object)this instanceof AbstractContainerScreen<?> containerScreen)) {
            return;
        }

        if (!(containerScreen instanceof AbstractContainerScreenMixinAccess access)) {
            return;
        }

        if (access.getGUITween$inTween()) { // 某些界面重写了render方法，导致没有取消渲染动画，需要强行终止
            access.setGUITween$inTween(false);
            access.setGUITween$isDisableScreenTween(true);

            GUITweenUtility.popAlpha();
            GUITweenUtility.enablePictureMatrix = false;

            CompatUtility.endOpenTween();
        }

        GUITweenUtility.setOpenScreen(access.getGUITween$screenName(), access.getGUITween$openTick());

        if (!GUITweenConfig.isEnableWindow())
            return;

        if (access.getGUITween$isDisableScreenTween())
            return;

        float gUITween$openTick = access.getGUITween$openTick();

        float moveProgress = gUITween$openTick / GUITweenConfig.windowMoveDuration();
        float gradientProgress = gUITween$openTick / GUITweenConfig.windowGradientDuration();

        if (moveProgress >= 1 && gradientProgress >= 1)
            return;

        access.setGUITween$inTween(true);

        float dx = TweenUtil.tween(GUITweenConfig.windowMoveX(), 0, moveProgress, GUITweenConfig.windowMoveEase());
        float dy = TweenUtil.tween(GUITweenConfig.windowMoveY(), 0, moveProgress, GUITweenConfig.windowMoveEase());

        Matrix3x2fStack poseStack = guiGraphics.pose();

        // 动画变换
        poseStack.pushMatrix();
        poseStack.translate(dx, dy);  // 上移

        float alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, gradientProgress, GUITweenConfig.windowGradientEase());
        GUITweenUtility.pushAlpha(alpha);
        GUITweenUtility.enablePictureMatrix = true;

        CompatUtility.startOpenTween(dx, dy, alpha);
    }

    @WrapOperation(
            method = "keyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;onClose()V")
    )
    private void onCloseBefore(Screen screen, Operation<Void> original) {
        if ((Object)this instanceof AbstractContainerScreen<?> containerScreen) {
            if (containerScreen instanceof AbstractContainerScreenMixinAccess access) {
                if (access.gUITween$playCloseTween()) {
                    return;
                }
            }
        }

        original.call(screen);
    }
}
