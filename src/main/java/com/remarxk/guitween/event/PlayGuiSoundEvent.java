package com.remarxk.guitween.event;

import net.neoforged.bus.api.Event;

public class PlayGuiSoundEvent extends Event {
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
}
