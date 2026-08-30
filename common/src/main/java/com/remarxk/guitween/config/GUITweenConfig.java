package com.remarxk.guitween.config;

import com.remarxk.guitween.anim.GUITweenStyle;
import com.remarxk.guitween.compat.SmoothSwappingCompat;
import com.remarxk.guitween.util.Ease;
import java.util.List;

public final class GUITweenConfig {

    private static IGUITweenConfig DELEGATE;

    private GUITweenConfig() {}

    public static void setConfig(IGUITweenConfig config) {
        if (config != null) {
            DELEGATE = config;
        }
    }

    private static IGUITweenConfig cfg() {
        if (DELEGATE == null) {
            throw new IllegalStateException("GUITweenConfig not initialized");
        }
        return DELEGATE;
    }

    private static GUITweenStyle lastAppliedStyle;

    public static void checkStyleUpdate() {
        GUITweenStyle currentStyle = style();

        if (lastAppliedStyle == null) {
            lastAppliedStyle = currentStyle;
            return;
        }

        if (currentStyle != lastAppliedStyle) {
            lastAppliedStyle = currentStyle;
            applyStylePreset(currentStyle);
        }
    }

    public static void applyStylePreset(GUITweenStyle newStyle) {
        cfg().applyStylePreset(newStyle);
    }

    // ===== root =====
    public static boolean enable() { return cfg().enable(); }
    public static boolean enableDebugWindow() { return cfg().enableDebugWindow(); }
    public static boolean isEnableDebugWindow() {
        return cfg().isEnableDebugWindow();
    }
    public static GUITweenStyle style() { return cfg().style(); }

    // ===== window =====
    public static boolean enableWindow() { return cfg().enableWindow(); }
    public static boolean enableCloseWindow() { return cfg().enableCloseWindow(); }
    public static boolean enableJei() { return cfg().enableJei(); }

    public static boolean isEnableWindow() { return cfg().isEnableWindow(); }
    public static boolean isEnableCloseWindow() { return cfg().isEnableCloseWindow(); }
    public static boolean isEnableJei() { return cfg().isEnableJei(); }

    public static float windowMoveDuration() { return cfg().windowMoveDuration(); }
    public static Ease windowMoveEase() { return cfg().windowMoveEase(); }
    public static float windowMoveX() { return cfg().windowMoveX(); }
    public static float windowMoveY() { return cfg().windowMoveY(); }
    public static float windowGradientDuration() { return cfg().windowGradientDuration(); }
    public static Ease windowGradientEase() { return cfg().windowGradientEase(); }
    public static float closeWindowSpeed() { return cfg().closeWindowSpeed(); }
    public static float jeiLeftMoveDuration() { return cfg().jeiLeftMoveDuration(); }
    public static Ease jeiLeftMoveEase() { return cfg().jeiLeftMoveEase(); }
    public static float jeiLeftMoveX() { return cfg().jeiLeftMoveX(); }
    public static float jeiLeftMoveY() { return cfg().jeiLeftMoveY(); }
    public static float jeiRightMoveDuration() { return cfg().jeiRightMoveDuration(); }
    public static Ease jeiRightMoveEase() { return cfg().jeiRightMoveEase(); }
    public static float jeiRightMoveX() { return cfg().jeiRightMoveX(); }
    public static float jeiRightMoveY() { return cfg().jeiRightMoveY(); }
    public static List<String> disableNames() { return cfg().disableNames(); }
    public static boolean isDisableTweenWindow(String name) { return cfg().isDisableTweenWindow(name); }
    public static float getWindowTotalDuration() { return cfg().getWindowTotalDuration(); }
    public static float getJeiTotalDuration() { return cfg().getJeiTotalDuration(); }

    // ===== screen item =====
    public static boolean enableHover() { return cfg().enableHover(); }
    public static boolean enableTooltip() { return cfg().enableTooltip(); }
    public static boolean enableClickItem() { return cfg().enableClickItem(); }
    public static boolean enableOutput() { return cfg().enableOutput(); }
    public static boolean enableDrag() { return cfg().enableDrag(); }
    public static boolean enableSameItem() { return cfg().enableSameItem(); }
    public static boolean enableQuick() { return cfg().enableQuick(); }
    public static boolean enableMove() { return cfg().enableMove(); }
    public static boolean enableFinish() { return cfg().enableFinish(); }
    public static boolean enablePickup() { return cfg().enablePickup(); }

