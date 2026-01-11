package com.remarxk.guitween.util;

import com.remarxk.guitween.AnimationState;
import com.remarxk.guitween.GUITween;

import java.util.Stack;

public class AnimationStatePool {
    public final static int MAX_POOL_COUNT = 50;

    private final static Stack<AnimationState> pool = new Stack<>();

    public static int getPoolSize() {
        return pool.size();
    }

    public static AnimationState getAnimationState() {
        if (pool.isEmpty())
            return new AnimationState();

        return pool.pop();
    }

    public static void releaseAnimationState(AnimationState state) {
        if (pool.size() >= MAX_POOL_COUNT)
            return;

        pool.push(state);
    }
}
