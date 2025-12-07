package com.remarxk.guitween.config;

import java.util.Arrays;

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

    public ConfigGroup windowGroup = new ConfigGroup("window");
    public int windowDuration = 40;

    @ConfigGroup.Pop
    public ValidatedChoice<Ease> windowEase = new ValidatedChoice<>(Ease.IN_OUT_SINE, Arrays.stream(Ease.values()).toList(), new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public ConfigGroup holdItemGroup = new ConfigGroup("hold item");
    public int holdItemScaleDuration = 30;

    public int holdItemRestoreDuration = 10;

    public float holdItemScale = 1.4f;

    public ValidatedChoice<Ease> holdItemScaleEase = new ValidatedChoice<>(Ease.OUT_QUINT, Arrays.stream(Ease.values()).toList(), new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    @ConfigGroup.Pop
    public ValidatedChoice<Ease> holdItemRestoreEase = new ValidatedChoice<>(Ease.OUT_QUART, Arrays.stream(Ease.values()).toList(), new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    public ConfigGroup hoverGroup = new ConfigGroup("hover item");
    public int hoverDuration = 20;

    public ValidatedChoice<Ease> hoverEase = new ValidatedChoice<>(Ease.IN_OUT_SINE, Arrays.stream(Ease.values()).toList(), new ValidatedEnum(Ease.class), WidgetType.SCROLLABLE);

    @ConfigGroup.Pop
    public float hoverScale = 1.2f;

    public GUITweenConfig() {
        super(ResourceLocation.fromNamespaceAndPath(GUITween.MODID, ""));
    }

    public int getHoldItemTotalDuration() {
        return holdItemScaleDuration + holdItemRestoreDuration;
    }
}
