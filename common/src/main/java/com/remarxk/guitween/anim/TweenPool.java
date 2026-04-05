package com.remarxk.guitween.anim;

import java.util.Stack;

public class TweenPool {
    public final static int MAX_POOL_COUNT = 50;

    private final static Stack<Tween> pool = new Stack<>();

    public static int getPoolSize() {
        return pool.size();
    }

    public static Tween getTween() {
        if (pool.isEmpty())
            return new Tween();

        return pool.pop();
    }

    public static void releaseTween(Tween tween) {
        if (pool.size() >= MAX_POOL_COUNT)
            return;

        pool.push(tween);
    }
}
