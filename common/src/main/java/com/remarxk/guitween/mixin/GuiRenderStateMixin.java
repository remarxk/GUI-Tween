package com.remarxk.guitween.mixin;

import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.mixinAccess.GuiRenderStateMixinAccess;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.joml.Matrix3x2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderState.class)
public abstract class GuiRenderStateMixin implements GuiRenderStateMixinAccess {
    @Unique
    private GuiGraphicsExtractor gUITween$guiGraphics;

    @Override
    public void setGUITween$GuiGraphics(GuiGraphicsExtractor guiGraphics) {
        gUITween$guiGraphics = guiGraphics;
    }

    @Override
    public GuiGraphicsExtractor getGUITween$GuiGraphics() {
        return gUITween$guiGraphics;
    }

    @Inject(
            method = "addPicturesInPictureState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/state/gui/GuiRenderState$Node;addPicturesInPictureState(Lnet/minecraft/client/renderer/state/gui/pip/PictureInPictureRenderState;)V"
            )
    )
    private void submitPicturesInPictureStateBefore(PictureInPictureRenderState picturesInPictureState, CallbackInfo ci) {
        if (gUITween$guiGraphics != null && GUITweenUtility.enablePictureMatrix) {
            GUITweenUtility.pushPictureMatrix(picturesInPictureState, new Matrix3x2f(gUITween$guiGraphics.pose()));
        }
    }
}
