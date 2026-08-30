package com.remarxk.guitween.config;

import com.remarxk.guitween.util.Ease;
import net.neoforged.neoforge.common.ModConfigSpec;

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

    public final ModConfigSpec.BooleanValue enableCloseWindow;

    public final ModConfigSpec.DoubleValue closeWindowSpeed;

    public final ModConfigSpec.BooleanValue enableJei;

    public final ModConfigSpec.DoubleValue jeiLeftMoveDuration;

    public final ModConfigSpec.EnumValue<Ease> jeiLeftMoveEase;

    public final ModConfigSpec.DoubleValue jeiLeftMoveX;

    public final ModConfigSpec.DoubleValue jeiLeftMoveY;

    public final ModConfigSpec.DoubleValue jeiRightMoveDuration;

    public final ModConfigSpec.EnumValue<Ease> jeiRightMoveEase;

    public final ModConfigSpec.DoubleValue jeiRightMoveX;

    public final ModConfigSpec.DoubleValue jeiRightMoveY;

    public WindowTweenConfig(ModConfigSpec.Builder BUILDER) {
        BUILDER.translation("guitween.config.windowGroup").push("windowGroup");

        enable = BUILDER
                .translation("guitween.config.enableWindow")
                .define("enableWindow", true);

        disableNames = BUILDER
                .translation("guitween.config.disableNames")
                .defineListAllowEmpty(
                        "disableNames",
                        List.of(),
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

        enableJei = BUILDER
                .translation("guitween.config.enableJei")
                .define("enableJei", true);

        jeiLeftMoveDuration = BUILDER
                .translation("guitween.config.jeiLeftMoveDuration")
                .defineInRange("jeiLeftMoveDuration", 6d, 0, 1000d);

        jeiLeftMoveEase = BUILDER
                .translation("guitween.config.jeiLeftMoveEase")
                .defineEnum("jeiLeftMoveEase", Ease.OUT_BACK);

        jeiLeftMoveX = BUILDER
                .translation("guitween.config.jeiLeftMoveX")
                .defineInRange("jeiLeftMoveX", -50d, -10000, 10000);

        jeiLeftMoveY = BUILDER
                .translation("guitween.config.jeiLeftMoveY")
                .defineInRange("jeiLeftMoveY", 0f, -10000, 10000);

        jeiRightMoveDuration = BUILDER
                .translation("guitween.config.jeiRightMoveDuration")
                .defineInRange("jeiRightMoveDuration", 6d, 0, 1000d);

        jeiRightMoveEase = BUILDER
                .translation("guitween.config.jeiRightMoveEase")
                .defineEnum("jeiRightMoveEase", Ease.OUT_BACK);

        jeiRightMoveX = BUILDER
                .translation("guitween.config.jeiRightMoveX")
                .defineInRange("jeiRightMoveX", 50d, -10000, 10000);

        jeiRightMoveY = BUILDER
                .translation("guitween.config.jeiRightMoveY")
                .defineInRange("jeiRightMoveY", 0f, -10000, 10000);

        enableCloseWindow = BUILDER
                .translation("guitween.config.enableCloseWindow")
                .define("enableCloseWindow", false);

        closeWindowSpeed = BUILDER
                .translation("guitween.config.closeWindowSpeed")
                .defineInRange("closeWindowSpeed", 1.5d, 0, 1000d);

        BUILDER.pop();
    }
}
