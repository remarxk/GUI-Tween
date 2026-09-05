package com.remarxk.guitween.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.compat.CompatUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractContainerEventHandler implements Renderable {
    @Redirect(
            method = "renderTransparentBackground",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;fillGradient(IIIIII)V"
            )
    )
    private void fadeBackgroundOnClose(GuiGraphics graphics, int x0, int y0, int x1, int y1, int colorTop, int colorBottom) {
        if (((Object) this instanceof AbstractContainerScreenMixinAccess access)
                && !access.getGUITween$isDisableScreenTween()) {
            boolean closing = access.gUITween$inCloseTween();

            if (closing) {
                // 关闭动画期间让屏幕背后的半透明黑色渐变遮罩按 closeGradient 渐隐到完全透明
                float total = GUITweenConfig.getCloseWindowTotalDuration();
                float elapsed = Math.max(0, total - GUITweenUtility.closeScreenTick);
                float duration = GUITweenConfig.window.closeGradientDuration.get().floatValue();
                float progress = duration <= 0 ? 1 : Math.min(1, elapsed / duration);
                float fade = TweenUtil.tween(1, 0, progress, GUITweenConfig.window.closeGradientEase.get());
                fade = Math.max(0, Math.min(1, fade));

                colorTop = gUITween$multiplyAlpha(colorTop, fade);
                colorBottom = gUITween$multiplyAlpha(colorBottom, fade);
            }
            else if (GUITweenConfig.isEnableWindow()) {
                // 开启动画期间让遮罩从透明渐变到正常的黑色。
                // 背景渐隐时长 = 窗口渐变时长 - 2（例如默认 8 → 背景 6）
                float duration = Math.max(0, GUITweenConfig.window.gradientDuration.get().floatValue() - 2);
                float progress = duration <= 0 ? 1 : Math.min(1, GUITweenUtility.openScreenTick / duration);
                float fade = TweenUtil.tween(0, 1, progress, GUITweenConfig.window.gradientEase.get());
                fade = Math.max(0, Math.min(1, fade));

                colorTop = gUITween$multiplyAlpha(colorTop, fade);
                colorBottom = gUITween$multiplyAlpha(colorBottom, fade);
            }
        }

        graphics.fillGradient(x0, y0, x1, y1, colorTop, colorBottom);
    }

    @Unique
    private static int gUITween$multiplyAlpha(int color, float fade) {
        int alpha = (color >>> 24) & 0xFF;
        int newAlpha = (int) (alpha * fade);
        return (newAlpha << 24) | (color & 0x00FFFFFF);
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
