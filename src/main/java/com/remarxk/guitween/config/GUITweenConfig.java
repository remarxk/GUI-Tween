package com.remarxk.guitween.config;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.util.Ease;

import net.neoforged.neoforge.common.ModConfigSpec;

public class GUITweenConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue enable;

    /// window

    public static final ModConfigSpec.BooleanValue enableWindow;

    public static final ModConfigSpec.BooleanValue enableDebugWindow;

    public static final ModConfigSpec.DoubleValue windowMoveDuration;

    public static final ModConfigSpec.EnumValue<Ease> windowMoveEase;

    public static final ModConfigSpec.DoubleValue windowMoveX;

    public static final ModConfigSpec.DoubleValue windowMoveY;

    public static final ModConfigSpec.DoubleValue windowGradientDuration;

    public static final ModConfigSpec.EnumValue<Ease> windowGradientEase;

    /// screen item

    public static final ModConfigSpec.BooleanValue enableHover;

    public static final ModConfigSpec.DoubleValue hoverDuration;

    public static final ModConfigSpec.EnumValue<Ease> hoverEase;

    public static final ModConfigSpec.DoubleValue hoverScale;

    public static final ModConfigSpec.BooleanValue enableTooltip;

    public static final ModConfigSpec.DoubleValue tooltipDuration;

    public static final ModConfigSpec.EnumValue<Ease> tooltipEase;

    public static final ModConfigSpec.BooleanValue enableClickItem;

    public static final ModConfigSpec.DoubleValue clickItemDuration;

    public static final ModConfigSpec.BooleanValue enableOutput;

    public static final ModConfigSpec.DoubleValue outputDuration;

    public static final ModConfigSpec.EnumValue<Ease> outputEase;

    /// hotbar

    public static final ModConfigSpec.BooleanValue enableHoldItem;

    public static final ModConfigSpec.DoubleValue holdZoomInDuration;

    public static final ModConfigSpec.DoubleValue holdZoomOutDuration;

    public static final ModConfigSpec.DoubleValue holdZoomScale;

    public static final ModConfigSpec.EnumValue<Ease> holdZoomInEase;

    public static final ModConfigSpec.EnumValue<Ease> holdZoomOutEase;

    public static final ModConfigSpec.BooleanValue enableLack;

    public static final ModConfigSpec.DoubleValue lackDuration;

    public static final ModConfigSpec.DoubleValue lackShakeStrength;

    public static final ModConfigSpec.BooleanValue enableExp;

    public static final ModConfigSpec.DoubleValue expDuration;

    public static final ModConfigSpec.EnumValue<Ease> expEase;

    public static final ModConfigSpec.DoubleValue expScale;

    /// chat

    public static final ModConfigSpec.BooleanValue enableChat;

    public static final ModConfigSpec.DoubleValue chatOpenMoveDuration;

    public static final ModConfigSpec.EnumValue<Ease> chatOpenMoveEase;

    public static final ModConfigSpec.DoubleValue chatOpenMoveX;

    public static final ModConfigSpec.DoubleValue chatOpenMoveY;

    public static final ModConfigSpec.DoubleValue chatOpenGradientDuration;

    public static final ModConfigSpec.EnumValue<Ease> chatOpenGradientEase;

    public static final ModConfigSpec.BooleanValue enableChatComp;

    public static final ModConfigSpec.DoubleValue chatCompMoveDuration;

    public static final ModConfigSpec.EnumValue<Ease> chatCompMoveEase;

    public static final ModConfigSpec.DoubleValue chatCompMoveX;

    public static final ModConfigSpec.DoubleValue chatCompMoveY;

    public static final ModConfigSpec.DoubleValue chatCompGradientDuration;

    public static final ModConfigSpec.EnumValue<Ease> chatCompGradientEase;

    public static final ModConfigSpec SPEC;

    static {
        GUITween.LOGGER.info("GUITweenConfig 初始化");

        enable = BUILDER
                .translation("guitween.config.enable")
                .define("enable", true);

        enableDebugWindow = BUILDER
                .translation("guitween.config.enableDebugWindow")
                .define("enableDebugWindow", false);

        BUILDER.translation("guitween.config.windowGroup").push("windowGroup");

        enableWindow = BUILDER
                .translation("guitween.config.enableWindow")
                .define("enableWindow", true);

        windowMoveDuration = BUILDER
                .translation("guitween.config.windowMoveDuration")
                .defineInRange("windowMoveDuration", 6d, 0, 1000d);

        windowMoveEase = BUILDER
                .translation("guitween.config.windowMoveEase")
                .defineEnum("windowMoveEase", Ease.OUT_BACK);

        windowMoveX = BUILDER
                .translation("guitween.config.windowMoveX")
                .defineInRange("windowMoveX", 0d, -10000, 10000);

        windowMoveY = BUILDER
                .translation("guitween.config.windowMoveY")
                .defineInRange("windowMoveY", 50d, -10000, 10000);

        windowGradientDuration = BUILDER
                .translation("guitween.config.windowGradientDuration")
                .defineInRange("windowGradientDuration", 8d, 0, 1000d);

        windowGradientEase = BUILDER
                .translation("guitween.config.windowGradientEase")
                .defineEnum("windowGradientEase", Ease.IN_OUT_SINE);

        BUILDER.pop();

        BUILDER.translation("guitween.config.screenItemGroup").push("screenItemGroup");

        enableHover = BUILDER
                .translation("guitween.config.enableHover")
                .define("enableHover", true);

        hoverDuration = BUILDER
                .translation("guitween.config.hoverDuration")
                .defineInRange("hoverDuration", 4d, 0, 1000d);

        hoverEase = BUILDER
                .translation("guitween.config.hoverEase")
                .defineEnum("hoverEase", Ease.IN_OUT_SINE);

        hoverScale = BUILDER
                .translation("guitween.config.hoverScale")
                .defineInRange("hoverScale", 1.2d, 1d, 10d);

        enableTooltip = BUILDER
                .translation("guitween.config.enableTooltip")
                .define("enableTooltip", true);

        tooltipDuration = BUILDER
                .translation("guitween.config.tooltipDuration")
                .defineInRange("tooltipDuration", 6, 0d, 1000d);

        tooltipEase = BUILDER
                .translation("guitween.config.tooltipEase")
                .defineEnum("tooltipEase", Ease.OUT_CIRC);

        enableClickItem = BUILDER
                .translation("guitween.config.enableClickItem")
                .define("enableClickItem", true);

        clickItemDuration = BUILDER
                .translation("guitween.config.itemClickDuration")
                .defineInRange("itemClickDuration", 5d, 0, 1000d);

        enableOutput = BUILDER
                .translation("guitween.config.enableOutput")
                .define("enableOutput", true);

        outputDuration = BUILDER
                .translation("guitween.config.outputDuration")
                .defineInRange("outputDuration", 5d, 0, 1000d);

        outputEase = BUILDER
                .translation("guitween.config.outputEase")
                .defineEnum("outputEase", Ease.OUT_BACK);

        BUILDER.pop();

        BUILDER.translation("guitween.config.hotbarGroup").push("hotbarGroup");

        enableHoldItem = BUILDER
                .translation("guitween.config.enableHoldItem")
                .define("enableHoldItem", true);

        holdZoomInDuration = BUILDER
                .translation("guitween.config.holdZoomInDuration")
                .defineInRange("holdZoomInDuration", 8d, 0, 1000);

        holdZoomInEase = BUILDER
                .translation("guitween.config.holdZoomInEase")
                .defineEnum("holdZoomInEase", Ease.OUT_QUINT);

        holdZoomScale = BUILDER
                .translation("guitween.config.holdZoomScale")
                .defineInRange("holdZoomScale", 1.4d, 1d, 10d);

        holdZoomOutDuration = BUILDER
                .translation("guitween.config.holdZoomOutDuration")
                .defineInRange("holdZoomOutDuration", 2d, 0, 1000);

        holdZoomOutEase = BUILDER
                .translation("guitween.config.holdZoomOutEase")
                .defineEnum("holdZoomOutEase", Ease.OUT_QUART);

        enableLack = BUILDER
                .translation("guitween.config.enableLack")
                .define("enableLack", true);

        lackDuration = BUILDER
                .translation("guitween.config.lackDuration")
                .defineInRange("lackDuration", 8d, 0, 1000d);

        lackShakeStrength = BUILDER
                .translation("guitween.config.lackShakeStrength")
                .defineInRange("lackShakeStrength", 3d, 0, 1000);

        enableExp = BUILDER
                .translation("guitween.config.enableExp")
                .define("enableExp", true);

        expDuration = BUILDER
                .translation("guitween.config.expDuration")
                .defineInRange("expDuration", 4d, 0, 1000d);

        expEase = BUILDER
                .translation("guitween.config.expEase")
                .defineEnum("expEase", Ease.OUT_BACK);

        expScale = BUILDER
                .translation("guitween.config.expScale")
                .defineInRange("expScale", 4d, 1, 1000);

        BUILDER.pop();

        BUILDER.translation("guitween.config.chatGroup").push("chatGroup");

        enableChat = BUILDER
                .translation("guitween.config.enableChat")
                .define("enableChat", true);

        chatOpenMoveDuration = BUILDER
                .translation("guitween.config.chatOpenMoveDuration")
                .defineInRange("chatOpenMoveDuration", 4, 0d, 1000d);

        chatOpenMoveEase = BUILDER
                .translation("guitween.config.chatOpenMoveEase")
                .defineEnum("chatOpenMoveEase", Ease.IN_OUT_SINE);

        chatOpenMoveX = BUILDER
                .translation("guitween.config.chatOpenMoveX")
                .defineInRange("chatOpenMoveX", 0, -10000d, 10000d);

        chatOpenMoveY = BUILDER
                .translation("guitween.config.chatOpenMoveY")
                .defineInRange("chatOpenMoveY", 50, -10000d, 10000d);

        chatOpenGradientDuration = BUILDER
                .translation("guitween.config.chatOpenGradientDuration")
                .defineInRange("chatOpenGradientDuration", 6, 0d, 1000d);

        chatOpenGradientEase = BUILDER
                .translation("guitween.config.chatOpenGradientEase")
                .defineEnum("chatOpenGradientEase", Ease.IN_OUT_SINE);

        enableChatComp = BUILDER
                .translation("guitween.config.enableChatComp")
                .define("enableChatComp", true);

        chatCompMoveDuration = BUILDER
                .translation("guitween.config.chatCompMoveDuration")
                .defineInRange("chatCompMoveDuration", 4, 0d, 1000d);

        chatCompMoveEase = BUILDER
                .translation("guitween.config.chatCompMoveEase")
                .defineEnum("chatCompMoveEase", Ease.IN_OUT_SINE);

        chatCompMoveX = BUILDER
                .translation("guitween.config.chatCompMoveX")
                .defineInRange("chatCompMoveX", -500, -10000d, 10000d);

        chatCompMoveY = BUILDER
                .translation("guitween.config.chatCompMoveY")
                .defineInRange("chatCompMoveY", 0, -10000d, 10000d);

        chatCompGradientDuration = BUILDER
                .translation("guitween.config.chatCompGradientDuration")
                .defineInRange("chatCompGradientDuration", 6, 0d, 1000d);

        chatCompGradientEase = BUILDER
                .translation("guitween.config.chatCompGradientEase")
                .defineEnum("chatCompGradientEase", Ease.IN_OUT_SINE);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static float getHoldItemTotalDuration() {
        return holdZoomInDuration.get().floatValue() + holdZoomOutDuration.get().floatValue();
    }

    public static float getChatOpenMaxDuration() {
        return Math.max(chatOpenMoveDuration.get().floatValue(), chatOpenGradientDuration.get().floatValue());
    }

    public static float getChatCompMaxDuration() {
        return Math.max(chatCompMoveDuration.get().floatValue(), chatCompGradientDuration.get().floatValue());
    }

    public static boolean isEnable() {
        return enable.get();
    }

    public static boolean isEnableWindow() {
        return isEnable() && enableWindow.get();
    }

    public static boolean isEnableDebugWindow() {
        return isEnable() && enableDebugWindow.get();
    }

    public static boolean isEnableHoverItem() {
        return isEnable() && enableHover.get();
    }

    public static boolean isEnableTooltip() {
        return isEnable() && enableTooltip.get();
    }

    public static boolean isEnableClickItem() {
        return isEnable() && enableClickItem.get();
    }

    public static boolean isEnableOutput() {
        return isEnable() && enableOutput.get();
    }

    public static boolean isEnableHoldItem() {
        return isEnable() && enableHoldItem.get();
    }

    public static boolean isEnableLack() {
        return isEnable() && enableLack.get();
    }

    public static boolean isEnableExp() {
        return isEnable() && enableExp.get();
    }

    public static boolean isEnableChat() {
        return isEnable() && enableChat.get();
    }

    public static boolean isEnableChatComp() {
        return isEnable() && enableChatComp.get();
    }
}
