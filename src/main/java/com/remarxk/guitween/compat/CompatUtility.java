package com.remarxk.guitween.compat;

import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.util.TweenUtil;

public class CompatUtility {
    private static final OpenTween openTween = new OpenTween();
    private static final JeiTween jeiTween = new JeiTween();

    public static void startOpenTween(float dx, float dy, float alpha) {
        openTween.inTween = true;
        openTween.dx = dx;
        openTween.dy = dy;
        openTween.alpha = alpha;
    }

    public static void endOpenTween() {
        openTween.inTween = false;
    }

    public static OpenTween getOpenTween() {
        return openTween;
    }

    /**
     * 计算当前窗口动画的位移与透明度。
     * 开启动画与关闭动画使用不同的计时和配置，REI 等需要在背景渲染后重新压入窗口矩阵的兼容层必须使用同一套结果。
     */
    public static WindowTween getWindowTween(AbstractContainerScreenMixinAccess access) {
        WindowTween tween = new WindowTween();

        if (access.gUITween$inCloseTween()) {
            float total = GUITweenConfig.getCloseWindowTotalDuration();
            float elapsed = Math.max(0, total - GUITweenUtility.closeScreenTick);

            float moveProgress = GUITweenConfig.window.closeMoveDuration.get().floatValue() <= 0
                    ? 1
                    : Math.min(1, elapsed / GUITweenConfig.window.closeMoveDuration.get().floatValue());
            float gradientProgress = GUITweenConfig.window.closeGradientDuration.get().floatValue() <= 0
                    ? 1
                    : Math.min(1, elapsed / GUITweenConfig.window.closeGradientDuration.get().floatValue());

            tween.inTween = true;
            tween.dx = TweenUtil.tween(0, GUITweenConfig.window.closeMoveX.get().floatValue(), moveProgress, GUITweenConfig.window.closeMoveEase.get());
            tween.dy = TweenUtil.tween(0, GUITweenConfig.window.closeMoveY.get().floatValue(), moveProgress, GUITweenConfig.window.closeMoveEase.get());
            tween.alpha = TweenUtil.tween(1, 0, gradientProgress, GUITweenConfig.window.closeGradientEase.get());
        }
        else {
            float moveProgress = GUITweenUtility.openScreenTick / GUITweenConfig.window.moveDuration.get().floatValue();
            float gradientProgress = GUITweenUtility.openScreenTick / GUITweenConfig.window.gradientDuration.get().floatValue();

            tween.inTween = moveProgress < 1 || gradientProgress < 1;
            tween.dx = TweenUtil.tween(GUITweenConfig.window.moveX.get().floatValue(), 0, moveProgress, GUITweenConfig.window.moveEase.get());
            tween.dy = TweenUtil.tween(GUITweenConfig.window.moveY.get().floatValue(), 0, moveProgress, GUITweenConfig.window.moveEase.get());
            tween.alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, gradientProgress, GUITweenConfig.window.gradientEase.get());
        }

        return tween;
    }

    public static class WindowTween {
        public boolean inTween;
        public float dx;
        public float dy;
        public float alpha;
    }

    public static JeiTween getJeiLeftTween() {
        jeiTween.inTween = false;

        if (GUITweenUtility.openScreenName == null)
            return jeiTween;

        if (!GUITweenConfig.isEnableJei())
            return jeiTween;

        if (GUITweenUtility.isWindowClosing) {
            float duration = GUITweenConfig.window.closeJeiMoveDuration.get().floatValue();
            float total = GUITweenConfig.getCloseJeiTotalDuration();
            float elapsed = Math.max(0, total - GUITweenUtility.closeJeiTick);
            float progress = duration <= 0 ? 1 : Math.min(1, elapsed / duration);

            jeiTween.inTween = true;
            jeiTween.dx = TweenUtil.tween(0, GUITweenConfig.window.closeJeiMoveX.get().floatValue(), progress, GUITweenConfig.window.closeJeiMoveEase.get());
            jeiTween.dy = TweenUtil.tween(0, GUITweenConfig.window.closeJeiMoveY.get().floatValue(), progress, GUITweenConfig.window.closeJeiMoveEase.get());
            return jeiTween;
        }

        float totalTick = Math.max(GUITweenConfig.window.jeiMoveDuration.get().floatValue(), 1);
        float progress = GUITweenUtility.jeiOpenTick / totalTick;

        if (progress > 1){
            return jeiTween;
        }

        jeiTween.inTween = true;

        float dx = TweenUtil.tween(GUITweenConfig.window.jeiMoveX.get().floatValue(), 0, progress, GUITweenConfig.window.jeiMoveEase.get());
        float dY = TweenUtil.tween(GUITweenConfig.window.jeiMoveY.get().floatValue(), 0, progress, GUITweenConfig.window.jeiMoveEase.get());

        jeiTween.dx = dx;
        jeiTween.dy = dY;

        return jeiTween;
    }

    public static JeiTween getJeiRightTween() {
        jeiTween.inTween = false;

        if (GUITweenUtility.openScreenName == null)
            return jeiTween;

        if (!GUITweenConfig.isEnableJei())
            return jeiTween;

        if (GUITweenUtility.isWindowClosing) {
            float duration = GUITweenConfig.window.closeJeiMoveDuration.get().floatValue();
            float total = GUITweenConfig.getCloseJeiTotalDuration();
            float elapsed = Math.max(0, total - GUITweenUtility.closeJeiTick);
            float progress = duration <= 0 ? 1 : Math.min(1, elapsed / duration);

            jeiTween.inTween = true;
            // X 自动镜像：配置按左侧方向填写，右侧自动取反
            jeiTween.dx = TweenUtil.tween(0, -GUITweenConfig.window.closeJeiMoveX.get().floatValue(), progress, GUITweenConfig.window.closeJeiMoveEase.get());
            jeiTween.dy = TweenUtil.tween(0, GUITweenConfig.window.closeJeiMoveY.get().floatValue(), progress, GUITweenConfig.window.closeJeiMoveEase.get());
            return jeiTween;
        }

        float totalTick = Math.max(GUITweenConfig.window.jeiMoveDuration.get().floatValue(), 1);
        float progress = GUITweenUtility.jeiOpenTick / totalTick;

        if (progress > 1){
            return jeiTween;
        }

        jeiTween.inTween = true;

        // X 自动镜像：配置按左侧方向填写，右侧自动取反
        jeiTween.dx = TweenUtil.tween(-GUITweenConfig.window.jeiMoveX.get().floatValue(), 0, progress, GUITweenConfig.window.jeiMoveEase.get());
        jeiTween.dy = TweenUtil.tween(GUITweenConfig.window.jeiMoveY.get().floatValue(), 0, progress, GUITweenConfig.window.jeiMoveEase.get());

        return jeiTween;
    }

    public static class OpenTween {
        public boolean inTween;
        public float dx;
        public float dy;
        public float alpha;
    }

    public static class JeiTween {
        public boolean inTween;
        public float dx;
        public float dy;
    }
}
