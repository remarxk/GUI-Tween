package com.remarxk.guitween.config;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.util.Ease;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class GUITweenConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue enable;

    public static final ModConfigSpec.BooleanValue enableDebugWindow;

    /// window
    public static final WindowTweenConfig window;
    public static final ModConfigSpec windowSpec;

    /// screen item
    public static final WindowItemTweenConfig windowItem;
    public static final ModConfigSpec windowItemSpec;

    /// hotbar
    public static final HotbarTweenConfig hotbar;
    public static final ModConfigSpec hotbarSpec;

    /// chat
    public static final ChatTweenConfig chat;
    public static final ModConfigSpec chatSpec;

    public static final ModConfigSpec SPEC;

    static {
        GUITween.LOGGER.info("GUITweenConfig 初始化");

        enable = BUILDER
                .translation("guitween.config.enable")
                .define("enable", true);

        enableDebugWindow = BUILDER
                .translation("guitween.config.enableDebugWindow")
                .define("enableDebugWindow", false);

        Pair<WindowTweenConfig, ModConfigSpec> windowPair =
                BUILDER.configure(WindowTweenConfig::new);

        window = windowPair.getLeft();
        windowSpec = windowPair.getRight();

        Pair<WindowItemTweenConfig, ModConfigSpec> windowItemPair =
                BUILDER.configure(WindowItemTweenConfig::new);

        windowItem = windowItemPair.getLeft();
        windowItemSpec = windowItemPair.getRight();

        Pair<HotbarTweenConfig, ModConfigSpec> hotbarPair =
                BUILDER.configure(HotbarTweenConfig::new);

        hotbar = hotbarPair.getLeft();
        hotbarSpec = hotbarPair.getRight();

        Pair<ChatTweenConfig, ModConfigSpec> chatPair =
                BUILDER.configure(ChatTweenConfig::new);

        chat = chatPair.getLeft();
        chatSpec = chatPair.getRight();

        SPEC = BUILDER.build();
    }

    public static float getHoldItemTotalDuration() {
        return hotbar.holdZoomInDuration.get().floatValue() + hotbar.holdZoomOutDuration.get().floatValue();
    }

    public static float getChatOpenMaxDuration() {
        return Math.max(chat.openMoveDuration.get().floatValue(), chat.openGradientDuration.get().floatValue());
    }

    public static float getChatCompMaxDuration() {
        return Math.max(chat.compMoveDuration.get().floatValue(), chat.compGradientDuration.get().floatValue());
    }

    public static boolean isDisableTweenWindow(String screenName) {
        return window.disableNames.get().contains(screenName);
    }

    public static boolean isEnable() {
        return enable.get();
    }

    public static boolean isEnableWindow() {
        return isEnable() && window.enable.get();
    }

    public static boolean isEnableDebugWindow() {
        return isEnable() && enableDebugWindow.get();
    }

    public static boolean isEnableHoverItem() {
        return isEnable() && windowItem.enableHover.get();
    }

    public static boolean isEnableTooltip() {
        return isEnable() && windowItem.enableTooltip.get();
    }

    public static boolean isEnableClickItem() {
        return isEnable() && windowItem.enableClickItem.get();
    }

    public static boolean isEnableOutput() {
        return isEnable() && windowItem.enableOutput.get();
    }

    public static boolean isEnableHoldItem() {
        return isEnable() && hotbar.enableHoldItem.get();
    }

    public static boolean isEnableLack() {
        return isEnable() && hotbar.enableLack.get();
    }

    public static boolean isEnableExp() {
        return isEnable() && hotbar.enableExp.get();
    }

    public static boolean isEnableChat() {
        return isEnable() && chat.enableChat.get();
    }

    public static boolean isEnableChatComp() {
        return isEnable() && chat.enableChatComp.get();
    }
}
