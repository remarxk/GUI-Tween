package com.remarxk.guitween.mixin;

import com.remarxk.guitween.mixinAccess.GuiItemRenderStateMixinAccess;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GuiItemRenderState.class)
public class GuiItemRenderStateMixin implements GuiItemRenderStateMixinAccess {
    @Unique
    private int gUITween$alpha = 0xFF;

    @Override
    public void setGUITween$alpha(int alpha) {
        gUITween$alpha = alpha;
    }

    @Override
    public int getGUITween$alpha() {
        return gUITween$alpha;
    }
}
