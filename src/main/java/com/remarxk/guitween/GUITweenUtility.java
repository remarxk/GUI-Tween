package com.remarxk.guitween;

import com.remarxk.guitween.anim.AttackTween;
import com.remarxk.guitween.anim.DragTween;
import com.remarxk.guitween.anim.UseTween;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;

import java.util.HashSet;
import java.util.Stack;

public class GUITweenUtility {
    public static final float fFontMinAlpha = 0.02f;
    public static final int iFontMinAlpha = 5;

    public static final HashSet<Class<?>> WINDOW_DELAY_TICK = new HashSet<>();
    public static final HashSet<Class<?>> COMPAT_WINDOW = new HashSet<>();

    public static String openScreenName;
    public static float openScreenTick;

    private static final Stack<Float> itemAlphaStack = new Stack<>();
    private static final Stack<Float> fontAlphaStack = new Stack<>();

    private final static AttackTween attackTween = new AttackTween();
    private final static UseTween usingTween = new UseTween();
    private final static DragTween dragTween = new DragTween();

    static {
        WINDOW_DELAY_TICK.add(MerchantScreen.class);
    }

    public static float getDeltaTicks() {
        return Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
    }

    public static void setOpenScreen(String screenName, float tick) {
        openScreenName = screenName;
        openScreenTick = tick;
    }

    public static void deleteOpenScreen() {
        openScreenName = null;
        openScreenTick = 0;
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

    public static AttackTween getAttackTween() {
        return attackTween;
    }

    public static UseTween getUsingTween() {
        return usingTween;
    }

    public static DragTween getDragTween() {
        return dragTween;
    }
}