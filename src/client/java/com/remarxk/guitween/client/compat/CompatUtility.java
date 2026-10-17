package com.remarxk.guitween.client.compat;

import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
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

        if (progress > 1) {
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
            jeiTween.dx = TweenUtil.tween(0, -GUITweenClient.CONFIG.closeJeiMoveX, progress, GUITweenClient.CONFIG.closeJeiMoveEase.get());
            jeiTween.dy = TweenUtil.tween(0, GUITweenClient.CONFIG.closeJeiMoveY, progress, GUITweenClient.CONFIG.closeJeiMoveEase.get());
            return jeiTween;
        }

        float totalTick = Math.max(GUITweenClient.CONFIG.jeiMoveDuration, 1);
        float progress = GUITweenUtility.jeiOpenTick / totalTick;

        if (progress > 1) {
            return jeiTween;
        }

        jeiTween.inTween = true;

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
