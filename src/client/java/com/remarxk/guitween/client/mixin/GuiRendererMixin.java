package com.remarxk.guitween.client.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.remarxk.guitween.client.mixinAccess.GuiItemRenderStateMixinAccess;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import net.minecraft.client.gui.render.state.TexturedQuadGuiElementRenderState;
import net.minecraft.client.texture.TextureSetup;
import org.joml.Matrix3x2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {
    @Unique
    private ItemGuiElementRenderState gUITween$guiItemRenderState;

    @Inject(
            method = "prepareItem",
            at = @At(
                    value = "HEAD"
            )
    )
    private void saveGUITween$guiItemRenderState(ItemGuiElementRenderState state, float u, float v, int pixelsPerItem, int itemAtlasSideLength, CallbackInfo ci) {
        gUITween$guiItemRenderState = state;
    }

    @Redirect(
            method = "prepareItem",
            at = @At(
                    value = "NEW",
                    target = "(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/texture/TextureSetup;Lorg/joml/Matrix3x2f;IIIIFFFFILnet/minecraft/client/gui/ScreenRect;Lnet/minecraft/client/gui/ScreenRect;)Lnet/minecraft/client/gui/render/state/TexturedQuadGuiElementRenderState;"
            )
    )
    private TexturedQuadGuiElementRenderState redirectNewBlitRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x1, int y1, int x2, int y2, float u1, float u2, float v1, float v2, int color, ScreenRect scissorArea, ScreenRect bounds) {
        if ((Object) gUITween$guiItemRenderState instanceof GuiItemRenderStateMixinAccess access) {
            // 1. 提取原始颜色分量（分离 Alpha 和 RGB）
            int originalAlpha = (color >> 24) & 0xFF; // 原版 Alpha
            int red = (color >> 16) & 0xFF;           // 红
            int green = (color >> 8) & 0xFF;          // 绿
            int blue = color & 0xFF;                  // 蓝

            // 2. 获取自定义 Alpha 并校验范围（0~255）
            int customAlpha = Math.max(0, Math.min(255, access.getGUITween$alpha()));

            // 3. 预乘 Alpha 处理（仅对预乘管线生效）
            if (pipeline == RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA) {
                // 预乘公式：RGB = RGB * customAlpha / 255（保留整数精度）
                red = (red * customAlpha) / 255;
                green = (green * customAlpha) / 255;
                blue = (blue * customAlpha) / 255;
            }

            // 4. 重新组合颜色（确保格式正确：0xAARRGGBB）
            color = (customAlpha << 24) | (red << 16) | (green << 8) | blue;
        }

        return new TexturedQuadGuiElementRenderState(pipeline, textureSetup, pose, x1, y1, x2, y2, u1, u2, v1, v2, color, scissorArea);
    }
}
