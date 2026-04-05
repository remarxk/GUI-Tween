package com.remarxk.guitween.mixinAccess;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface GuiRenderStateMixinAccess {
    public void setGUITween$GuiGraphics(GuiGraphicsExtractor guiGraphics);

    public GuiGraphicsExtractor getGUITween$GuiGraphics();
}
