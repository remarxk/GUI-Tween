package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.remarxk.guitween.GUITweenUtility;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.joml.Matrix3x2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PictureInPictureRenderer.class)
public class PictureInPictureRendererMixin<T extends PictureInPictureRenderState> {
    @Unique
    private T gUITween$curRenderState;

    @Inject(
            method = "blitTexture",
            at = @At(
                    value = "HEAD"
            )
    )
    private void blitTextureBefore(T renderState, GuiRenderState guiRenderState, CallbackInfo ci) {
        gUITween$curRenderState = renderState;
    }

    @Redirect(
            method = "blitTexture",
            at = @At(
                    value = "NEW",
                    target = "(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/gui/render/TextureSetup;Lorg/joml/Matrix3x2f;IIIIFFFFILnet/minecraft/client/gui/navigation/ScreenRectangle;Lnet/minecraft/client/gui/navigation/ScreenRectangle;)Lnet/minecraft/client/renderer/state/gui/BlitRenderState;")
    )
    private BlitRenderState newBlitRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1, int color, ScreenRectangle scissorArea, ScreenRectangle bounds) {
        Matrix3x2f matrix3x2f = GUITweenUtility.popPictureMatrix(gUITween$curRenderState);
        if (matrix3x2f != null) {
            Matrix3x2f newMatrix = new Matrix3x2f(matrix3x2f);
            newMatrix.mul(pose);
            pose = newMatrix;
        }

        return new BlitRenderState(pipeline, textureSetup, pose, x0, y0, x1, y1, u0, u1, v0, v1, color, scissorArea, bounds);
    }
}
