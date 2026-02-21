package com.remarxk.guitween.anim;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.Ease;
import com.remarxk.guitween.util.TweenUtil;

public class AttackTween {
    public int slot = -1;
    private float progress;
    private boolean nextReset;
    private float subProgress;

    public boolean isRunning() {
        return progress > 0;
    }

    public void stop() {
        slot = -1;
        progress = 0f;
        nextReset = false;
    }

    public float getAngle() {
        float maxAngle = TweenUtil.tween(GUITween.CONFIG.attackMaxAngle, 0, 1 - progress, Ease.IN_OUT_SINE);
        return maxAngle * (float) Math.sin(subProgress);
    }

    public void resetProgress(int slot) {
        this.slot = slot;

        if (progress > 0) {
//            nextReset = true;
        }
        else {
            progress = 1f;
            subProgress = 0;
            nextReset = false;
        }
    }

    public void update() {
        subProgress += GUITweenUtility.getDeltaTicks() * progress;

        double tPI = 2 * Math.PI;

        if (subProgress >= tPI) {
            subProgress = subProgress % (float) tPI;

            if (nextReset) {
                nextReset = false;
                progress = 1f;
            }
            else {
                progress -= 1f;
            }
        }
    }
}
