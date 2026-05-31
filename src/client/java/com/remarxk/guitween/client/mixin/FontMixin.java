package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.client.GUITweenUtility;
import net.minecraft.client.font.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TextRenderer.class)
public abstract class FontMixin {
    @ModifyArg(
            method = "drawInternal(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;IIZ)I",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;tweakTransparency(I)I"),
            index = 0
    )
    public int changeColor1(int color) {
        return gUITween$changeColorInternal(color);
    }

    @ModifyArg(
            method = "drawInternal(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/font/TextRenderer;tweakTransparency(I)I"
            ),
            index = 0
    )
    public int changeColor2(int color) {
        return gUITween$changeColorInternal(color);
    }

    @Unique
    private int gUITween$changeColorInternal(int color) {
        if (GUITweenUtility.hasFontAlpha()) {
            int a = (color >> 24) & 0xFF;
            if (a == 0) {
                a = 255;
            }
            a = (int) (a * GUITweenUtility.peekFontAlpha());
            color = (a << 24) | (color & 0x00FFFFFF);
        }

        return color;
    }
}