    public static boolean isEnableHoverItem() { return cfg().isEnableHoverItem(); }
    public static boolean isEnableTooltip() { return cfg().isEnableTooltip(); }
    public static boolean isEnableClickItem() { return cfg().isEnableClickItem(); }
    public static boolean isEnableOutput() { return cfg().isEnableOutput(); }
    public static boolean isEnableDragItem() { return cfg().isEnableDragItem(); }
    public static boolean isEnableSameItem() { return cfg().isEnableSameItem(); }
    public static boolean isEnableQuickCraft() { return cfg().isEnableQuickCraft(); }
    public static boolean isEnableMoveItem() { return cfg().isEnableMoveItem() && !SmoothSwappingCompat.isLoaded; }
    public static boolean isEnableFinishItem() { return cfg().isEnableFinishItem(); }
    public static boolean isEnablePickupItem() { return cfg().isEnablePickupItem(); }

    public static float hoverDuration() { return cfg().hoverDuration(); }
    public static Ease hoverEase() { return cfg().hoverEase(); }
    public static float hoverScale() { return cfg().hoverScale(); }

    public static float tooltipDuration() { return cfg().tooltipDuration(); }
    public static Ease tooltipEase() { return cfg().tooltipEase(); }

    public static float clickItemScale() { return cfg().clickItemScale(); }
    public static float clickZoomStrength() { return cfg().clickZoomStrength(); }
    public static float clickItemDuration() { return cfg().clickItemDuration(); }

    public static float outputDuration() { return cfg().outputDuration(); }
    public static Ease outputEase() { return cfg().outputEase(); }

    public static float dragMaxAngle() { return cfg().dragMaxAngle(); }
    public static float dragSensitivity() { return cfg().dragSensitivity(); }

    public static float sameItemDelay() { return cfg().sameItemDelay(); }
    public static float sameItemShakeStrength() { return cfg().sameItemShakeStrength(); }
    public static float sameItemShakeDuration() { return cfg().sameItemShakeDuration(); }
    public static float sameItemShakeFrequency() { return cfg().sameItemShakeFrequency(); }
    public static float sameItemShakeWaitDuration() { return cfg().sameItemShakeWaitDuration(); }
    public static float getSameItemTotalDuration() { return cfg().getSameItemTotalDuration(); }

    public static float moveDuration() { return cfg().moveDuration(); }
    public static Ease moveEase() { return cfg().moveEase(); }
    public static float finishPunchStrength() { return cfg().finishPunchStrength(); }
    public static float finishDuration() { return cfg().finishDuration(); }
    public static float pickupDuration() { return cfg().pickupDuration(); }
    public static Ease pickupEase() { return cfg().pickupEase(); }
    public static float quickCraftDuration() { return cfg().quickCraftDuration(); }

    // ===== hotbar =====
    public static boolean enableHoldItem() { return cfg().enableHoldItem(); }
    public static boolean enableHoldZoomTransition() { return cfg().enableHoldZoomTransition(); }
    public static boolean isEnableHoldItem() { return cfg().isEnableHoldItem(); }
    public static float holdZoomInDuration() { return cfg().holdZoomInDuration(); }
    public static Ease holdZoomInEase() { return cfg().holdZoomInEase(); }
    public static float holdZoomScale() { return cfg().holdZoomScale(); }
    public static float holdZoomOutDuration() { return cfg().holdZoomOutDuration(); }
    public static Ease holdZoomOutEase() { return cfg().holdZoomOutEase(); }
    public static float getHoldItemTotalDuration(){
        return cfg().getHoldItemTotalDuration();
    }

    public static boolean enableSelectedItemName() { return cfg().enableSelectedItemName(); }
    public static boolean isEnableSelectedItemName() { return cfg().isEnableSelectedItemName(); }
    public static float selectedItemNameMoveDuration() { return cfg().selectedItemNameMoveDuration(); }
    public static float selectedItemNameMoveY() { return cfg().selectedItemNameMoveY(); }
    public static Ease selectedItemNameMoveEase() { return cfg().selectedItemNameMoveEase(); }
    public static float selectedItemNameAlphaDuration() { return cfg().selectedItemNameAlphaDuration(); }
    public static Ease selectedItemNameAlphaEase() { return cfg().selectedItemNameAlphaEase(); }
    public static float getSelectedItemNameDuration() { return cfg().getSelectedItemNameDuration(); }

    public static boolean enableAttack() { return cfg().enableAttack(); }
    public static boolean isEnableAttack() { return cfg().isEnableAttack(); }
    public static float attackMaxAngle() { return cfg().attackMaxAngle(); }

