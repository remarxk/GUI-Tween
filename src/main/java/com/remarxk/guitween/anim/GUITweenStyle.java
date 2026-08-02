package com.remarxk.guitween.anim;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.TranslatableEnum;

public enum GUITweenStyle implements TranslatableEnum {
    DEFAULT,
    SIMPLE,
    COMPLETE;

    @Override
    public Component getTranslatedName() {
        return Component.translatable("guitween.config.style.enum." + name().toLowerCase());
    }
}
