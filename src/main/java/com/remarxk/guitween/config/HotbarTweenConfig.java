package com.remarxk.guitween.config;

import com.remarxk.guitween.util.Ease;
import net.neoforged.neoforge.common.ModConfigSpec;

public class HotbarTweenConfig {
    public final ModConfigSpec.BooleanValue enableHoldItem;

    public final ModConfigSpec.BooleanValue enableHoldZoomTransition;

    public final ModConfigSpec.DoubleValue holdZoomInDuration;

    public final ModConfigSpec.DoubleValue holdZoomOutDuration;

    public final ModConfigSpec.DoubleValue holdZoomScale;

    public final ModConfigSpec.EnumValue<Ease> holdZoomInEase;

    public final ModConfigSpec.EnumValue<Ease> holdZoomOutEase;

    public final ModConfigSpec.BooleanValue enableLack;

    public final ModConfigSpec.DoubleValue lackDuration;

    public final ModConfigSpec.DoubleValue lackShakeStrength;

    public final ModConfigSpec.BooleanValue enableExp;

    public final ModConfigSpec.DoubleValue expDuration;

    public final ModConfigSpec.EnumValue<Ease> expEase;

    public final ModConfigSpec.DoubleValue expScale;
    
    public HotbarTweenConfig(ModConfigSpec.Builder builder) {
        builder.translation("guitween.config.hotbarGroup").push("hotbarGroup");

        enableHoldItem = builder
                .translation("guitween.config.enableHoldItem")
                .define("enableHoldItem", true);

        enableHoldZoomTransition = builder
                .translation("guitween.config.enableHoldZoomTransition")
                .define("enableHoldZoomTransition", false);

        holdZoomInDuration = builder
                .translation("guitween.config.holdZoomInDuration")
                .defineInRange("holdZoomInDuration", 8d, 0, 1000);

        holdZoomInEase = builder
                .translation("guitween.config.holdZoomInEase")
                .defineEnum("holdZoomInEase", Ease.OUT_QUINT);

        holdZoomScale = builder
                .translation("guitween.config.holdZoomScale")
                .defineInRange("holdZoomScale", 1.4d, 1d, 10d);

        holdZoomOutDuration = builder
                .translation("guitween.config.holdZoomOutDuration")
                .defineInRange("holdZoomOutDuration", 2d, 0, 1000);

        holdZoomOutEase = builder
                .translation("guitween.config.holdZoomOutEase")
                .defineEnum("holdZoomOutEase", Ease.OUT_QUART);

        enableLack = builder
                .translation("guitween.config.enableLack")
                .define("enableLack", true);

        lackDuration = builder
                .translation("guitween.config.lackDuration")
                .defineInRange("lackDuration", 8d, 0, 1000d);

        lackShakeStrength = builder
                .translation("guitween.config.lackShakeStrength")
                .defineInRange("lackShakeStrength", 3d, 0, 1000);

        enableExp = builder
                .translation("guitween.config.enableExp")
                .define("enableExp", true);

        expDuration = builder
                .translation("guitween.config.expDuration")
                .defineInRange("expDuration", 4d, 0, 1000d);

        expEase = builder
                .translation("guitween.config.expEase")
                .defineEnum("expEase", Ease.OUT_BACK);

        expScale = builder
                .translation("guitween.config.expScale")
                .defineInRange("expScale", 4d, 1, 1000);

        builder.pop();
    }
}
