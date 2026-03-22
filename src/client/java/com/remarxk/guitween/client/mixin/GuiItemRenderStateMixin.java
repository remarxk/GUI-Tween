package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.client.mixinAccess.GuiItemRenderStateMixinAccess;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemGuiElementRenderState.class)
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
