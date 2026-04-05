package com.remarxk.guitween.config;

import com.remarxk.guitween.util.Ease;
import net.neoforged.neoforge.common.ModConfigSpec;

public class WindowItemTweenConfig {
    public final ModConfigSpec.BooleanValue enableHover;

    public final ModConfigSpec.DoubleValue hoverDuration;

    public final ModConfigSpec.EnumValue<Ease> hoverEase;

    public final ModConfigSpec.DoubleValue hoverScale;

    public final ModConfigSpec.BooleanValue enableTooltip;

    public final ModConfigSpec.DoubleValue tooltipDuration;

    public final ModConfigSpec.EnumValue<Ease> tooltipEase;

    public final ModConfigSpec.DoubleValue clickItemScale;

    public final ModConfigSpec.BooleanValue enableClickItem;

    public final ModConfigSpec.DoubleValue clickZoomStrength;

    public final ModConfigSpec.DoubleValue clickItemDuration;

    public final ModConfigSpec.BooleanValue enableOutput;

    public final ModConfigSpec.DoubleValue outputDuration;

    public final ModConfigSpec.EnumValue<Ease> outputEase;

    public final ModConfigSpec.BooleanValue enableDrag;

    public final ModConfigSpec.DoubleValue dragMaxAngle;

    public final ModConfigSpec.DoubleValue dragSensitivity;

    public final ModConfigSpec.BooleanValue enableSameItem;

    public final ModConfigSpec.DoubleValue sameItemDelay;

    public final ModConfigSpec.DoubleValue sameItemShakeStrength;

    public final ModConfigSpec.DoubleValue sameItemShakeDuration;

    public final ModConfigSpec.DoubleValue sameItemShakeFrequency;

    public final ModConfigSpec.DoubleValue sameItemShakeWaitDuration;

    public final ModConfigSpec.BooleanValue enableQuick;

    public WindowItemTweenConfig(ModConfigSpec.Builder builder) {
        builder.translation("guitween.config.screenItemGroup").push("screenItemGroup");

        enableHover = builder
                .translation("guitween.config.enableHover")
                .define("enableHover", true);

        hoverDuration = builder
                .translation("guitween.config.hoverDuration")
                .defineInRange("hoverDuration", 4d, 0, 1000d);

        hoverEase = builder
                .translation("guitween.config.hoverEase")
                .defineEnum("hoverEase", Ease.IN_OUT_SINE);

        hoverScale = builder
                .translation("guitween.config.hoverScale")
                .defineInRange("hoverScale", 1.2d, 1d, 10d);

        enableTooltip = builder
                .translation("guitween.config.enableTooltip")
                .define("enableTooltip", true);

        tooltipDuration = builder
                .translation("guitween.config.tooltipDuration")
                .defineInRange("tooltipDuration", 6, 0d, 1000d);

        tooltipEase = builder
                .translation("guitween.config.tooltipEase")
                .defineEnum("tooltipEase", Ease.OUT_CIRC);

        clickItemScale = builder
                .translation("guitween.config.clickItemScale")
                .defineInRange("clickItemScale", 1.2d, 0, 1000d);

        enableClickItem = builder
                .translation("guitween.config.enableClickItem")
                .define("enableClickItem", true);

        clickZoomStrength = builder
                .translation("guitween.config.clickZoomStrength")
                .defineInRange("clickZoomStrength", 0.2d, 0, 1000d);

        clickItemDuration = builder
                .translation("guitween.config.clickItemDuration")
                .defineInRange("clickItemDuration", 5d, 0, 1000d);

        enableOutput = builder
                .translation("guitween.config.enableOutput")
                .define("enableOutput", true);

        outputDuration = builder
                .translation("guitween.config.outputDuration")
                .defineInRange("outputDuration", 5d, 0, 1000d);

        outputEase = builder
                .translation("guitween.config.outputEase")
                .defineEnum("outputEase", Ease.OUT_BACK);

        enableDrag = builder
                .translation("guitween.config.enableDrag")
                .define("enableDrag", true);

        dragMaxAngle = builder
                .translation("guitween.config.dragMaxAngle")
                .defineInRange("dragMaxAngle", 15d, 0, 90);

        dragSensitivity = builder
                .translation("guitween.config.dragSensitivity")
                .defineInRange("dragSensitivity", 8d, 0, 1000);

        enableSameItem = builder
                .translation("guitween.config.enableSameItem")
                .define("enableSameItem", true);

        sameItemDelay = builder
                .translation("guitween.config.sameItemDelay")
                .defineInRange("sameItemDelay", 8d, 0, 1000);

        sameItemShakeStrength = builder
                .translation("guitween.config.sameItemShakeStrength")
                .defineInRange("sameItemShakeStrength", 1.5d, 0, 1000);

        sameItemShakeDuration = builder
                .translation("guitween.config.sameItemShakeDuration")
                .defineInRange("sameItemShakeDuration", 8d, 0, 1000);

        sameItemShakeFrequency = builder
                .translation("guitween.config.sameItemShakeFrequency")
                .defineInRange("sameItemShakeFrequency", 1d, 0, 1000);

        sameItemShakeWaitDuration = builder
                .translation("guitween.config.sameItemShakeWaitDuration")
                .defineInRange("sameItemShakeWaitDuration", 20d, 0, 1000);

        enableQuick = builder
                .translation("guitween.config.enableQuick")
                .define("enableQuick", true);

        builder.pop();
    }
}
