package com.remarxk.guitween.config;

import com.remarxk.guitween.Constants;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.anim.GUITweenStyle;
import com.remarxk.guitween.util.Ease;
import me.fzzyhmstrs.fzzy_config.annotations.Translation;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedChoice;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedChoice.WidgetType;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum;
import net.minecraft.resources.Identifier;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Translation(prefix = "guitween.config")
public class FabricGUITweenConfig extends Config {
    public final static List<Ease> EASE_LIST = Arrays.stream(Ease.values()).toList();

    public final static List<GUITweenStyle> STYLE_LIST = Arrays.stream(GUITweenStyle.values()).toList();

    private static final FabricGUITweenConfig DEFAULTS = new FabricGUITweenConfig();

    public boolean enable = true;

    public ValidatedChoice<GUITweenStyle> style = new ValidatedChoice<>(GUITweenStyle.DEFAULT, STYLE_LIST, new ValidatedEnum(GUITweenStyle.class), WidgetType.SCROLLABLE);

    public boolean enableDebugWindow = false;

    public ConfigGroup windowGroup = new ConfigGroup("window tween");
    public boolean enableWindow = true;

    public List<String> disableNames = List.of("None");

    public float windowMoveDuration = 6;

    public ValidatedChoice<Ease> windowMoveEase = new ValidatedChoice<>(Ease.OUT_BACK, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public float windowMoveX = 0;

    public float windowMoveY = 50;

    public float windowGradientDuration = 8;

    public ValidatedChoice<Ease> windowGradientEase  = new ValidatedChoice<>(Ease.IN_OUT_SINE, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public boolean enableCloseWindow = false;

    public float closeWindowSpeed = 1.5f;

    public boolean enableJei = true;

    public float jeiLeftMoveDuration = 6f;

    public ValidatedChoice<Ease> jeiLeftMoveEase = new ValidatedChoice<>(Ease.OUT_BACK, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);;;

    public float jeiLeftMoveX = -50f;

    public float jeiLeftMoveY = 0f;

    public float jeiRightMoveDuration = 6f;

    public ValidatedChoice<Ease> jeiRightMoveEase = new ValidatedChoice<>(Ease.OUT_BACK, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);;

    public float jeiRightMoveX = 50f;

    @ConfigGroup.Pop
    public float jeiRightMoveY = 0;

    public ConfigGroup screenItemGroup = new ConfigGroup("Screen Item tween");
    public boolean enableHover = true;

    public float hoverDuration = 4;

    public ValidatedChoice<Ease> hoverEase = new ValidatedChoice<>(Ease.IN_OUT_SINE, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public float hoverScale = 1.2f;

    public boolean enableTooltip = true;

    public float tooltipDuration = 6;

    public ValidatedChoice<Ease> tooltipEase = new ValidatedChoice<>(Ease.OUT_CIRC, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public float clickItemScale = 1.2f;

    public boolean enableClickItem = true;

    public float clickZoomStrength = 0.2f;

    public float clickItemDuration =5;

    public boolean enableOutput = true;

    public float outputDuration = 5;

    public ValidatedChoice<Ease> outputEase = new ValidatedChoice<>(Ease.OUT_BACK, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public boolean enableDrag = true;

    public float dragMaxAngle = 15f;

    public float dragSensitivity = 8f;

    public boolean enableSameItem = true;

    public float sameItemDelay = 8f;

    public float sameItemShakeStrength = 1.5f;

    public float sameItemShakeDuration = 8f;

    public float sameItemShakeFrequency = 1f;

    public float sameItemShakeWaitDuration = 20f;

    public boolean enableQuick = true;

    public boolean enableMove = true;

    public float moveDuration = 6;

    public ValidatedChoice<Ease> moveEase = new ValidatedChoice<>(Ease.IN_OUT_SINE, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public boolean enableFinish = true;

    public float finishPunchStrength = 0.2f;

    public float finishDuration = 6;

    public boolean enablePickup = true;

    public float pickupDuration = 4;

    public ValidatedChoice<Ease> pickupEase = new ValidatedChoice<>(Ease.IN_OUT_SINE, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    @ConfigGroup.Pop
    public float quickCraftDuration = 4;

    public ConfigGroup hotbarGroup = new ConfigGroup("hotbar tween");
    public boolean enableHoldItem = true;

    public boolean enableHoldZoomTransition = false;

    public float holdZoomInDuration = 8;

    public ValidatedChoice<Ease> holdZoomInEase = new ValidatedChoice<>(Ease.OUT_QUINT, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public float holdZoomScale = 1.4f;

    public float holdZoomOutDuration = 2;

    public ValidatedChoice<Ease> holdZoomOutEase = new ValidatedChoice<>(Ease.OUT_QUART, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public boolean enableSelectedItemName = true;

    public float selectedItemNameMoveDuration = 4f;

    public float selectedItemNameMoveY = 5f;

    public ValidatedChoice<Ease> selectedItemNameMoveEase = new ValidatedChoice<>(Ease.IN_OUT_SINE, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public float selectedItemNameAlphaDuration = 6f;

    public ValidatedChoice<Ease> selectedItemNameAlphaEase  = new ValidatedChoice<>(Ease.IN_OUT_SINE, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public boolean enableAttack = true;

    public float attackMaxAngle = 15f;

    public boolean enableUse = true;

    public float useStrength = 0.2f;

    public boolean enableLack = true;

    public float lackDuration = 8f;

    public float lackShakeStrength = 3f;

    public boolean enableSelectMove = true;

    public float selectMoveSpeed = 1.5f;

    public boolean enableExp = true;

    public float expDuration = 4f;

    public ValidatedChoice<Ease> expEase = new ValidatedChoice<>(Ease.OUT_BACK, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public float expScale = 4f;

    public boolean enableArmor = true;

    public float armorDuration = 4f;

    public float upArmorScale = 1.5f;

    public ValidatedChoice<Ease> upArmorEase = new ValidatedChoice<>(Ease.IN_OUT_SINE, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    @ConfigGroup.Pop
    public float downArmorShakeStrength = 3f;

    public ConfigGroup chatGroup = new ConfigGroup("chat tween");
    public boolean enableChat = true;

    public float chatOpenMoveDuration = 4f;

    public ValidatedChoice<Ease> chatOpenMoveEase = new ValidatedChoice<>(Ease.IN_OUT_SINE, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public float chatOpenGradientDuration = 6f;

    public ValidatedChoice<Ease> chatOpenGradientEase = new ValidatedChoice<>(Ease.IN_OUT_SINE, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public boolean enableCloseChat = true;

    public float closeChatSpeed = 1.2f;

    public boolean enableChatComp = true;

    public float chatCompMoveDuration = 4f;

    public ValidatedChoice<Ease> chatCompMoveEase = new ValidatedChoice<>(Ease.IN_OUT_SINE, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public float chatCompGradientDuration = 6f;

    @ConfigGroup.Pop
    public ValidatedChoice<Ease> chatCompGradientEase = new ValidatedChoice<>(Ease.IN_OUT_SINE, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public ConfigGroup bossGroup = new ConfigGroup("boss tween");
    public boolean enableBossShow = true;

    public float bossShowDuration = 10f;

    public ValidatedChoice<Ease> bossShowEase = new ValidatedChoice<>(Ease.IN_OUT_SINE, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public float bossShowFadeDuration = 10f;

    public ValidatedChoice<Ease> bossShowFadeEase = new ValidatedChoice<>(Ease.IN_OUT_SINE, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public boolean enableBossHide = true;

    public float bossHideDuration = 6f;

    public ValidatedChoice<Ease> bossHideEase = new ValidatedChoice<>(Ease.IN_OUT_SINE, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public float bossHideFadeDuration = 6f;

    public ValidatedChoice<Ease> bossHideFadeEase = new ValidatedChoice<>(Ease.IN_OUT_SINE, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public boolean enableBossHurt = true;

    public float bossHurtShakeStrength = 1.5f;

    public float bossHurtDuration = 4f;

    public FabricGUITweenConfig() {
        super(Identifier.fromNamespaceAndPath(Constants.MODID, "config"));
    }

    public float getWindowTotalDuration() {
        return Math.max(windowMoveDuration, windowGradientDuration);
    }

    public float getJeiTotalDuration() {
        return Math.max(jeiLeftMoveDuration, jeiRightMoveDuration);
    }

    public float getHoldItemTotalDuration() {
        return holdZoomInDuration + holdZoomOutDuration;
    }

    public float getSameItemTotalDuration() {
        return sameItemDelay + sameItemShakeDuration + sameItemShakeWaitDuration;
    }

    public float getSelectedItemNameDuration() {
        return Math.max(selectedItemNameMoveDuration, selectedItemNameAlphaDuration);
    }

    public float getChatOpenMaxDuration() {
        return Math.max(chatOpenMoveDuration, chatOpenGradientDuration);
    }

    public float getChatCompMaxDuration() {
        return Math.max(chatCompMoveDuration, chatCompGradientDuration);
    }

    public boolean isDisableTweenWindow(String screenName) {
        return disableNames.contains(screenName);
    }

    public boolean isEnable() {
        return enable;
    }

    public boolean isEnableWindow() {
        return isEnable() && enableWindow;
    }

    public boolean isEnableCloseWindow() {
        return isEnable() && enableCloseWindow;
    }

    public boolean isEnableJei() {
        return isEnable() && enableJei;
    }

    public boolean isEnableDebugWindow() {
        return isEnable() && enableDebugWindow;
    }

    public boolean isEnableHoverItem() {
        return isEnable() && enableHover;
    }

    public boolean isEnableTooltip() {
        return isEnable() && enableTooltip;
    }

    public boolean isEnableClickItem() {
        return isEnable() && enableClickItem;
    }

    public boolean isEnableOutput() {
        return isEnable() && enableOutput;
    }

    public boolean isEnableDragItem() {
        return isEnable() && enableDrag;
    }

    public boolean isEnableSameItem() {
        return isEnable() && enableSameItem;
    }

    public boolean isEnableQuickCraft() {
        return isEnable() && enableQuick;
    }

    public boolean isEnableMoveItem() {
        return isEnable() && enableMove;
    }

    public boolean isEnableFinishItem() {
        return isEnable() && enableFinish;
    }

    public boolean isEnablePickupItem() {
        return isEnable() && enablePickup;
    }

    public boolean isEnableHoldItem() {
        return isEnable() && enableHoldItem;
    }

    public boolean isEnableAttack() {
        return isEnable() && enableAttack;
    }

    public boolean isEnableUse() {
        return isEnable() && enableUse;
    }

    public boolean isEnableLack() {
        return isEnable() && enableLack;
    }

    public boolean isEnableSelectMove() {
        return isEnable() && enableSelectMove;
    }

    public boolean isEnableSelectedItemName() {
        return isEnable() && enableSelectedItemName;
    }

    public boolean isEnableExp() {
        return isEnable() && enableExp;
    }

    public boolean isEnableArmor() {
        return isEnable() && enableArmor;
    }

    public boolean isEnableChat() {
        return isEnable() && enableChat;
    }

    public boolean isEnableCloseChat() {
        return isEnable() && enableCloseChat;
    }

    public boolean isEnableChatComp() {
        return isEnable() && enableChatComp;
    }

    public void applyStylePreset(GUITweenStyle newStyle) {
        resetToDefaults();
        enableDebugWindow = false;

        switch (newStyle) {
            case SIMPLE -> {
                applySimpleStyle();
            }
            case COMPLETE -> {
                applyCompleteStyle();
            }
        }

        save();
    }

    private void applySimpleStyle() {
        windowMoveEase.accept(Ease.OUT_QUART);
        jeiLeftMoveEase.accept(Ease.OUT_QUART);
        jeiRightMoveEase.accept(Ease.OUT_QUART);

        enableSameItem = false;
        enableClickItem = false;
        enableFinish = false;
        enableDrag = false;
    }

    private void applyCompleteStyle() {
        enableCloseWindow = true;
    }

    private static GUITweenStyle lastAppliedStyle;

    @Override
    public void onUpdateClient() {
        GUITweenStyle currentStyle = style.get();

        if (lastAppliedStyle == null) {
            lastAppliedStyle = currentStyle;
            return;
        }

        if (currentStyle != lastAppliedStyle) {
            lastAppliedStyle = currentStyle;
            applyStylePreset(currentStyle);
        }
    }

    private void resetToDefaults() {
        for (Field field : FabricGUITweenConfig.class.getDeclaredFields()) {
            try {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers()))
                    continue;
                if (field.getName().equals("style"))
                    continue;
                field.setAccessible(true);
                Object value = field.get(this);
                if (value instanceof me.fzzyhmstrs.fzzy_config.validation.ValidatedField<?> validated) {
                    validated.restore();
                } else {
                    field.set(this, field.get(DEFAULTS));
                }
            } catch (IllegalAccessException ignored) {
            }
        }
    }
}