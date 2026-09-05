package com.remarxk.guitween.config;

import com.remarxk.guitween.anim.GUITweenStyle;
import com.remarxk.guitween.util.Ease;

import java.util.ArrayList;
import java.util.List;

public class NeoForgeConfigAdapter implements IGUITweenConfig {
    @Override public boolean enable() { return NeoforgeGUITweenConfig.enable.get(); }
    @Override public boolean enableDebugWindow() { return NeoforgeGUITweenConfig.enableDebugWindow.get(); }

    @Override
    public boolean isEnableDebugWindow() {
        return NeoforgeGUITweenConfig.isEnableDebugWindow();
    }

    @Override
    public GUITweenStyle style() {
        return NeoforgeGUITweenConfig.style.get();
    }

    @Override
    public void applyStylePreset(GUITweenStyle newStyle) {
        NeoforgeGUITweenConfig.applyStylePreset(newStyle);
    }

    @Override public boolean enableWindow() { return NeoforgeGUITweenConfig.window.enable.get(); }
    @Override public boolean enableCloseWindow() { return NeoforgeGUITweenConfig.window.enableCloseWindow.get(); }
    @Override public boolean enableJei() { return NeoforgeGUITweenConfig.window.enableJei.get(); }

    @Override public float windowMoveDuration() { return NeoforgeGUITweenConfig.window.moveDuration.get().floatValue(); }

    @Override
    public Ease windowMoveEase() {
        return NeoforgeGUITweenConfig.window.moveEase.get();
    }

    @Override
    public float windowMoveX() {
        return NeoforgeGUITweenConfig.window.moveX.get().floatValue();
    }

    @Override
    public float windowMoveY() {
        return NeoforgeGUITweenConfig.window.moveY.get().floatValue();
    }

    @Override public float windowGradientDuration() { return NeoforgeGUITweenConfig.window.gradientDuration.get().floatValue(); }

    @Override
    public Ease windowGradientEase() {
        return NeoforgeGUITweenConfig.window.gradientEase.get();
    }

    @Override public float closeMoveDuration() { return NeoforgeGUITweenConfig.window.closeMoveDuration.get().floatValue(); }

    @Override
    public Ease closeMoveEase() {
        return NeoforgeGUITweenConfig.window.closeMoveEase.get();
    }

    @Override
    public float closeMoveX() {
        return NeoforgeGUITweenConfig.window.closeMoveX.get().floatValue();
    }

    @Override
    public float closeMoveY() {
        return NeoforgeGUITweenConfig.window.closeMoveY.get().floatValue();
    }

    @Override public float closeGradientDuration() { return NeoforgeGUITweenConfig.window.closeGradientDuration.get().floatValue(); }

    @Override
    public Ease closeGradientEase() {
        return NeoforgeGUITweenConfig.window.closeGradientEase.get();
    }

    @Override public float jeiMoveDuration() { return NeoforgeGUITweenConfig.window.jeiMoveDuration.get().floatValue(); }

    @Override
    public Ease jeiMoveEase() {
        return NeoforgeGUITweenConfig.window.jeiMoveEase.get();
    }

    @Override
    public float jeiMoveX() {
        return NeoforgeGUITweenConfig.window.jeiMoveX.get().floatValue();
    }

    @Override
    public float jeiMoveY() {
        return NeoforgeGUITweenConfig.window.jeiMoveY.get().floatValue();
    }

    @Override public float closeJeiMoveDuration() { return NeoforgeGUITweenConfig.window.closeJeiMoveDuration.get().floatValue(); }

    @Override
    public Ease closeJeiMoveEase() {
        return NeoforgeGUITweenConfig.window.closeJeiMoveEase.get();
    }

    @Override
    public float closeJeiMoveX() {
        return NeoforgeGUITweenConfig.window.closeJeiMoveX.get().floatValue();
    }

    @Override
    public float closeJeiMoveY() {
        return NeoforgeGUITweenConfig.window.closeJeiMoveY.get().floatValue();
    }

    @Override public List<String> disableNames() { return castStringList(NeoforgeGUITweenConfig.window.disableNames.get()); }

    @Override public boolean enableHover() { return NeoforgeGUITweenConfig.windowItem.enableHover.get(); }