    public static boolean enableUse() { return cfg().enableUse(); }
    public static boolean isEnableUse() { return cfg().isEnableUse(); }
    public static float useStrength() { return cfg().useStrength(); }

    public static boolean enableLack() { return cfg().enableLack(); }
    public static boolean isEnableLack() { return cfg().isEnableLack(); }
    public static float lackDuration() { return cfg().lackDuration(); }
    public static float lackShakeStrength() { return cfg().lackShakeStrength(); }

    public static boolean enableSelectMove() { return cfg().enableSelectMove(); }
    public static boolean isEnableSelectMove() { return cfg().isEnableSelectMove(); }
    public static float selectMoveSpeed() { return cfg().selectMoveSpeed(); }

    public static boolean enableExp() { return cfg().enableExp(); }
    public static boolean isEnableExp() { return cfg().isEnableExp(); }
    public static float expDuration() { return cfg().expDuration(); }
    public static Ease expEase() { return cfg().expEase(); }
    public static float expScale() { return cfg().expScale(); }

    public static boolean enableArmor() { return cfg().enableArmor(); }
    public static boolean isEnableArmor() { return cfg().isEnableArmor(); }
    public static float armorDuration() { return cfg().armorDuration(); }
    public static float upArmorScale() { return cfg().upArmorScale(); }
    public static Ease upArmorEase() { return cfg().upArmorEase(); }
    public static float downArmorShakeStrength() { return cfg().downArmorShakeStrength(); }

    // ===== chat =====
    public static boolean enableChat() { return cfg().enableChat(); }
    public static boolean isEnableChat() { return cfg().isEnableChat(); }
    public static boolean enableCloseChat() { return cfg().enableCloseChat(); }
    public static boolean isEnableCloseChat() { return cfg().isEnableCloseChat(); }
    public static boolean enableChatComp() { return cfg().enableChatComp(); }
    public static boolean isEnableChatComp() { return cfg().isEnableChatComp(); }

    public static float chatOpenMoveDuration() { return cfg().chatOpenMoveDuration(); }
    public static Ease chatOpenMoveEase() { return cfg().chatOpenMoveEase(); }
    public static float chatOpenGradientDuration() { return cfg().chatOpenGradientDuration(); }
    public static Ease chatOpenGradientEase() { return cfg().chatOpenGradientEase(); }
    public static float closeChatSpeed() { return cfg().closeChatSpeed(); }
    public static float chatCompMoveDuration() { return cfg().chatCompMoveDuration(); }
    public static Ease chatCompMoveEase() { return cfg().chatCompMoveEase(); }
    public static float chatCompGradientDuration() { return cfg().chatCompGradientDuration(); }
    public static Ease chatCompGradientEase() { return cfg().chatCompGradientEase(); }

    public static float getChatOpenMaxDuration() { return cfg().getChatOpenMaxDuration(); }
    public static float getChatCompMaxDuration() { return cfg().getChatCompMaxDuration(); }

    public static boolean isEnableBossShow() {
        return cfg().isEnableBossShow();
    }

    public static boolean enableBossShow() {
        return cfg().enableBossShow();
    }

    public static float bossShowDuration() {
        return cfg().bossShowDuration();
    }

    public static Ease bossShowEase() {
        return cfg().bossShowEase();
    }

    public static float bossShowFadeDuration() {
        return cfg().bossShowFadeDuration();
    }

    public static Ease bossShowFadeEase() {
        return cfg().bossShowEase();
    }

    public static boolean isEnableBossHide() {
        return cfg().isEnableBossHide();
    }

    public static boolean enableBossHide() {
        return cfg().enableBossHide();
    }

    public static float bossHideDuration() {
        return cfg().bossHideDuration();
    }

    public static Ease bossHideEase() {
        return cfg().bossHideEase();
    }

    public static float bossHideFadeDuration() {
        return cfg().bossHideFadeDuration();
    }

    public static Ease bossHideFadeEase() {
        return cfg().bossHideFadeEase();
    }

    public static boolean isEnableBossHurt() {
        return cfg().isEnableBossHurt();
    }

    public static boolean enableBossHurt() {
        return cfg().enableBossHurt();
    }

    public static float bossHurtShakeStrength() {
        return cfg().bossHurtShakeStrength();
    }

    public static float bossHurtDuration() {
        return cfg().bossHurtDuration();
    }

    public static float getBossShowMaxDuration() {
        return cfg().getBossShowMaxDuration();
    }

    public static float getBossHideMaxDuration() {
        return cfg().getBossHideMaxDuration();
    }
}