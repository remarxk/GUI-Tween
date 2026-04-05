package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.remarxk.guitween.mixinAccess.GuiItemRenderStateMixinAccess;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.GuiItemAtlas;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
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
    private GuiItemRenderState gUITween$guiItemRenderState;

    @Inject(
            method = "submitBlitFromItemAtlas",
            at = @At(
                    value = "HEAD"
            )
    )
    private void saveGUITween$guiItemRenderState(GuiItemRenderState itemState, GuiItemAtlas.SlotView slotView, CallbackInfo ci) {
        gUITween$guiItemRenderState = itemState;
    }

    @Redirect(
            method = "submitBlitFromItemAtlas",
            at = @At(
                    value = "NEW",
                    target = "(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/gui/render/TextureSetup;Lorg/joml/Matrix3x2f;IIIIFFFFILnet/minecraft/client/gui/navigation/ScreenRectangle;Lnet/minecraft/client/gui/navigation/ScreenRectangle;)Lnet/minecraft/client/renderer/state/gui/BlitRenderState;"
            )
    )
    private BlitRenderState redirectNewBlitRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1, int color, ScreenRectangle scissorArea, ScreenRectangle bounds) {
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

        return new BlitRenderState(pipeline, textureSetup, pose, x0, y0, x1, y1, u0, u1, v0, v1, color, scissorArea);
    }
}
