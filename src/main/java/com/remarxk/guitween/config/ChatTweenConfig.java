package com.remarxk.guitween.config;

import com.remarxk.guitween.util.Ease;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ChatTweenConfig {
    public final ModConfigSpec.BooleanValue enableChat;

    public final ModConfigSpec.DoubleValue openMoveDuration;

    public final ModConfigSpec.EnumValue<Ease> openMoveEase;

    public final ModConfigSpec.DoubleValue openGradientDuration;

    public final ModConfigSpec.EnumValue<Ease> openGradientEase;

    public final ModConfigSpec.BooleanValue enableChatComp;

    public final ModConfigSpec.DoubleValue compMoveDuration;

    public final ModConfigSpec.EnumValue<Ease> compMoveEase;

    public final ModConfigSpec.DoubleValue compGradientDuration;

    public final ModConfigSpec.EnumValue<Ease> compGradientEase;

    public ChatTweenConfig(ModConfigSpec.Builder builder) {
        builder.translation("guitween.config.chatGroup").push("chatGroup");

        enableChat = builder
                .translation("guitween.config.enableChat")
                .define("enableChat", true);

        openMoveDuration = builder
                .translation("guitween.config.chatOpenMoveDuration")
                .defineInRange("chatOpenMoveDuration", 4, 0d, 1000d);

        openMoveEase = builder
                .translation("guitween.config.chatOpenMoveEase")
                .defineEnum("chatOpenMoveEase", Ease.IN_OUT_SINE);

        openGradientDuration = builder
                .translation("guitween.config.chatOpenGradientDuration")
                .defineInRange("chatOpenGradientDuration", 6, 0d, 1000d);

        openGradientEase = builder
                .translation("guitween.config.chatOpenGradientEase")
                .defineEnum("chatOpenGradientEase", Ease.IN_OUT_SINE);

        enableChatComp = builder
                .translation("guitween.config.enableChatComp")
                .define("enableChatComp", true);

        compMoveDuration = builder
                .translation("guitween.config.chatCompMoveDuration")
                .defineInRange("chatCompMoveDuration", 4, 0d, 1000d);

        compMoveEase = builder
                .translation("guitween.config.chatCompMoveEase")
                .defineEnum("chatCompMoveEase", Ease.IN_OUT_SINE);

        compGradientDuration = builder
                .translation("guitween.config.chatCompGradientDuration")
                .defineInRange("chatCompGradientDuration", 6, 0d, 1000d);

        compGradientEase = builder
                .translation("guitween.config.chatCompGradientEase")
                .defineEnum("chatCompGradientEase", Ease.IN_OUT_SINE);

        builder.pop();
    }
}
