package com.remarxk.guitween.compat;

import com.mojang.blaze3d.vertex.PoseStack;
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

        float totalTick = Math.max(GUITweenConfig.window.jeiLeftMoveDuration.get().floatValue(), 1);
        float progress = GUITweenUtility.jeiOpenTick / totalTick;

        if (progress > 1){
            return jeiTween;
        }

        jeiTween.inTween = true;

        float dx = TweenUtil.tween(GUITweenConfig.window.jeiLeftMoveX.get().floatValue(), 0, progress, GUITweenConfig.window.jeiLeftMoveEase.get());
        float dY = TweenUtil.tween(GUITweenConfig.window.jeiLeftMoveY.get().floatValue(), 0, progress, GUITweenConfig.window.jeiLeftMoveEase.get());

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

        float totalTick = Math.max(GUITweenConfig.window.jeiRightMoveDuration.get().floatValue(), 1);
        float progress = GUITweenUtility.jeiOpenTick / totalTick;

        if (progress > 1){
            return jeiTween;
        }

        jeiTween.inTween = true;

        jeiTween.dx = TweenUtil.tween(GUITweenConfig.window.jeiRightMoveX.get().floatValue(), 0, progress, GUITweenConfig.window.jeiRightMoveEase.get());
        jeiTween.dy = TweenUtil.tween(GUITweenConfig.window.jeiRightMoveY.get().floatValue(), 0, progress, GUITweenConfig.window.jeiRightMoveEase.get());

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
