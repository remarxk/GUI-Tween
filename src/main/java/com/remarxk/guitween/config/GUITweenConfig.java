package com.remarxk.guitween.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.compat.ImmersiveUICompat;
import com.remarxk.guitween.util.Ease;

import me.fzzyhmstrs.fzzy_config.annotations.Translation;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedChoice;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedChoice.WidgetType;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum;
import net.minecraft.resources.ResourceLocation;

@Translation(prefix = "guitween.config")
public class GUITweenConfig extends Config {
    public final static List<Ease> EASE_LIST = Arrays.stream(Ease.values()).toList();
    
    public boolean enable = true;

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

    public boolean enableJeiLeft = true;

    public float jeiLeftMoveDuration = 6f;

    public ValidatedChoice<Ease> jeiLeftMoveEase = new ValidatedChoice<>(Ease.OUT_BACK, EASE_LIST, new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);;;

    public float jeiLeftMoveX = -50f;

    public float jeiLeftMoveY = 0f;

    public boolean enableJeiRight = true;

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

    @ConfigGroup.Pop
    public boolean enableQuick = true;
    
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

    public GUITweenConfig() {
        super(ResourceLocation.fromNamespaceAndPath(GUITween.MODID, ""));
    }

    public float getWindowTotalDuration() {
        float windowMax = Math.max(windowMoveDuration, windowGradientDuration);
        float jeiMax = Math.max(jeiLeftMoveDuration, jeiRightMoveDuration);
        return Math.max(windowMax, jeiMax);
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

    public float getBossShowMaxDuration() {
        return Math.max(bossShowDuration, bossShowFadeDuration);
    }

    public float getBossHideMaxDuration() {
        return Math.max(bossHideDuration, bossHideFadeDuration);
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

    public boolean isEnableJeiLeft() {
        return isEnable() && enableJeiLeft;
    }

    public boolean isEnableJeiRight() {
        return isEnable() && enableJeiRight;
    }

    public boolean isEnableDebugWindow() {
        return isEnable() && enableDebugWindow;
    }

    public boolean isEnableHoverItem() {
        return isEnable() && enableHover && !ImmersiveUICompat.isLoaded;
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
        return isEnable() && enableDrag && !ImmersiveUICompat.isLoaded;
    }

    public boolean isEnableSameItem() {
        return isEnable() && enableSameItem;
    }

    public boolean isEnableQuickCraft() {
        return isEnable() && enableQuick;
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
        return isEnable() && enableSelectMove && !ImmersiveUICompat.isLoaded;
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

    public boolean isEnableBossShow() {
        return isEnable() && enableBossShow;
    }

    public boolean isEnableBossHide() {
        return isEnable() && enableBossHide;
    }

    public boolean isEnableBossHurt() {
        return isEnable() && enableBossHurt;
    }
}
