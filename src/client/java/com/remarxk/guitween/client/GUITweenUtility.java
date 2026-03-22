package com.remarxk.guitween.client;

import com.remarxk.guitween.client.anim.AttackTween;
import com.remarxk.guitween.client.anim.DragTween;
import com.remarxk.guitween.client.anim.UseTween;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.state.TexturedQuadGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.EntityGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import org.joml.Matrix3x2f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class GUITweenUtility {
    public interface CompatWindowCheck {
        public boolean isCompatWindow(Class<?> screenClass);
    }

    public static final float fFontMinAlpha = 0;
    public static final int iFontMinAlpha = 0;

    private static final List<CompatWindowCheck> COMPAT_WINDOW = new ArrayList<>();

    public static String openScreenName;
    public static float openScreenTick;

    public static boolean inTooltipTween;
    public static float tooltipTweenTick;

    private static final Stack<Float> itemAlphaStack = new Stack<>();
    private static final Stack<Float> fontAlphaStack = new Stack<>();
    private static final Stack<Float> spriteAlphaStack = new Stack<>();

    private final static AttackTween attackTween = new AttackTween();
    private final static UseTween usingTween = new UseTween();
    private final static DragTween dragTween = new DragTween();

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

    public static float getDeltaTicks() {
        return MinecraftClient.getInstance().getRenderTickCounter().getDynamicDeltaTicks();
    }

    public static void setOpenScreen(String screenName, float tick) {
        openScreenName = screenName;
        openScreenTick = tick;
    }

    public static void deleteOpenScreen() {
        openScreenName = null;
        openScreenTick = 0;
    }

    public static void startTooltipTween(float tick) {
        inTooltipTween = true;
        tooltipTweenTick = tick;
    }

    public static void endTooltipTween() {
        inTooltipTween = false;
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

    public static void pushSpriteAlpha(float alpha) {
        spriteAlphaStack.push(alpha);
    }

    public static void popSpriteAlpha() {
        spriteAlphaStack.pop();
    }

    public static float peekSpriteAlpha() {
        return spriteAlphaStack.peek();
    }

    public static boolean hasSpriteAlpha() {
        return !spriteAlphaStack.isEmpty();
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

    private final static HashMap<SpecialGuiElementRenderState, Matrix3x2f> pictureMatrix = new HashMap<>();

    public static void pushPictureMatrix(SpecialGuiElementRenderState renderState, Matrix3x2f matrix) {
        pictureMatrix.put(renderState, matrix);
    }

    public static Matrix3x2f popPictureMatrix(SpecialGuiElementRenderState renderState) {
        return pictureMatrix.remove(renderState);
    }
}