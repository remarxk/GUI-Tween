package com.remarxk.guitween;

import com.remarxk.guitween.anim.*;
import com.remarxk.guitween.config.GUITweenConfig;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GUITweenUtility {
    public interface CompatWindowCheck {
        public boolean isCompatWindow(Class<?> screenClass);
    }

    public static final float fFontMinAlpha = 0.02f;
    public static final int iFontMinAlpha = 5;

    private static final List<CompatWindowCheck> COMPAT_WINDOW = new ArrayList<>();

    public static String openScreenName;
    public static float openScreenTick;
    public static float jeiOpenTick;

    /** 是否正处于窗口关闭动画阶段（与 openScreenTick/jeiOpenTick 完全独立的计时） */
    public static boolean isWindowClosing;
    public static float closeScreenTick;
    public static float closeJeiTick;

    public static boolean inDragging;

    private static final Stack<Float> itemAlphaStack = new Stack<>();
    private static final Stack<Float> fontAlphaStack = new Stack<>();

    private final static AttackTween attackTween = new AttackTween();
    private final static UseTween usingTween = new UseTween();
    private final static DragTween dragTween = new DragTween();
    private final static ContainerItemTween CONTAINER_ITEM_TWEEN = new ContainerItemTween();

    public static float getDeltaTicks() {
        return Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
    }

    public static void addCompatWindow(CompatWindowCheck check) {
        COMPAT_WINDOW.add(check);
    }

    public static boolean isCompatWindow(Class<?> screenClass) {
        for (var check : COMPAT_WINDOW) {
            if (check.isCompatWindow(screenClass))
                return true;
        }

        return false;
    }

    public static void setOpenScreen(String screenName, float tick) {
        openScreenName = screenName;
        openScreenTick = tick;
    }

    public static void deleteOpenScreen() {
        openScreenName = null;
        openScreenTick = 0;
        jeiOpenTick = 0;
        endCloseWindowTween();
    }

    /**
     * 开始独立的窗口关闭动画计时。
     * 关闭动画从“居中、完全可见”的位置开始，向各自独立的偏移量运动，
     * 因此这里不再复用 openScreenTick/jeiOpenTick，也不再有“关闭速度”概念。
     */
    public static void startCloseWindowTween() {
        isWindowClosing = true;
        closeScreenTick = GUITweenConfig.getCloseWindowTotalDuration();
        closeJeiTick = GUITweenConfig.getCloseJeiTotalDuration();
    }

    public static void endCloseWindowTween() {
        isWindowClosing = false;
        closeScreenTick = 0;
        closeJeiTick = 0;
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

    public static ContainerItemTween getMoveItemTween() {return CONTAINER_ITEM_TWEEN;}
}