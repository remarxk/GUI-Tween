package com.remarxk.guitween.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Gui.class)
public interface GuiScreenAccessor {
    @Accessor("screen")
    void setGUITween$screen(Screen screen);

    @Accessor("screen")
    Screen getGUITween$screen();
}
