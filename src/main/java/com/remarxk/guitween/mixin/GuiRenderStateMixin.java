package com.remarxk.guitween.mixin;

import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.mixinAccess.GuiRenderStateMixinAccess;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import org.joml.Matrix3x2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderState.class)
public class GuiRenderStateMixin implements GuiRenderStateMixinAccess {
    @Unique
    private GuiGraphics gUITween$guiGraphics;

    @Override
    public void setGUITween$GuiGraphics(GuiGraphics guiGraphics) {
        gUITween$guiGraphics = guiGraphics;
    }

    @Override
    public GuiGraphics getGUITween$GuiGraphics() {
        return gUITween$guiGraphics;
    }

    @Inject(
            method = "submitPicturesInPictureState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/render/state/GuiRenderState$Node;submitPicturesInPictureState(Lnet/minecraft/client/gui/render/state/pip/PictureInPictureRenderState;)V"
            )
    )
    private void submitPicturesInPictureStateBefore(PictureInPictureRenderState renderState, CallbackInfo ci) {
        if (gUITween$guiGraphics != null) {
            GUITweenUtility.pushPictureMatrix(renderState, new Matrix3x2f(gUITween$guiGraphics.pose()));
        }
    }
}
