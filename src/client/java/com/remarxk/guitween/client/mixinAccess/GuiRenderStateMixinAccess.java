package com.remarxk.guitween.client.mixinAccess;

import net.minecraft.client.gui.DrawContext;

public interface GuiRenderStateMixinAccess {
    public void setGUITween$GuiGraphics(DrawContext guiGraphics);

    public DrawContext getGUITween$GuiGraphics();
}
