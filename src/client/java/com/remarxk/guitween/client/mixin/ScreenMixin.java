package com.remarxk.guitween.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.client.util.TweenUtil;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractParentElement implements Drawable {
    @Redirect(
            method = "renderInGameBackground",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;fillGradient(IIIIII)V"
            )
    )
    private void fadeBackgroundOnClose(DrawContext context, int x0, int y0, int x1, int y1, int colorTop, int colorBottom) {
        if (((Object) this instanceof AbstractContainerScreenMixinAccess access)
                && !access.getGUITween$isDisableScreenTween()) {
            boolean closing = access.gUITween$inCloseTween();

            if (closing) {
                // 关闭动画期间让屏幕背后的半透明黑色渐变遮罩随 closeGradient 渐隐到完全透明
                float total = GUITweenClient.CONFIG.getCloseWindowTotalDuration();
                float elapsed = Math.max(0, total - GUITweenUtility.closeScreenTick);
                float duration = GUITweenClient.CONFIG.closeGradientDuration;
                float progress = duration <= 0 ? 1 : Math.min(1, elapsed / duration);
                float fade = TweenUtil.tween(1, 0, progress, GUITweenClient.CONFIG.closeGradientEase.get());
                fade = Math.max(0, Math.min(1, fade));

                colorTop = gUITween$multiplyAlpha(colorTop, fade);
                colorBottom = gUITween$multiplyAlpha(colorBottom, fade);
            }
            else if (GUITweenClient.CONFIG.isEnableWindow()) {
                // 开启动画期间让遮罩从透明渐变到正常的黑色
                // 背景渐隐时长 = 窗口渐变时长 - 2（例如默认 8 -> 背景 6）
                float duration = Math.max(0, GUITweenClient.CONFIG.windowGradientDuration - 2);
                float progress = duration <= 0 ? 1 : Math.min(1, GUITweenUtility.openScreenTick / duration);
                float fade = TweenUtil.tween(0, 1, progress, GUITweenClient.CONFIG.windowGradientEase.get());
                fade = Math.max(0, Math.min(1, fade));

                colorTop = gUITween$multiplyAlpha(colorTop, fade);
                colorBottom = gUITween$multiplyAlpha(colorBottom, fade);
            }
        }

        context.fillGradient(x0, y0, x1, y1, colorTop, colorBottom);
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
                    target = "Lnet/minecraft/client/gui/screen/Screen;close()V")
    )
    private void onCloseBefore(Screen screen, Operation<Void> original) {
        if ((Object)this instanceof HandledScreen<?> containerScreen) {
            if (containerScreen instanceof AbstractContainerScreenMixinAccess access) {
                if (access.gUITween$playCloseTween()) {
                    return;
                }
            }
        }

        original.call(screen);
    }
}
