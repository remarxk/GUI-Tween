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

    public final ModConfigSpec.DoubleValue closeMoveDuration;

    public final ModConfigSpec.EnumValue<Ease> closeMoveEase;

    public final ModConfigSpec.DoubleValue closeMoveX;

    public final ModConfigSpec.DoubleValue closeMoveY;

    public final ModConfigSpec.DoubleValue closeGradientDuration;

    public final ModConfigSpec.EnumValue<Ease> closeGradientEase;

    public final ModConfigSpec.BooleanValue enableJei;

    public final ModConfigSpec.DoubleValue jeiMoveDuration;

    public final ModConfigSpec.EnumValue<Ease> jeiMoveEase;

    public final ModConfigSpec.DoubleValue jeiMoveX;

    public final ModConfigSpec.DoubleValue jeiMoveY;

    public final ModConfigSpec.DoubleValue closeJeiMoveDuration;

    public final ModConfigSpec.EnumValue<Ease> closeJeiMoveEase;

    public final ModConfigSpec.DoubleValue closeJeiMoveX;

    public final ModConfigSpec.DoubleValue closeJeiMoveY;

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

        // 左右统一：按左侧方向填写，右侧会自动镜像 X
        jeiMoveDuration = BUILDER
                .translation("guitween.config.jeiMoveDuration")
                .defineInRange("jeiMoveDuration", 6d, 0, 1000d);

        jeiMoveEase = BUILDER
                .translation("guitween.config.jeiMoveEase")
                .defineEnum("jeiMoveEase", Ease.OUT_BACK);

        jeiMoveX = BUILDER
                .translation("guitween.config.jeiMoveX")
                .defineInRange("jeiMoveX", -50d, -10000, 10000);

        jeiMoveY = BUILDER
                .translation("guitween.config.jeiMoveY")
                .defineInRange("jeiMoveY", 0d, -10000, 10000);

        enableCloseWindow = BUILDER
                .translation("guitween.config.enableCloseWindow")
                .define("enableCloseWindow", false);

        closeMoveDuration = BUILDER
                .translation("guitween.config.closeMoveDuration")
                .defineInRange("closeMoveDuration", 6d, 0, 1000d);

        closeMoveEase = BUILDER
                .translation("guitween.config.closeMoveEase")
                .defineEnum("closeMoveEase", Ease.IN_OUT_SINE);

        closeMoveX = BUILDER
                .translation("guitween.config.closeMoveX")
                .defineInRange("closeMoveX", 0d, -10000, 10000);

        closeMoveY = BUILDER
                .translation("guitween.config.closeMoveY")
                .defineInRange("closeMoveY", 300d, -10000, 10000);

        closeGradientDuration = BUILDER
                .translation("guitween.config.closeGradientDuration")
                .defineInRange("closeGradientDuration", 8d, 0, 1000d);

        closeGradientEase = BUILDER
                .translation("guitween.config.closeGradientEase")
                .defineEnum("closeGradientEase", Ease.IN_OUT_SINE);

        // 关闭时同样左右统一，右侧自动镜像 X
        closeJeiMoveDuration = BUILDER
                .translation("guitween.config.closeJeiMoveDuration")
                .defineInRange("closeJeiMoveDuration", 6d, 0, 1000d);

        closeJeiMoveEase = BUILDER
                .translation("guitween.config.closeJeiMoveEase")
                .defineEnum("closeJeiMoveEase", Ease.IN_OUT_SINE);

        closeJeiMoveX = BUILDER
                .translation("guitween.config.closeJeiMoveX")
                .defineInRange("closeJeiMoveX", -300d, -10000, 10000);

        closeJeiMoveY = BUILDER
                .translation("guitween.config.closeJeiMoveY")
                .defineInRange("closeJeiMoveY", 0d, -10000, 10000);

        BUILDER.pop();
    }
}
