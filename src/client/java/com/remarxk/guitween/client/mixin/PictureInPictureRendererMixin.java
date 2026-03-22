package com.remarxk.guitween.client.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.remarxk.guitween.client.GUITweenUtility;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.TexturedQuadGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import net.minecraft.client.texture.TextureSetup;
import org.joml.Matrix3x2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpecialGuiElementRenderer.class)
public class PictureInPictureRendererMixin<T extends SpecialGuiElementRenderState> {
    @Unique
    private T gUITween$curRenderState;

    @Inject(
            method = "renderElement",
            at = @At(
                    value = "HEAD"
            )
    )
    private void blitTextureBefore(T renderState, GuiRenderState guiRenderState, CallbackInfo ci) {
        gUITween$curRenderState = renderState;
    }

    @Redirect(
            method = "renderElement",
            at = @At(
                    value = "NEW",
                    target = "(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/texture/TextureSetup;Lorg/joml/Matrix3x2f;IIIIFFFFILnet/minecraft/client/gui/ScreenRect;Lnet/minecraft/client/gui/ScreenRect;)Lnet/minecraft/client/gui/render/state/TexturedQuadGuiElementRenderState;")
    )
    private TexturedQuadGuiElementRenderState newBlitRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x1, int y1, int x2, int y2, float u1, float u2, float v1, float v2, int color, ScreenRect scissorArea, ScreenRect bounds) {
        Matrix3x2f matrix3x2f = GUITweenUtility.popPictureMatrix(gUITween$curRenderState);
        if (matrix3x2f != null) {
            Matrix3x2f newMatrix = new Matrix3x2f(matrix3x2f);
            newMatrix.mul(pose);
            pose = newMatrix;
        }

        return new TexturedQuadGuiElementRenderState(pipeline, textureSetup, pose, x1, y1, x2, y2, u1, u2, v1, v2, color, scissorArea, bounds);
    }
}
