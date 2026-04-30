package com.remarxk.guitween.event;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.eventbus.api.Event;

public class PostScreenTickEvent extends Event {
    private final Screen screen;

    public PostScreenTickEvent(Screen screen) {
        this.screen = screen;
    }

    public Screen getScreen() {
        return screen;
    }
}
