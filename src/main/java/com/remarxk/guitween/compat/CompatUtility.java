package com.remarxk.guitween.compat;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
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

    public static JeiTween getJeiLeftTween() {
        jeiTween.inTween = false;

        if (GUITweenUtility.openScreenName == null)
            return jeiTween;

        if (!GUITween.CONFIG.isEnableJei())
            return jeiTween;

        if (GUITweenUtility.isWindowClosing) {
            float duration = GUITween.CONFIG.closeJeiMoveDuration;
            float total = GUITween.CONFIG.getCloseJeiTotalDuration();
            float elapsed = Math.max(0, total - GUITweenUtility.closeJeiTick);
            float progress = duration <= 0 ? 1 : Math.min(1, elapsed / duration);

            jeiTween.inTween = true;
            jeiTween.dx = TweenUtil.tween(0, GUITween.CONFIG.closeJeiMoveX, progress, GUITween.CONFIG.closeJeiMoveEase.get());
            jeiTween.dy = TweenUtil.tween(0, GUITween.CONFIG.closeJeiMoveY, progress, GUITween.CONFIG.closeJeiMoveEase.get());
            return jeiTween;
        }

        float totalTick = Math.max(GUITween.CONFIG.jeiMoveDuration, 1);
        float progress = GUITweenUtility.jeiOpenTick / totalTick;

        if (progress > 1){
            return jeiTween;
        }

        jeiTween.inTween = true;

        float dx = TweenUtil.tween(GUITween.CONFIG.jeiMoveX, 0, progress, GUITween.CONFIG.jeiMoveEase.get());
        float dY = TweenUtil.tween(GUITween.CONFIG.jeiMoveY, 0, progress, GUITween.CONFIG.jeiMoveEase.get());

        jeiTween.dx = dx;
        jeiTween.dy = dY;

        return jeiTween;
    }

    public static JeiTween getJeiRightTween() {
        jeiTween.inTween = false;

        if (GUITweenUtility.openScreenName == null)
            return jeiTween;

        if (!GUITween.CONFIG.isEnableJei())
            return jeiTween;

        if (GUITweenUtility.isWindowClosing) {
            float duration = GUITween.CONFIG.closeJeiMoveDuration;
            float total = GUITween.CONFIG.getCloseJeiTotalDuration();
            float elapsed = Math.max(0, total - GUITweenUtility.closeJeiTick);
            float progress = duration <= 0 ? 1 : Math.min(1, elapsed / duration);

            jeiTween.inTween = true;
            // X 自动镜像：配置按左侧方向填写，右侧自动取反
            jeiTween.dx = TweenUtil.tween(0, -GUITween.CONFIG.closeJeiMoveX, progress, GUITween.CONFIG.closeJeiMoveEase.get());
            jeiTween.dy = TweenUtil.tween(0, GUITween.CONFIG.closeJeiMoveY, progress, GUITween.CONFIG.closeJeiMoveEase.get());
            return jeiTween;
        }

        float totalTick = Math.max(GUITween.CONFIG.jeiMoveDuration, 1);
        float progress = GUITweenUtility.jeiOpenTick / totalTick;

        if (progress > 1){
            return jeiTween;
        }

        jeiTween.inTween = true;

        // X 自动镜像：配置按左侧方向填写，右侧自动取反
        jeiTween.dx = TweenUtil.tween(-GUITween.CONFIG.jeiMoveX, 0, progress, GUITween.CONFIG.jeiMoveEase.get());
        jeiTween.dy = TweenUtil.tween(GUITween.CONFIG.jeiMoveY, 0, progress, GUITween.CONFIG.jeiMoveEase.get());

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
