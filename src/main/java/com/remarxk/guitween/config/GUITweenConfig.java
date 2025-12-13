package com.remarxk.guitween.config;

import java.util.Arrays;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.util.Ease;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

public class GUITweenConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue enable;

    public static final ModConfigSpec.IntValue windowDuration;

    public static final ModConfigSpec.EnumValue<Ease> windowEase;

    public static final ModConfigSpec.IntValue holdItemScaleDuration;

    public static final ModConfigSpec.IntValue holdItemRestoreDuration;

    public static final ModConfigSpec.DoubleValue holdItemScale;

    public static final ModConfigSpec.EnumValue<Ease> holdItemScaleEase;

    public static final ModConfigSpec.EnumValue<Ease> holdItemRestoreEase;

    public static final ModConfigSpec.IntValue hoverDuration;

    public static final ModConfigSpec.EnumValue<Ease> hoverEase;

    public static final ModConfigSpec.DoubleValue hoverScale;

    public static final ModConfigSpec SPEC;

    static {
        GUITween.LOGGER.info("GUITweenConfig 初始化");

        enable = BUILDER
                .translation("guitween.config.enable")
                .define("enable", true);

        BUILDER.translation("guitween.config.windowGroup").push("windowGroup");

        windowDuration = BUILDER
                .translation("guitween.config.windowDuration")
                .defineInRange("windowDuration", 40, 10, 1000);

        windowEase = BUILDER
                .translation("guitween.config.windowEase")
                .defineEnum("windowEase", Ease.IN_OUT_SINE);

        BUILDER.pop();

        BUILDER.translation("guitween.config.holdItemGroup").push("holdItemGroup");

        holdItemScaleDuration = BUILDER
                .translation("guitween.config.holdItemScaleDuration")
                .defineInRange("holdItemScaleDuration", 30, 10, 1000);

        holdItemRestoreDuration = BUILDER
                .translation("guitween.config.holdItemRestoreDuration")
                .defineInRange("holdItemRestoreDuration", 10, 5, 1000);

        holdItemScale = BUILDER
                .translation("guitween.config.holdItemScale")
                .defineInRange("holdItemScale", 1.4d, 1d, 10d);

        holdItemScaleEase = BUILDER
                .translation("guitween.config.holdItemScaleEase")
                .defineEnum("holdItemScaleEase", Ease.OUT_QUINT);

        holdItemRestoreEase = BUILDER
                .translation("guitween.config.holdItemRestoreEase")
                .defineEnum("holdItemRestoreEase", Ease.OUT_QUART);

        BUILDER.pop();

        BUILDER.translation("guitween.config.hoverGroup").push("hoverGroup");

        hoverDuration = BUILDER
                .translation("guitween.config.hoverDuration")
                .defineInRange("hoverDuration", 20, 10, 1000);

        hoverEase = BUILDER
                .translation("guitween.config.hoverEase")
                .defineEnum("hoverEase", Ease.IN_OUT_SINE);

        hoverScale = BUILDER
                .translation("guitween.config.hoverScale")
                .defineInRange("hoverScale", 1.2d, 1d, 10d);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static int getHoldItemTotalDuration() {
        return holdItemScaleDuration.get() + holdItemRestoreDuration.get();
    }
}
