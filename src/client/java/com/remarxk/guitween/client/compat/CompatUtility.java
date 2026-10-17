package com.remarxk.guitween.client.compat;

import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.client.util.TweenUtil;

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
            float total = GUITweenClient.CONFIG.getCloseWindowTotalDuration();
            float elapsed = Math.max(0, total - GUITweenUtility.closeScreenTick);

            float moveProgress = GUITweenClient.CONFIG.closeMoveDuration <= 0
                    ? 1
                    : Math.min(1, elapsed / GUITweenClient.CONFIG.closeMoveDuration);
            float gradientProgress = GUITweenClient.CONFIG.closeGradientDuration <= 0
                    ? 1
                    : Math.min(1, elapsed / GUITweenClient.CONFIG.closeGradientDuration);

            tween.inTween = true;
            tween.dx = TweenUtil.tween(0, GUITweenClient.CONFIG.closeMoveX, moveProgress, GUITweenClient.CONFIG.closeMoveEase.get());
            tween.dy = TweenUtil.tween(0, GUITweenClient.CONFIG.closeMoveY, moveProgress, GUITweenClient.CONFIG.closeMoveEase.get());
            tween.alpha = TweenUtil.tween(1, 0, gradientProgress, GUITweenClient.CONFIG.closeGradientEase.get());
        }
        else {
            float moveProgress = GUITweenUtility.openScreenTick / GUITweenClient.CONFIG.windowMoveDuration;
            float gradientProgress = GUITweenUtility.openScreenTick / GUITweenClient.CONFIG.windowGradientDuration;

            tween.inTween = moveProgress < 1 || gradientProgress < 1;
            tween.dx = TweenUtil.tween(GUITweenClient.CONFIG.windowMoveX, 0, moveProgress, GUITweenClient.CONFIG.windowMoveEase.get());
            tween.dy = TweenUtil.tween(GUITweenClient.CONFIG.windowMoveY, 0, moveProgress, GUITweenClient.CONFIG.windowMoveEase.get());
            tween.alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, gradientProgress, GUITweenClient.CONFIG.windowGradientEase.get());
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

        if (!GUITweenClient.CONFIG.isEnableJei())
            return jeiTween;

        if (GUITweenUtility.isWindowClosing) {
            float duration = GUITweenClient.CONFIG.closeJeiMoveDuration;
            float total = GUITweenClient.CONFIG.getCloseJeiTotalDuration();
            float elapsed = Math.max(0, total - GUITweenUtility.closeJeiTick);
            float progress = duration <= 0 ? 1 : Math.min(1, elapsed / duration);

            jeiTween.inTween = true;
            jeiTween.dx = TweenUtil.tween(0, GUITweenClient.CONFIG.closeJeiMoveX, progress, GUITweenClient.CONFIG.closeJeiMoveEase.get());
            jeiTween.dy = TweenUtil.tween(0, GUITweenClient.CONFIG.closeJeiMoveY, progress, GUITweenClient.CONFIG.closeJeiMoveEase.get());
            return jeiTween;
        }

        float totalTick = Math.max(GUITweenClient.CONFIG.jeiMoveDuration, 1);
        float progress = GUITweenUtility.jeiOpenTick / totalTick;

        if (progress > 1){
            return jeiTween;
        }

        jeiTween.inTween = true;

        float dx = TweenUtil.tween(GUITweenClient.CONFIG.jeiMoveX, 0, progress, GUITweenClient.CONFIG.jeiMoveEase.get());
        float dY = TweenUtil.tween(GUITweenClient.CONFIG.jeiMoveY, 0, progress, GUITweenClient.CONFIG.jeiMoveEase.get());

        jeiTween.dx = dx;
        jeiTween.dy = dY;

        return jeiTween;
    }

    public static JeiTween getJeiRightTween() {
        jeiTween.inTween = false;

        if (GUITweenUtility.openScreenName == null)
            return jeiTween;

        if (!GUITweenClient.CONFIG.isEnableJei())
            return jeiTween;

        if (GUITweenUtility.isWindowClosing) {
            float duration = GUITweenClient.CONFIG.closeJeiMoveDuration;
            float total = GUITweenClient.CONFIG.getCloseJeiTotalDuration();
            float elapsed = Math.max(0, total - GUITweenUtility.closeJeiTick);
            float progress = duration <= 0 ? 1 : Math.min(1, elapsed / duration);

            jeiTween.inTween = true;
            // X 自动镜像：配置按左侧方向填写，右侧自动取反
            jeiTween.dx = TweenUtil.tween(0, -GUITweenClient.CONFIG.closeJeiMoveX, progress, GUITweenClient.CONFIG.closeJeiMoveEase.get());
            jeiTween.dy = TweenUtil.tween(0, GUITweenClient.CONFIG.closeJeiMoveY, progress, GUITweenClient.CONFIG.closeJeiMoveEase.get());
            return jeiTween;
        }

        float totalTick = Math.max(GUITweenClient.CONFIG.jeiMoveDuration, 1);
        float progress = GUITweenUtility.jeiOpenTick / totalTick;

        if (progress > 1){
            return jeiTween;
        }

        jeiTween.inTween = true;

        // X 自动镜像：配置按左侧方向填写，右侧自动取反
        jeiTween.dx = TweenUtil.tween(-GUITweenClient.CONFIG.jeiMoveX, 0, progress, GUITweenClient.CONFIG.jeiMoveEase.get());
        jeiTween.dy = TweenUtil.tween(GUITweenClient.CONFIG.jeiMoveY, 0, progress, GUITweenClient.CONFIG.jeiMoveEase.get());

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
