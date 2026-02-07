package com.remarxk.guitween.mixin;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.immediate.model.BakedModelEncoder", remap = false)
public class EmbeddiumBakedModelEncoderMixin {
    @ModifyVariable(
            method = "writeQuadVertices(Lnet/caffeinemc/mods/sodium/api/vertex/buffer/VertexBufferWriter;Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lme/jellysquid/mods/sodium/client/model/quad/ModelQuadView;III)V",
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
