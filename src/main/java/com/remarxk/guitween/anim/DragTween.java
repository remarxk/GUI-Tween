package com.remarxk.guitween.anim;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.Ease;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.util.Mth;

public class DragTween {
    private DragTweenState state;

    private float curAngle;
    private float maxAngle;

    private boolean init;
    private float lastX;

    private float velocity;

    private float swingSign;
    private float swingProgress;
    private float swingSubProgress;

    enum DragTweenState {
        None,
        Move,
        Stop
    }

    public boolean isRunning() {
        return state != DragTweenState.None;
    }

    public void stop() {
        init = false;
        state = DragTweenState.None;

        curAngle = 0;
        velocity = 0;
    }

    public float getAngle() {
        return curAngle;
    }

    public void update() {
        if (state == DragTweenState.Move) {
            float gotoSpeed = velocity * 3f;
            curAngle = Mth.lerp(0.3f, curAngle, curAngle + gotoSpeed);

            if (velocity > 0) {
                curAngle = Math.min(curAngle, maxAngle);
            }
            else {
                curAngle = Math.max(curAngle, maxAngle);
            }
        }
        else if (state == DragTweenState.Stop) {
            if (swingProgress >= 1 || maxAngle <= 0) {
                state = DragTweenState.None;
            }
            else {
                float max = TweenUtil.tween(maxAngle, 0, swingProgress, Ease.IN_OUT_SINE);
                curAngle = swingSign * max * (float) Math.sin(swingSubProgress);

                swingSubProgress += GUITweenUtility.getDeltaTicks();

                float tPI = 2 * (float) Math.PI;
                if (swingSubProgress >= tPI) {
                    swingSubProgress = swingSubProgress % tPI;

                    swingProgress += 0.6f;
                }
            }
        }
        else if (state == DragTweenState.None) {
            stop();
        }
    }


    /// 1. 进入拖拽移动时，根据拖拽速度根据旋转角度
    /// 2. 停止拖拽时，根据之前的速度计算最大的摇晃角度

    public void setPos(float x, float y) {
        if (!init) {
            init = true;
            lastX = x;
        }
        else {
            float lastVel = velocity;
            velocity = Mth.lerp(0.3f, lastVel, x - lastX);

            lastX = x;

            if (Math.abs(velocity) < 1f) {
                if (state == DragTweenState.Move) {
                    state = DragTweenState.Stop;
                    maxAngle = Math.abs(curAngle);

                    swingSubProgress = (float) Math.PI / 2;
                    swingProgress = 0;
                    swingSign = curAngle > 0 ? 1 : -1;
                }
            }
            else {
                state = DragTweenState.Move;
                maxAngle = calMaxAngle(velocity);
            }
        }
    }

    private float calMaxAngle(float vel) {
        float maxAngle = (float) Math.abs(GUITweenConfig.windowItem.dragMaxAngle.get());
        return Math.clamp(vel * GUITweenConfig.windowItem.dragSensitivity.get().floatValue(), -maxAngle, maxAngle);
    }
}
