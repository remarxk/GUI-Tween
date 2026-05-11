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

        if (!GUITweenClient.CONFIG.isEnableJeiLeft())
            return jeiTween;

        float totalTick = Math.max(GUITweenClient.CONFIG.jeiLeftMoveDuration, 1);
        float progress = GUITweenUtility.openScreenTick / totalTick;

        if (progress > 1){
            return jeiTween;
        }

        jeiTween.inTween = true;

        float dx = TweenUtil.tween(GUITweenClient.CONFIG.jeiLeftMoveX, 0, progress, GUITweenClient.CONFIG.jeiLeftMoveEase.get());
        float dY = TweenUtil.tween(GUITweenClient.CONFIG.jeiLeftMoveY, 0, progress, GUITweenClient.CONFIG.jeiLeftMoveEase.get());

        jeiTween.dx = dx;
        jeiTween.dy = dY;

        return jeiTween;
    }

    public static JeiTween getJeiRightTween() {
        jeiTween.inTween = false;

        if (GUITweenUtility.openScreenName == null)
            return jeiTween;

        if (!GUITweenClient.CONFIG.isEnableJeiRight())
            return jeiTween;

        float totalTick = Math.max(GUITweenClient.CONFIG.jeiRightMoveDuration, 1);
        float progress = GUITweenUtility.openScreenTick / totalTick;

        if (progress > 1){
            return jeiTween;
        }

        jeiTween.inTween = true;

        jeiTween.dx = TweenUtil.tween(GUITweenClient.CONFIG.jeiRightMoveX, 0, progress, GUITweenClient.CONFIG.jeiRightMoveEase.get());
        jeiTween.dy = TweenUtil.tween(GUITweenClient.CONFIG.jeiRightMoveY, 0, progress, GUITweenClient.CONFIG.jeiRightMoveEase.get());

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
