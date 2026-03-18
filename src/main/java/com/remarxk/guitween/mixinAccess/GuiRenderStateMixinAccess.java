package com.remarxk.guitween.mixinAccess;

import net.minecraft.client.gui.GuiGraphics;

public interface GuiRenderStateMixinAccess {
    public void setGUITween$GuiGraphics(GuiGraphics guiGraphics);

    public GuiGraphics getGUITween$GuiGraphics();
}
