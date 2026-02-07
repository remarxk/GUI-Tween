package com.remarxk.guitween.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.remarxk.guitween.GUITween;
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
    public boolean enable = true;

    public boolean enableDebugWindow = false;

    public ConfigGroup windowGroup = new ConfigGroup("window tween");
    public boolean enableWindow = true;

    public List<String> disableNames = List.of("None");

    public float windowMoveDuration = 6;

    public ValidatedChoice<Ease> windowMoveEase = new ValidatedChoice<>(Ease.OUT_BACK, Arrays.stream(Ease.values()).toList(), new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public float windowMoveX = 0;

    public float windowMoveY = 50;

    public float windowGradientDuration = 8;

    @ConfigGroup.Pop
    public ValidatedChoice<Ease> windowGradientEase  = new ValidatedChoice<>(Ease.IN_OUT_SINE, Arrays.stream(Ease.values()).toList(), new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public ConfigGroup screenItemGroup = new ConfigGroup("Screen Item tween");
    public boolean enableHover = true;

    public float hoverDuration = 4;

    public ValidatedChoice<Ease> hoverEase = new ValidatedChoice<>(Ease.IN_OUT_SINE, Arrays.stream(Ease.values()).toList(), new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public float hoverScale = 1.2f;

    public boolean enableTooltip = true;

    public float tooltipDuration = 6;

    public ValidatedChoice<Ease> tooltipEase = new ValidatedChoice<>(Ease.OUT_CIRC, Arrays.stream(Ease.values()).toList(), new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public boolean enableClickItem = true;

    public float clickItemDuration =5;

    public boolean enableOutput = true;

    public float outputDuration = 5;

    @ConfigGroup.Pop
    public ValidatedChoice<Ease> outputEase = new ValidatedChoice<>(Ease.OUT_BACK, Arrays.stream(Ease.values()).toList(), new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public ConfigGroup hotbarGroup = new ConfigGroup("hotbar tween");
    public boolean enableHoldItem = true;

    public float holdZoomInDuration = 8;

    public ValidatedChoice<Ease> holdZoomInEase = new ValidatedChoice<>(Ease.OUT_QUINT, Arrays.stream(Ease.values()).toList(), new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public float holdZoomScale = 1.4f;

    public float holdZoomOutDuration = 2;

    public ValidatedChoice<Ease> holdZoomOutEase = new ValidatedChoice<>(Ease.OUT_QUART, Arrays.stream(Ease.values()).toList(), new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public boolean enableLack = true;

    public float lackDuration = 8f;

    public float lackShakeStrength = 3f;

    public boolean enableExp = true;

    public float expDuration = 4f;

    public ValidatedChoice<Ease> expEase = new ValidatedChoice<>(Ease.OUT_BACK, Arrays.stream(Ease.values()).toList(), new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    @ConfigGroup.Pop
    public float expScale = 4f;

    public ConfigGroup chatGroup = new ConfigGroup("chat tween");
    public boolean enableChat = true;

    public float chatOpenMoveDuration = 4f;

    public ValidatedChoice<Ease> chatOpenMoveEase = new ValidatedChoice<>(Ease.IN_OUT_SINE, Arrays.stream(Ease.values()).toList(), new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public float chatOpenMoveX = 0;

    public float chatOpenMoveY = 50;

    public float chatOpenGradientDuration = 6f;

    public ValidatedChoice<Ease> chatOpenGradientEase = new ValidatedChoice<>(Ease.IN_OUT_SINE, Arrays.stream(Ease.values()).toList(), new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public boolean enableChatComp = true;

    public float chatCompMoveDuration = 4f;

    public ValidatedChoice<Ease> chatCompMoveEase = new ValidatedChoice<>(Ease.IN_OUT_SINE, Arrays.stream(Ease.values()).toList(), new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public float chatCompMoveX = -500f;

    public float chatCompMoveY = 0;

    public float chatCompGradientDuration = 6f;

    public ValidatedChoice<Ease> chatCompGradientEase = new ValidatedChoice<>(Ease.IN_OUT_SINE, Arrays.stream(Ease.values()).toList(), new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public GUITweenConfig() {
        super(ResourceLocation.fromNamespaceAndPath(GUITween.MODID, ""));
    }

    public float getHoldItemTotalDuration() {
        return holdZoomInDuration + holdZoomOutDuration;
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

    public boolean isEnableHoldItem() {
        return isEnable() && enableHoldItem;
    }

    public boolean isEnableLack() {
        return isEnable() && enableLack;
    }

    public boolean isEnableExp() {
        return isEnable() && enableExp;
    }

    public boolean isEnableChat() {
        return isEnable() && enableChat;
    }

    public boolean isEnableChatComp() {
        return isEnable() && enableChatComp;
    }
}
