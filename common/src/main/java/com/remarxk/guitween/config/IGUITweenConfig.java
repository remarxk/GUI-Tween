package com.remarxk.guitween.config;

import com.remarxk.guitween.anim.GUITweenStyle;
import com.remarxk.guitween.util.Ease;

import java.util.List;

public interface IGUITweenConfig {

    // ===== root =====
    boolean enable();
    boolean enableDebugWindow();
    boolean isEnableDebugWindow();

    GUITweenStyle style();

    void applyStylePreset(GUITweenStyle newStyle);

    // ===== window =====
    boolean enableWindow();
    List<String> disableNames();

    float windowMoveDuration();
    Ease windowMoveEase();
    float windowMoveX();
    float windowMoveY();

    float windowGradientDuration();
    Ease windowGradientEase();

    boolean enableCloseWindow();
    float closeWindowSpeed();

    boolean enableJei();
    float jeiLeftMoveDuration();
    Ease jeiLeftMoveEase();
    float jeiLeftMoveX();
    float jeiLeftMoveY();

    float jeiRightMoveDuration();
    Ease jeiRightMoveEase();
    float jeiRightMoveX();
    float jeiRightMoveY();

    // ===== screen item =====
    boolean enableHover();
    float hoverDuration();
    Ease hoverEase();
    float hoverScale();

    boolean enableTooltip();
    float tooltipDuration();
    Ease tooltipEase();

    boolean enableClickItem();
    float clickItemScale();
    float clickZoomStrength();
    float clickItemDuration();

    boolean enableOutput();
    float outputDuration();
    Ease outputEase();

    boolean enableDrag();
    float dragMaxAngle();
    float dragSensitivity();

    boolean enableSameItem();
    float sameItemDelay();
    float sameItemShakeStrength();
    float sameItemShakeDuration();
    float sameItemShakeFrequency();
    float sameItemShakeWaitDuration();

    boolean enableQuick();
    boolean enableMove();
    float moveDuration();
    Ease moveEase();
    boolean enableFinish();
    float finishPunchStrength();
    float finishDuration();
    boolean enablePickup();
    float pickupDuration();
    Ease pickupEase();
    float quickCraftDuration();

    // ===== hotbar =====
    boolean enableHoldItem();
    boolean enableHoldZoomTransition();

    float holdZoomInDuration();
    Ease holdZoomInEase();
    float holdZoomScale();

    float holdZoomOutDuration();
    Ease holdZoomOutEase();

    boolean enableSelectedItemName();
    float selectedItemNameMoveDuration();
    float selectedItemNameMoveY();
    Ease selectedItemNameMoveEase();

    float selectedItemNameAlphaDuration();
    Ease selectedItemNameAlphaEase();

    boolean enableAttack();
    float attackMaxAngle();

    boolean enableUse();
    float useStrength();

    boolean enableLack();
    float lackDuration();
    float lackShakeStrength();

    boolean enableSelectMove();
    float selectMoveSpeed();

    boolean enableExp();
    float expDuration();
    Ease expEase();
    float expScale();

    boolean enableArmor();
    float armorDuration();
    float upArmorScale();
    Ease upArmorEase();
    float downArmorShakeStrength();

    // ===== chat =====
    boolean enableChat();

    float chatOpenMoveDuration();
    Ease chatOpenMoveEase();
    float chatOpenGradientDuration();
    Ease chatOpenGradientEase();

    boolean enableCloseChat();
    float closeChatSpeed();

    boolean enableChatComp();
    float chatCompMoveDuration();
    Ease chatCompMoveEase();
    float chatCompGradientDuration();
    Ease chatCompGradientEase();

    // ===== boss =====
    boolean enableBossShow();
    float bossShowDuration();
    Ease bossShowEase();
    float bossShowFadeDuration();
    Ease bossShowFadeEase();

    boolean enableBossHide();
    float bossHideDuration();
    Ease bossHideEase();
    float bossHideFadeDuration();
    Ease bossHideFadeEase();

    boolean enableBossHurt();
    float bossHurtShakeStrength();
    float bossHurtDuration();

    // ===== 默认逻辑（复用 Fabric）=====
    default float getWindowTotalDuration() {
        return Math.max(windowMoveDuration(), windowGradientDuration());
    }

    default float getJeiTotalDuration() {
        return Math.max(jeiLeftMoveDuration(), jeiRightMoveDuration());
    }

    default float getHoldItemTotalDuration() {
        return holdZoomInDuration() + holdZoomOutDuration();
    }

    default float getSameItemTotalDuration() {
        return sameItemDelay() + sameItemShakeDuration() + sameItemShakeWaitDuration();
    }

    default float getSelectedItemNameDuration() {
        return Math.max(selectedItemNameMoveDuration(), selectedItemNameAlphaDuration());
    }

    default float getChatOpenMaxDuration() {
        return Math.max(chatOpenMoveDuration(), chatOpenGradientDuration());
    }

    default float getChatCompMaxDuration() {
        return Math.max(chatCompMoveDuration(), chatCompGradientDuration());
    }

    default float getBossShowMaxDuration() {
        return Math.max(bossShowDuration(), bossShowFadeDuration());
    }

    default float getBossHideMaxDuration() {
        return Math.max(bossHideDuration(), bossHideFadeDuration());
    }

    default boolean isDisableTweenWindow(String name) {
        return disableNames().contains(name);
    }

    // ===== enable 聚合 =====
    default boolean isEnableWindow() {
        return enable() && enableWindow();
    }

    default boolean isEnableCloseWindow() {
        return enable() && enableCloseWindow();
    }

    default boolean isEnableJei() {
        return enable() && enableJei();
    }

    default boolean isEnableHoverItem() {
        return enable() && enableHover();
    }

    default boolean isEnableTooltip() {
        return enable() && enableTooltip();
    }

    default boolean isEnableClickItem() {
        return enable() && enableClickItem();
    }

    default boolean isEnableOutput() {
        return enable() && enableOutput();
    }

    default boolean isEnableDragItem() {
        return enable() && enableDrag();
    }

    default boolean isEnableSameItem() {
        return enable() && enableSameItem();
    }

    default boolean isEnableQuickCraft() {
        return enable() && enableQuick();
    }

    default boolean isEnableMoveItem() {
        return enable() && enableMove();
    }

    default boolean isEnableFinishItem() {
        return enable() && enableFinish();
    }

    default boolean isEnablePickupItem() {
        return enable() && enablePickup();
    }

    default boolean isEnableHoldItem() {
        return enable() && enableHoldItem();
    }

    default boolean isEnableAttack() {
        return enable() && enableAttack();
    }

    default boolean isEnableUse() {
        return enable() && enableUse();
    }

    default boolean isEnableLack() {
        return enable() && enableLack();
    }

    default boolean isEnableSelectMove() {
        return enable() && enableSelectMove();
    }

    default boolean isEnableSelectedItemName() {
        return enable() && enableSelectedItemName();
    }

    default boolean isEnableExp() {
        return enable() && enableExp();
    }

    default boolean isEnableArmor() {
        return enable() && enableArmor();
    }

    default boolean isEnableChat() {
        return enable() && enableChat();
    }

    default boolean isEnableCloseChat() {
        return enable() && enableCloseChat();
    }

    default boolean isEnableChatComp() {
        return enable() && enableChatComp();
    }

    default boolean isEnableBossShow() {
        return enable() && enableBossShow();
    }

    default boolean isEnableBossHide() {
        return enable() && enableBossHide();
    }

    default boolean isEnableBossHurt() {
        return enable() && enableBossHurt();
    }
}