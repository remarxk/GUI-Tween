package com.remarxk.guitween.mixin;

import com.remarxk.guitween.GUITweenUtility;
import net.minecraft.client.gui.Font;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Font.class)
public abstract class FontMixin implements net.neoforged.neoforge.client.extensions.IFontExtension {
    @ModifyArg(
            method = "drawInternal(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;adjustColor(I)I"),
            index = 0
    )
    public int changeColor(int color) {
        if (GUITweenUtility.isInTween(GUITweenUtility.TOOL_TIP)) {
            int a = (int)(((color >> 24) & 0xFF) * GUITweenUtility.getTweenValue(GUITweenUtility.TOOL_TIP_ALPHA));
            color = (a << 24) | (color & 0x00FFFFFF);
        }

        return color;
    }
}
