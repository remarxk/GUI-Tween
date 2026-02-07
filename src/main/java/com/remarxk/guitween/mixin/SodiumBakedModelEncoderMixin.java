package com.remarxk.guitween.mixin;

import com.remarxk.guitween.GUITweenUtility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.immediate.model.BakedModelEncoder", remap = false)
public class SodiumBakedModelEncoderMixin {
    @ModifyVariable(
            method = "writeQuadVertices(Lnet/caffeinemc/mods/sodium/api/vertex/buffer/VertexBufferWriter;Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/caffeinemc/mods/sodium/client/model/quad/ModelQuadView;IIIZ)V",
            at = @At(value = "HEAD"),
            index = 3,
            argsOnly = true
    )
    private static int modifyAlpha(int color) {
        if (GUITweenUtility.hasItemAlpha()) {
            int a = (int)(((color >> 24) & 0xFF) * GUITweenUtility.peekItemAlpha());
            color = (a << 24) | (color & 0x00FFFFFF);
        }

        return color;
    }
}