    @Override
    public float hoverDuration() {
        return NeoforgeGUITweenConfig.windowItem.hoverDuration.get().floatValue();
    }

    @Override
    public Ease hoverEase() {
        return NeoforgeGUITweenConfig.windowItem.hoverEase.get();
    }

    @Override
    public float hoverScale() {
        return NeoforgeGUITweenConfig.windowItem.hoverScale.get().floatValue();
    }

    @Override public boolean enableTooltip() { return NeoforgeGUITweenConfig.windowItem.enableTooltip.get(); }

    @Override
    public float tooltipDuration() {
        return NeoforgeGUITweenConfig.windowItem.tooltipDuration.get().floatValue();
    }

    @Override
    public Ease tooltipEase() {
        return NeoforgeGUITweenConfig.windowItem.tooltipEase.get();
    }

    @Override public boolean enableClickItem() { return NeoforgeGUITweenConfig.windowItem.enableClickItem.get(); }

    @Override
    public float clickItemScale() {
        return NeoforgeGUITweenConfig.windowItem.clickItemScale.get().floatValue();
    }

    @Override
    public float clickZoomStrength() {
        return NeoforgeGUITweenConfig.windowItem.clickZoomStrength.get().floatValue();
    }

    @Override
    public float clickItemDuration() {
        return NeoforgeGUITweenConfig.windowItem.clickItemDuration.get().floatValue();
    }

    @Override public boolean enableOutput() { return NeoforgeGUITweenConfig.windowItem.enableOutput.get(); }

    @Override
    public float outputDuration() {
        return NeoforgeGUITweenConfig.windowItem.outputDuration.get().floatValue();
    }

    @Override
    public Ease outputEase() {
        return NeoforgeGUITweenConfig.windowItem.outputEase.get();
    }

    @Override public boolean enableDrag() { return NeoforgeGUITweenConfig.windowItem.enableDrag.get(); }

    @Override
    public float dragMaxAngle() {
        return NeoforgeGUITweenConfig.windowItem.dragMaxAngle.get().floatValue();
    }

    @Override
    public float dragSensitivity() {
        return NeoforgeGUITweenConfig.windowItem.dragSensitivity.get().floatValue();
    }

    @Override public boolean enableSameItem() { return NeoforgeGUITweenConfig.windowItem.enableSameItem.get(); }
    @Override public boolean enableQuick() { return NeoforgeGUITweenConfig.windowItem.enableQuick.get(); }
    @Override public boolean enableMove() { return NeoforgeGUITweenConfig.windowItem.enableMove.get(); }
    @Override public boolean enableFinish() { return NeoforgeGUITweenConfig.windowItem.enableFinish.get(); }
    @Override public boolean enablePickup() { return NeoforgeGUITweenConfig.windowItem.enablePickup.get(); }

    @Override public float moveDuration() { return NeoforgeGUITweenConfig.windowItem.moveDuration.get().floatValue(); }

    @Override
    public Ease moveEase() {
        return NeoforgeGUITweenConfig.windowItem.moveEase.get();
    }

    @Override public float finishPunchStrength() { return NeoforgeGUITweenConfig.windowItem.finishPunchStrength.get().floatValue(); }

    @Override public float finishDuration() { return NeoforgeGUITweenConfig.windowItem.finishDuration.get().floatValue(); }

    @Override public float pickupDuration() { return NeoforgeGUITweenConfig.windowItem.pickupDuration.get().floatValue(); }

    @Override
    public Ease pickupEase() {
        return NeoforgeGUITweenConfig.windowItem.pickupEase.get();
    }

    @Override public float quickCraftDuration() { return NeoforgeGUITweenConfig.windowItem.quickCraftDuration.get().floatValue(); }

    @Override public float sameItemDelay() { return NeoforgeGUITweenConfig.windowItem.sameItemDelay.get().floatValue(); }

    @Override
    public float sameItemShakeStrength() {
        return NeoforgeGUITweenConfig.windowItem.sameItemShakeStrength.get().floatValue();
    }

    @Override public float sameItemShakeDuration() { return NeoforgeGUITweenConfig.windowItem.sameItemShakeDuration.get().floatValue(); }

    @Override
    public float sameItemShakeFrequency() {
        return NeoforgeGUITweenConfig.windowItem.sameItemShakeFrequency.get().floatValue();
    }

