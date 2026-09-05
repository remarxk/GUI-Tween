package com.remarxk.guitween.config;

import com.remarxk.guitween.util.Ease;
import net.neoforged.neoforge.common.ModConfigSpec;

public class BossTweenConfig {
    public final ModConfigSpec.BooleanValue enableBossShow;

    public final ModConfigSpec.DoubleValue bossShowDuration;

    public final ModConfigSpec.EnumValue<Ease> bossShowEase;

    public final ModConfigSpec.DoubleValue bossShowFadeDuration;

    public final ModConfigSpec.EnumValue<Ease> bossShowFadeEase;

    public final ModConfigSpec.BooleanValue enableBossHide;

    public final ModConfigSpec.DoubleValue bossHideDuration;

    public final ModConfigSpec.EnumValue<Ease> bossHideEase;

    public final ModConfigSpec.DoubleValue bossHideFadeDuration;

    public final ModConfigSpec.EnumValue<Ease> bossHideFadeEase;

    public final ModConfigSpec.BooleanValue enableBossHurt;

    public final ModConfigSpec.DoubleValue bossHurtShakeStrength;

    public final ModConfigSpec.DoubleValue bossHurtDuration;

    public BossTweenConfig(ModConfigSpec.Builder BUILDER) {
        BUILDER.translation("guitween.config.bossGroup").push("bossGroup");

        enableBossShow = BUILDER
                .translation("guitween.config.enableBossShow")
                .define("enableBossShow", true);

        bossShowDuration = BUILDER
                .translation("guitween.config.bossShowDuration")
                .defineInRange("bossShowDuration", 10d, 0, 1000f);

        bossShowEase = BUILDER
                .translation("guitween.config.bossShowEase")
                .defineEnum("bossShowEase", Ease.IN_OUT_SINE);

        bossShowFadeDuration = BUILDER
                .translation("guitween.config.bossShowFadeDuration")
                .defineInRange("bossShowFadeDuration", 10d, 0, 1000f);

        bossShowFadeEase = BUILDER
                .translation("guitween.config.bossShowFadeEase")
                .defineEnum("bossShowFadeEase", Ease.IN_OUT_SINE);

        enableBossHide = BUILDER
                .translation("guitween.config.enableBossHide")
                .define("enableBossHide", true);

        bossHideDuration = BUILDER
                .translation("guitween.config.bossHideDuration")
                .defineInRange("bossHideDuration", 6d, 0, 1000f);

        bossHideEase = BUILDER
                .translation("guitween.config.bossHideEase")
                .defineEnum("bossHideEase", Ease.IN_OUT_SINE);

        bossHideFadeDuration = BUILDER
                .translation("guitween.config.bossHideFadeDuration")
                .defineInRange("bossHideFadeDuration", 6d, 0, 1000f);

        bossHideFadeEase = BUILDER
                .translation("guitween.config.bossHideFadeEase")
                .defineEnum("bossHideFadeEase", Ease.IN_OUT_SINE);

        enableBossHurt = BUILDER
                .translation("guitween.config.enableBossHurt")
                .define("enableBossHurt", true);

        bossHurtShakeStrength = BUILDER
                .translation("guitween.config.bossHurtShakeStrength")
                .defineInRange("bossHurtShakeStrength", 1.5f, 0, 1000f);

        bossHurtDuration = BUILDER
                .translation("guitween.config.bossHurtDuration")
                .defineInRange("bossHurtDuration", 4f, 0, 1000f);
    }
}
