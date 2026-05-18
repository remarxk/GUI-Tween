package com.remarxk.guitween.mixin;

import com.remarxk.guitween.GUITweenUtility;
import net.minecraft.client.gui.Font;
import net.minecraftforge.client.extensions.IForgeFont;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Font.class)
public abstract class FontMixin implements IForgeFont {
    @ModifyArg(
            method = "drawInternal(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;IIZ)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;adjustColor(I)I"
            ),
            index = 0
    )
    public int changeColor1(int color) {
        return gUITween$changeColorInternal(color);
    }

    @ModifyArg(
            method = "drawInternal(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;adjustColor(I)I"),
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
