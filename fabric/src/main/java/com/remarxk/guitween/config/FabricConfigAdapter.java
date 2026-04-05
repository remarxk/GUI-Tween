package com.remarxk.guitween.config;

import com.remarxk.guitween.util.Ease;

import java.util.List;

public class FabricConfigAdapter implements IGUITweenConfig {

    private final FabricGUITweenConfig c;

    public FabricConfigAdapter(FabricGUITweenConfig config) {
        this.c = config;
    }

    @Override public boolean enable() { return c.enable; }
    @Override public boolean enableDebugWindow() { return c.enableDebugWindow; }

    @Override
    public boolean isEnableDebugWindow() {
        return c.isEnableDebugWindow();
    }

    // ===== window =====
    @Override public boolean enableWindow() { return c.enableWindow; }
    @Override public boolean enableCloseWindow() { return c.enableCloseWindow; }
    @Override public boolean enableJeiLeft() { return c.enableJeiLeft; }
    @Override public boolean enableJeiRight() { return c.enableJeiRight; }

    @Override public float windowMoveDuration() { return c.windowMoveDuration; }
    @Override public Ease windowMoveEase() { return c.windowMoveEase.get(); }
    @Override public float windowMoveX() { return c.windowMoveX; }
    @Override public float windowMoveY() { return c.windowMoveY; }

    @Override public float windowGradientDuration() { return c.windowGradientDuration; }
    @Override public Ease windowGradientEase() { return c.windowGradientEase.get(); }

    @Override public float closeWindowSpeed() { return c.closeWindowSpeed; }

    @Override public float jeiLeftMoveDuration() { return c.jeiLeftMoveDuration; }
    @Override public Ease jeiLeftMoveEase() { return c.jeiLeftMoveEase.get(); }
    @Override public float jeiLeftMoveX() { return c.jeiLeftMoveX; }
    @Override public float jeiLeftMoveY() { return c.jeiLeftMoveY; }

    @Override public float jeiRightMoveDuration() { return c.jeiRightMoveDuration; }
    @Override public Ease jeiRightMoveEase() { return c.jeiRightMoveEase.get(); }
    @Override public float jeiRightMoveX() { return c.jeiRightMoveX; }
    @Override public float jeiRightMoveY() { return c.jeiRightMoveY; }

    @Override public List<String> disableNames() { return c.disableNames; }

    // ===== screen items =====
    @Override public boolean enableHover() { return c.enableHover; }
    @Override public float hoverDuration() { return c.hoverDuration; }
    @Override public Ease hoverEase() { return c.hoverEase.get(); }
    @Override public float hoverScale() { return c.hoverScale; }

    @Override public boolean enableTooltip() { return c.enableTooltip; }
    @Override public float tooltipDuration() { return c.tooltipDuration; }
    @Override public Ease tooltipEase() { return c.tooltipEase.get(); }

    @Override public boolean enableClickItem() { return c.enableClickItem; }
    @Override public float clickItemScale() { return c.clickItemScale; }
    @Override public float clickZoomStrength() { return c.clickZoomStrength; }
    @Override public float clickItemDuration() { return c.clickItemDuration; }

    @Override public boolean enableOutput() { return c.enableOutput; }
    @Override public float outputDuration() { return c.outputDuration; }
    @Override public Ease outputEase() { return c.outputEase.get(); }

    @Override public boolean enableDrag() { return c.enableDrag; }
    @Override public float dragMaxAngle() { return c.dragMaxAngle; }
    @Override public float dragSensitivity() { return c.dragSensitivity; }

    @Override public boolean enableSameItem() { return c.enableSameItem; }
    @Override public boolean enableQuick() { return c.enableQuick; }
    @Override public float sameItemDelay() { return c.sameItemDelay; }
    @Override public float sameItemShakeStrength() { return c.sameItemShakeStrength; }
    @Override public float sameItemShakeDuration() { return c.sameItemShakeDuration; }
    @Override public float sameItemShakeFrequency() { return c.sameItemShakeFrequency; }
    @Override public float sameItemShakeWaitDuration() { return c.sameItemShakeWaitDuration; }

    // ===== hotbar =====
    @Override public boolean enableHoldItem() { return c.enableHoldItem; }
    @Override public boolean enableHoldZoomTransition() { return c.enableHoldZoomTransition; }
    @Override public float holdZoomInDuration() { return c.holdZoomInDuration; }
    @Override public Ease holdZoomInEase() { return c.holdZoomInEase.get(); }
    @Override public float holdZoomScale() { return c.holdZoomScale; }
    @Override public float holdZoomOutDuration() { return c.holdZoomOutDuration; }
    @Override public Ease holdZoomOutEase() { return c.holdZoomOutEase.get(); }

    @Override public boolean enableSelectedItemName() { return c.enableSelectedItemName; }
    @Override public float selectedItemNameMoveDuration() { return c.selectedItemNameMoveDuration; }
    @Override public float selectedItemNameMoveY() { return c.selectedItemNameMoveY; }
    @Override public Ease selectedItemNameMoveEase() { return c.selectedItemNameMoveEase.get(); }
    @Override public float selectedItemNameAlphaDuration() { return c.selectedItemNameAlphaDuration; }
    @Override public Ease selectedItemNameAlphaEase() { return c.selectedItemNameAlphaEase.get(); }

    @Override public boolean enableAttack() { return c.enableAttack; }
    @Override public float attackMaxAngle() { return c.attackMaxAngle; }

    @Override public boolean enableUse() { return c.enableUse; }
    @Override public float useStrength() { return c.useStrength; }

    @Override public boolean enableLack() { return c.enableLack; }
    @Override public float lackDuration() { return c.lackDuration; }
    @Override public float lackShakeStrength() { return c.lackShakeStrength; }

    @Override public boolean enableSelectMove() { return c.enableSelectMove; }
    @Override public float selectMoveSpeed() { return c.selectMoveSpeed; }

    @Override public boolean enableExp() { return c.enableExp; }
    @Override public float expDuration() { return c.expDuration; }
    @Override public Ease expEase() { return c.expEase.get(); }
    @Override public float expScale() { return c.expScale; }

    @Override public boolean enableArmor() { return c.enableArmor; }
    @Override public float armorDuration() { return c.armorDuration; }
    @Override public float upArmorScale() { return c.upArmorScale; }
    @Override public Ease upArmorEase() { return c.upArmorEase.get(); }
    @Override public float downArmorShakeStrength() { return c.downArmorShakeStrength; }

    // ===== chat =====
    @Override public boolean enableChat() { return c.enableChat; }
    @Override public boolean enableCloseChat() { return c.enableCloseChat; }
    @Override public boolean enableChatComp() { return c.enableChatComp; }
    @Override public float chatOpenMoveDuration() { return c.chatOpenMoveDuration; }
    @Override public Ease chatOpenMoveEase() { return c.chatOpenMoveEase.get(); }
    @Override public float chatOpenGradientDuration() { return c.chatOpenGradientDuration; }
    @Override public Ease chatOpenGradientEase() { return c.chatOpenGradientEase.get(); }
    @Override public float closeChatSpeed() { return c.closeChatSpeed; }
    @Override public float chatCompMoveDuration() { return c.chatCompMoveDuration; }
    @Override public Ease chatCompMoveEase() { return c.chatCompMoveEase.get(); }
    @Override public float chatCompGradientDuration() { return c.chatCompGradientDuration; }
    @Override public Ease chatCompGradientEase() { return c.chatCompGradientEase.get(); }
}