package com.remarxk.guitween;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;

import java.util.HashSet;
import java.util.Stack;

public class GUITweenUtility {
    public static final HashSet<Class<?>> WINDOW_DELAY_TICK = new HashSet<>();

    private static final Stack<Float> itemAlphaStack = new Stack<>();
    private static final Stack<Float> fontAlphaStack = new Stack<>();

    static {
        WINDOW_DELAY_TICK.add(MerchantScreen.class);
    }

    public static float getDeltaTicks() {
        return Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
    }

    public static void pushAlpha(float alpha) {
        pushItemAlpha(alpha);
        pushFontAlpha(alpha);
    }

    public static void popAlpha() {
        popItemAlpha();
        popFontAlpha();
    }

    public static void pushItemAlpha(float alpha) {
        itemAlphaStack.push(alpha);
    }

    public static void popItemAlpha() {
        itemAlphaStack.pop();
    }

    public static float peekItemAlpha() {
        return itemAlphaStack.peek();
    }

    public static boolean hasItemAlpha() {
        return !itemAlphaStack.isEmpty();
    }

    public static void pushFontAlpha(float alpha) {
        fontAlphaStack.push(alpha);
    }

    public static void popFontAlpha() {
        fontAlphaStack.pop();
    }

    public static float peekFontAlpha() {
        return fontAlphaStack.peek();
    }

    public static boolean hasFontAlpha() {
        return !fontAlphaStack.isEmpty();
    }
}