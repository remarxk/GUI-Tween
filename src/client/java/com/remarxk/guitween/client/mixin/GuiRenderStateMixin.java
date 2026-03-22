package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.mixinAccess.GuiRenderStateMixinAccess;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import org.joml.Matrix3x2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderState.class)
public class GuiRenderStateMixin implements GuiRenderStateMixinAccess {
    @Unique
    private DrawContext gUITween$guiGraphics;

    @Override
    public void setGUITween$GuiGraphics(DrawContext guiGraphics) {
        gUITween$guiGraphics = guiGraphics;
    }

    @Override
    public DrawContext getGUITween$GuiGraphics() {
        return gUITween$guiGraphics;
    }

    @Inject(
            method = "addSpecialElement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/render/state/GuiRenderState$Layer;addSpecialElement(Lnet/minecraft/client/gui/render/state/special/SpecialGuiElementRenderState;)V"
            )
    )
    private void submitPicturesInPictureStateBefore(SpecialGuiElementRenderState state, CallbackInfo ci) {
        if (gUITween$guiGraphics != null) {
            GUITweenUtility.pushPictureMatrix(state, new Matrix3x2f(gUITween$guiGraphics.getMatrices()));
        }
    }
}
