package com.remarxk.guitween.config;

import com.remarxk.guitween.util.Ease;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class WindowTweenConfig {
    public final ModConfigSpec.BooleanValue enable;

    public final ModConfigSpec.ConfigValue<List<?>> disableNames;

    public final ModConfigSpec.DoubleValue moveDuration;

    public final ModConfigSpec.EnumValue<Ease> moveEase;

    public final ModConfigSpec.DoubleValue moveX;

    public final ModConfigSpec.DoubleValue moveY;

    public final ModConfigSpec.DoubleValue gradientDuration;

    public final ModConfigSpec.EnumValue<Ease> gradientEase;

    public WindowTweenConfig(ModConfigSpec.Builder BUILDER) {
        BUILDER.translation("guitween.config.windowGroup").push("windowGroup");

        enable = BUILDER
                .translation("guitween.config.enableWindow")
                .define("enableWindow", true);

        disableNames = BUILDER
                .translation("guitween.config.disableNames")
                .defineList(
                        "disableNames",
                        List.of("None"),
                        () -> "",
                        obj -> obj instanceof String
                );

        moveDuration = BUILDER
                .translation("guitween.config.windowMoveDuration")
                .defineInRange("windowMoveDuration", 6d, 0, 1000d);

        moveEase = BUILDER
                .translation("guitween.config.windowMoveEase")
                .defineEnum("windowMoveEase", Ease.OUT_BACK);

        moveX = BUILDER
                .translation("guitween.config.windowMoveX")
                .defineInRange("windowMoveX", 0d, -10000, 10000);

        moveY = BUILDER
                .translation("guitween.config.windowMoveY")
                .defineInRange("windowMoveY", 50d, -10000, 10000);

        gradientDuration = BUILDER
                .translation("guitween.config.windowGradientDuration")
                .defineInRange("windowGradientDuration", 8d, 0, 1000d);

        gradientEase = BUILDER
                .translation("guitween.config.windowGradientEase")
                .defineEnum("windowGradientEase", Ease.IN_OUT_SINE);

        BUILDER.pop();
    }
}
