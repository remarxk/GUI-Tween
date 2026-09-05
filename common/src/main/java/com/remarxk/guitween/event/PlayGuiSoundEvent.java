package com.remarxk.guitween.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PlayGuiSoundEvent {
    public enum SoundType {
        NORMAL_CLICK,
        PUNCH_CLICK,

        PICK_UP,
        OUTPUT_CREATE,
        OUTPUT_ADD,
        MOVE_FINISH,

        LACK_ITEM,
        SELECT_ITEM,
    }

    public final SoundType soundType;

    public PlayGuiSoundEvent(SoundType soundType) {
        this.soundType = soundType;
    }

    private static final List<Consumer<PlayGuiSoundEvent>> LISTENERS = new ArrayList<>();

    public static void register(Consumer<PlayGuiSoundEvent> listener) {
        LISTENERS.add(listener);
    }

    public static void post(PlayGuiSoundEvent event) {
        for (Consumer<PlayGuiSoundEvent> listener : LISTENERS) {
            listener.accept(event);
        }
    }
}