    @Override public float sameItemShakeWaitDuration() { return NeoforgeGUITweenConfig.windowItem.sameItemShakeWaitDuration.get().floatValue(); }

    @Override public boolean enableHoldItem() { return NeoforgeGUITweenConfig.hotbar.enableHoldItem.get(); }

    @Override
    public boolean enableHoldZoomTransition() {
        return NeoforgeGUITweenConfig.hotbar.enableHoldZoomTransition.get();
    }

    @Override public float holdZoomInDuration() { return NeoforgeGUITweenConfig.hotbar.holdZoomInDuration.get().floatValue(); }

    @Override
    public Ease holdZoomInEase() {
        return NeoforgeGUITweenConfig.hotbar.holdZoomInEase.get();
    }

    @Override
    public float holdZoomScale() {
        return NeoforgeGUITweenConfig.hotbar.holdZoomScale.get().floatValue();
    }

    @Override public float holdZoomOutDuration() { return NeoforgeGUITweenConfig.hotbar.holdZoomOutDuration.get().floatValue(); }

    @Override
    public Ease holdZoomOutEase() {
        return NeoforgeGUITweenConfig.hotbar.holdZoomOutEase.get();
    }

    @Override public boolean enableSelectedItemName() { return NeoforgeGUITweenConfig.hotbar.enableSelectedItemName.get(); }

    @Override public float selectedItemNameMoveDuration() { return NeoforgeGUITweenConfig.hotbar.selectedItemNameMoveDuration.get().floatValue(); }

    @Override
    public float selectedItemNameMoveY() {
        return NeoforgeGUITweenConfig.hotbar.selectedItemNameMoveY.get().floatValue();
    }

    @Override
    public Ease selectedItemNameMoveEase() {
        return NeoforgeGUITweenConfig.hotbar.selectedItemNameMoveEase.get();
    }

    @Override public float selectedItemNameAlphaDuration() { return NeoforgeGUITweenConfig.hotbar.selectedItemNameAlphaDuration.get().floatValue(); }

    @Override
    public Ease selectedItemNameAlphaEase() {
        return NeoforgeGUITweenConfig.hotbar.selectedItemNameAlphaEase.get();
    }

    @Override public boolean enableAttack() { return NeoforgeGUITweenConfig.hotbar.enableAttack.get(); }

    @Override
    public float attackMaxAngle() {
        return NeoforgeGUITweenConfig.hotbar.attackMaxAngle.get().floatValue();
    }

    @Override public boolean enableUse() { return NeoforgeGUITweenConfig.hotbar.enableUse.get(); }

    @Override
    public float useStrength() {
        return NeoforgeGUITweenConfig.hotbar.useStrength.get().floatValue();
    }

    @Override public boolean enableLack() { return NeoforgeGUITweenConfig.hotbar.enableLack.get(); }

    @Override
    public float lackDuration() {
        return NeoforgeGUITweenConfig.hotbar.lackDuration.get().floatValue();
    }

    @Override
    public float lackShakeStrength() {
        return NeoforgeGUITweenConfig.hotbar.lackShakeStrength.get().floatValue();
    }

    @Override public boolean enableSelectMove() { return NeoforgeGUITweenConfig.hotbar.enableSelectMove.get(); }

    @Override
    public float selectMoveSpeed() {
        return NeoforgeGUITweenConfig.hotbar.selectMoveSpeed.get().floatValue();
    }

    @Override public boolean enableExp() { return NeoforgeGUITweenConfig.hotbar.enableExp.get(); }

    @Override
    public float expDuration() {
        return NeoforgeGUITweenConfig.hotbar.expDuration.get().floatValue();
    }

    @Override
    public Ease expEase() {
        return NeoforgeGUITweenConfig.hotbar.expEase.get();
    }

    @Override
    public float expScale() {
        return NeoforgeGUITweenConfig.hotbar.expScale.get().floatValue();
    }

    @Override public boolean enableArmor() { return NeoforgeGUITweenConfig.hotbar.enableArmor.get(); }

    @Override
    public float armorDuration() {
        return NeoforgeGUITweenConfig.hotbar.armorDuration.get().floatValue();
    }

    @Override
    public float upArmorScale() {
        return NeoforgeGUITweenConfig.hotbar.upArmorScale.get().floatValue();
    }

