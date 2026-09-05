package com.remarxk.guitween.compat;

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

        if (!GUITweenConfig.isEnableJei())
            return jeiTween;

        if (GUITweenUtility.isWindowClosing) {
            float duration = GUITweenConfig.closeJeiMoveDuration();
            float total = GUITweenConfig.getCloseJeiTotalDuration();
            float elapsed = Math.max(0, total - GUITweenUtility.closeJeiTick);
            float progress = duration <= 0 ? 1 : Math.min(1, elapsed / duration);

            jeiTween.inTween = true;
            jeiTween.dx = TweenUtil.tween(0, GUITweenConfig.closeJeiMoveX(), progress, GUITweenConfig.closeJeiMoveEase());
            jeiTween.dy = TweenUtil.tween(0, GUITweenConfig.closeJeiMoveY(), progress, GUITweenConfig.closeJeiMoveEase());
            return jeiTween;
        }

        float totalTick = Math.max(GUITweenConfig.jeiMoveDuration(), 1);
        float progress = GUITweenUtility.jeiOpenTick / totalTick;

        if (progress > 1){
            return jeiTween;
        }

        jeiTween.inTween = true;

        float dx = TweenUtil.tween(GUITweenConfig.jeiMoveX(), 0, progress, GUITweenConfig.jeiMoveEase());
        float dY = TweenUtil.tween(GUITweenConfig.jeiMoveY(), 0, progress, GUITweenConfig.jeiMoveEase());

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
            float duration = GUITweenConfig.closeJeiMoveDuration();
            float total = GUITweenConfig.getCloseJeiTotalDuration();
            float elapsed = Math.max(0, total - GUITweenUtility.closeJeiTick);
            float progress = duration <= 0 ? 1 : Math.min(1, elapsed / duration);

            jeiTween.inTween = true;
            // X 自动镜像：配置按左侧方向填写，右侧自动取反
            jeiTween.dx = TweenUtil.tween(0, -GUITweenConfig.closeJeiMoveX(), progress, GUITweenConfig.closeJeiMoveEase());
            jeiTween.dy = TweenUtil.tween(0, GUITweenConfig.closeJeiMoveY(), progress, GUITweenConfig.closeJeiMoveEase());
            return jeiTween;
        }

        float totalTick = Math.max(GUITweenConfig.jeiMoveDuration(), 1);
        float progress = GUITweenUtility.jeiOpenTick / totalTick;

        if (progress > 1){
            return jeiTween;
        }

        jeiTween.inTween = true;

        // X 自动镜像：配置按左侧方向填写，右侧自动取反
        jeiTween.dx = TweenUtil.tween(-GUITweenConfig.jeiMoveX(), 0, progress, GUITweenConfig.jeiMoveEase());
        jeiTween.dy = TweenUtil.tween(GUITweenConfig.jeiMoveY(), 0, progress, GUITweenConfig.jeiMoveEase());

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
