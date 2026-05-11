package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.client.GUITweenUtility;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin implements SynchronousResourceReloader {
    @Unique
    private static final HashMap<Identifier, RenderLayer> gUITween$cacheTransRenderType = new HashMap<>();

    @Unique
    private BakedModel gUITween$bakedModel;

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At(value = "HEAD"))
    private void getBakedModel(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        gUITween$bakedModel = model;
    }

    @Redirect(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/RenderLayers;getItemLayer(Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/client/render/RenderLayer;"
            )
    )
    private RenderLayer modifyRenderType(ItemStack stack, boolean direct) {
        if (GUITweenUtility.hasItemAlpha()) {
            Sprite sprite = gUITween$bakedModel.getParticleSprite();
            if (sprite == null) {
                // 如果模型没有 particle，使用默认缺失纹理
                sprite = net.minecraft.client.MinecraftClient.getInstance().getBakedModelManager().getMissingModel().getParticleSprite();
            }

            Identifier resourceLocation = sprite.getAtlasId();
            RenderLayer transRenderType = gUITween$cacheTransRenderType.getOrDefault(resourceLocation, null);
            if (transRenderType == null) {
                transRenderType = RenderLayer.getItemEntityTranslucentCull(resourceLocation);
                gUITween$cacheTransRenderType.put(resourceLocation, transRenderType);
            }
            return transRenderType;
        }
        return RenderLayers.getItemLayer(stack, direct);
    }

    @ModifyArg(
            method = "renderBakedItemQuads",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/VertexConsumer;quad(Lnet/minecraft/client/util/math/MatrixStack$Entry;Lnet/minecraft/client/render/model/BakedQuad;FFFFII)V"),
            index = 5
    )
    private float modifyQuadAlpha(float alpha) {
        if (GUITweenUtility.hasItemAlpha()) {
            alpha = alpha * GUITweenUtility.peekItemAlpha();
        }

        return alpha;
    }
}