    @Override
    public Ease upArmorEase() {
        return NeoforgeGUITweenConfig.hotbar.upArmorEase.get();
    }

    @Override
    public float downArmorShakeStrength() {
        return NeoforgeGUITweenConfig.hotbar.downArmorShakeStrength.get().floatValue();
    }

    @Override public boolean enableChat() { return NeoforgeGUITweenConfig.chat.enableChat.get(); }
    @Override public boolean enableCloseChat() { return NeoforgeGUITweenConfig.chat.enableCloseChat.get(); }
    @Override public boolean enableChatComp() { return NeoforgeGUITweenConfig.chat.enableChatComp.get(); }

    @Override public float chatOpenMoveDuration() { return NeoforgeGUITweenConfig.chat.openMoveDuration.get().floatValue(); }

    @Override
    public Ease chatOpenMoveEase() {
        return NeoforgeGUITweenConfig.chat.openMoveEase.get();
    }

    @Override public float chatOpenGradientDuration() { return NeoforgeGUITweenConfig.chat.openGradientDuration.get().floatValue(); }

    @Override
    public Ease chatOpenGradientEase() {
        return NeoforgeGUITweenConfig.chat.openGradientEase.get();
    }

    @Override
    public float closeChatSpeed() {
        return NeoforgeGUITweenConfig.chat.closeChatSpeed.get().floatValue();
    }

    @Override public float chatCompMoveDuration() { return NeoforgeGUITweenConfig.chat.compMoveDuration.get().floatValue(); }

    @Override
    public Ease chatCompMoveEase() {
        return NeoforgeGUITweenConfig.chat.compMoveEase.get();
    }

    @Override public float chatCompGradientDuration() { return NeoforgeGUITweenConfig.chat.compGradientDuration.get().floatValue(); }

    @Override
    public Ease chatCompGradientEase() {
        return NeoforgeGUITweenConfig.chat.compGradientEase.get();
    }

    @Override
    public boolean enableBossShow() {
        return NeoforgeGUITweenConfig.boss.enableBossShow.get();
    }

    @Override
    public float bossShowDuration() {
        return NeoforgeGUITweenConfig.boss.bossShowDuration.get().floatValue();
    }

    @Override
    public Ease bossShowEase() {
        return NeoforgeGUITweenConfig.boss.bossShowEase.get();
    }

    @Override
    public float bossShowFadeDuration() {
        return NeoforgeGUITweenConfig.boss.bossShowFadeDuration.get().floatValue();
    }

    @Override
    public Ease bossShowFadeEase() {
        return NeoforgeGUITweenConfig.boss.bossShowFadeEase.get();
    }

    @Override
    public boolean enableBossHide() {
        return NeoforgeGUITweenConfig.boss.enableBossHide.get();
    }

    @Override
    public float bossHideDuration() {
        return NeoforgeGUITweenConfig.boss.bossHideDuration.get().floatValue();
    }

    @Override
    public Ease bossHideEase() {
        return NeoforgeGUITweenConfig.boss.bossHideEase.get();
    }

    @Override
    public float bossHideFadeDuration() {
        return NeoforgeGUITweenConfig.boss.bossHideFadeDuration.get().floatValue();
    }

    @Override
    public Ease bossHideFadeEase() {
        return NeoforgeGUITweenConfig.boss.bossHideFadeEase.get();
    }

    @Override
    public boolean enableBossHurt() {
        return NeoforgeGUITweenConfig.boss.enableBossHurt.get();
    }

    @Override
    public float bossHurtShakeStrength() {
        return NeoforgeGUITweenConfig.boss.bossHurtShakeStrength.get().floatValue();
    }

    @Override
    public float bossHurtDuration() {
        return NeoforgeGUITweenConfig.boss.bossHurtDuration.get().floatValue();
    }

    @SuppressWarnings("unchecked")
    public static List<String> castStringList(List<?> list) {
        // 快速路径：空 or 已经是 String
        if (list.isEmpty() || list.get(0) instanceof String) {
            return (List<String>) list;
        }

        // 慢路径：逐个转换（防止类型污染）
        List<String> result = new ArrayList<>(list.size());
        for (Object o : list) {
            result.add(String.valueOf(o));
        }
        return result;
    }
}