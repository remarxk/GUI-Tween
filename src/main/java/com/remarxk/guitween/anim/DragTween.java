package com.remarxk.guitween.anim;

import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import net.minecraft.util.Mth;

public class DragTween {
    private static final float ANGLE_EPSILON = 0.01f;
    private static final float VELOCITY_EPSILON = 0.01f;
    private static final float MAX_SUBSTEP = 0.25f;
    private static final float VELOCITY_SMOOTHING = 0.5f;
    private static final float STIFFNESS_TO_OMEGA = 0.05f;

    private boolean init;
    private boolean running;

    private float lastX;
    private float smoothVel;
    private float angle;
    private float angularVel;

    public boolean isRunning() {
        return running;
    }

    public void stop() {
        init = false;
        running = false;

        smoothVel = 0f;
        angle = 0f;
        angularVel = 0f;
    }

    public float getAngle() {
        return angle;
    }

    public void setPos(float x, float y) {
        if (!init) {
            init = true;
            running = true;

            lastX = x;
            smoothVel = 0f;
            return;
        }

        float curVel = x - lastX;
        lastX = x;

        smoothVel = Mth.lerp(VELOCITY_SMOOTHING, smoothVel, curVel);
        running = true;
    }

    public void update() {
        float dt = Mth.clamp(GUITweenUtility.getDeltaTicks(), 0.0001f, 2f);

        float maxAngle = (float) Math.abs(GUITweenConfig.windowItem.dragMaxAngle.get());
        float target = Mth.clamp(smoothVel * GUITweenConfig.windowItem.dragSensitivity.get().floatValue(), -maxAngle, maxAngle);

        float omega = Math.max(GUITweenConfig.windowItem.dragStiffness.get().floatValue(), 0.05f) * STIFFNESS_TO_OMEGA;
        float zeta = Mth.clamp(GUITweenConfig.windowItem.dragDamping.get().floatValue(), 0.05f, 1f);
        float damping = 2f * zeta * omega;

        // 弹簧-阻尼二阶系统，按子步积分保证稳定
        int steps = Math.max(1, (int) Math.ceil(dt / MAX_SUBSTEP));
        float h = dt / steps;
        for (int i = 0; i < steps; i++) {
            float accel = omega * omega * (target - angle) - damping * angularVel;
            angularVel += accel * h;
            angle += angularVel * h;
        }

        if (Math.abs(angle) < ANGLE_EPSILON && Math.abs(angularVel) < VELOCITY_EPSILON) {
            angle = 0f;
            angularVel = 0f;
            running = false;
        }
    }
}
