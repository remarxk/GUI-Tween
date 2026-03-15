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

    public final ModConfigSpec.BooleanValue enableSelectedItemName;

    public final ModConfigSpec.DoubleValue selectedItemNameMoveDuration;

    public final ModConfigSpec.DoubleValue selectedItemNameMoveY;

    public final ModConfigSpec.EnumValue<Ease> selectedItemNameMoveEase;

    public final ModConfigSpec.DoubleValue selectedItemNameAlphaDuration;

    public final ModConfigSpec.EnumValue<Ease> selectedItemNameAlphaEase;

    public final ModConfigSpec.BooleanValue enableAttack;

    public final ModConfigSpec.DoubleValue attackMaxAngle;

    public final ModConfigSpec.BooleanValue enableUse;

    public final ModConfigSpec.DoubleValue useStrength;

    public final ModConfigSpec.BooleanValue enableLack;

    public final ModConfigSpec.DoubleValue lackDuration;

    public final ModConfigSpec.DoubleValue lackShakeStrength;

    public final ModConfigSpec.BooleanValue enableSelectMove;

    public final ModConfigSpec.DoubleValue selectMoveSpeed;

    public final ModConfigSpec.BooleanValue enableExp;

    public final ModConfigSpec.DoubleValue expDuration;

    public final ModConfigSpec.EnumValue<Ease> expEase;

    public final ModConfigSpec.DoubleValue expScale;

    public final ModConfigSpec.BooleanValue enableArmor;

    public final ModConfigSpec.DoubleValue armorDuration;

    public final ModConfigSpec.DoubleValue upArmorScale;

    public final ModConfigSpec.EnumValue<Ease> upArmorEase;

    public final ModConfigSpec.DoubleValue downArmorShakeStrength;

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

        enableSelectedItemName = builder
                .translation("guitween.config.enableSelectedItemName")
                .define("enableSelectedItemName", true);

        selectedItemNameMoveDuration = builder
                .translation("guitween.config.selectedItemNameMoveDuration")
                .defineInRange("selectedItemNameMoveDuration", 4d, 0d, 1000d);

        selectedItemNameMoveY = builder
                .translation("guitween.config.selectedItemNameMoveY")
                .defineInRange("selectedItemNameMoveY", 5d, 0d, 1000d);

        selectedItemNameMoveEase = builder
                .translation("guitween.config.selectedItemNameMoveEase")
                .defineEnum("selectedItemNameMoveEase", Ease.IN_OUT_SINE);

        selectedItemNameAlphaDuration = builder
                .translation("guitween.config.selectedItemNameAlphaDuration")
                .defineInRange("selectedItemNameAlphaDuration", 6d, 0, 1000);

        selectedItemNameAlphaEase = builder
                .translation("guitween.config.selectedItemNameAlphaEase")
                .defineEnum("selectedItemNameAlphaEase", Ease.IN_OUT_SINE);

        enableAttack = builder
                .translation("guitween.config.enableAttack")
                .define("enableAttack", true);

        attackMaxAngle = builder
                .translation("guitween.config.attackMaxAngle")
                .defineInRange("attackMaxAngle", 15d, 0d, 90d);

        enableUse = builder
                .translation("guitween.config.enableUse")
                .define("enableUse", true);

        useStrength = builder
                .translation("guitween.config.useStrength")
                .defineInRange("useStrength", 0.2f, 0d, 1000d);

        enableLack = builder
                .translation("guitween.config.enableLack")
                .define("enableLack", true);

        lackDuration = builder
                .translation("guitween.config.lackDuration")
                .defineInRange("lackDuration", 8d, 0, 1000d);

        lackShakeStrength = builder
                .translation("guitween.config.lackShakeStrength")
                .defineInRange("lackShakeStrength", 3d, 0, 1000);

        enableSelectMove = builder
                .translation("guitween.config.enableSelectMove")
                .define("enableSelectMove", true);

        selectMoveSpeed = builder
                .translation("guitween.config.selectMoveSpeed")
                .defineInRange("selectMoveSpeed", 1.5d, 0, 1000d);

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

        enableArmor = builder
                .translation("guitween.config.enableArmor")
                .define("enableArmor", true);

        armorDuration = builder
                .translation("guitween.config.armorDuration")
                .defineInRange("armorDuration", 4d, 0, 1000d);

        upArmorScale = builder
                .translation("guitween.config.upArmorScale")
                .defineInRange("upArmorScale", 1.5d, 0, 1000d);

        upArmorEase = builder
                .translation("guitween.config.upArmorEase")
                .defineEnum("upArmorEase", Ease.IN_OUT_SINE);

        downArmorShakeStrength = builder
                .translation("guitween.config.downArmorShakeStrength")
                .defineInRange("downArmorShakeStrength", 3, 0, 1000d);

        builder.pop();
    }
}
