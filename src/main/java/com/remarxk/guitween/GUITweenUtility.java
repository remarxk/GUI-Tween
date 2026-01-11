package com.remarxk.guitween;

import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.HashSet;

public class GUITweenUtility {
    public static final String OPEN_WINDOW = "OpenWindow";
    public static final String OPEN_WINDOW_ALPHA = "OpenWindowAlpha";

    public static final String TOOL_TIP = "TOOL_TIP";
    public static final String TOOL_TIP_ALPHA = "TOOL_TIP_ALPHA";

    private static final HashSet<String> inTween = new HashSet<>();

    private static final HashMap<String, Float> tweenValue = new HashMap<>();

    public static void setInTween(String name, boolean state) {
        if (state) {
            inTween.add(name);
        }
        else {
            inTween.remove(name);
        }
    }

    public static void setTweenValue(String name, Float value) {
        tweenValue.put(name, value);
    }

    public static boolean isInTween(String name) {
        return inTween.contains(name);
    }

    public static Float getTweenValue(String name) {
        return tweenValue.getOrDefault(name, null);
    }

    public static float getDeltaTicks() {
        return Minecraft.getInstance().getDeltaFrameTime();
    }
}