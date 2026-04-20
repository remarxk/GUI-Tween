package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin implements ResourceManagerReloadListener {
    @Unique
    private static final HashMap<ResourceLocation, RenderType> gUITween$cacheTransRenderType = new HashMap<>();

    @Unique
    private BakedModel gUITween$bakedModel;

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void getBakedModel(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel p_model, CallbackInfo ci) {
        gUITween$bakedModel = p_model;
    }

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getFoilBufferDirect(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            ),
            index = 1 // RenderType 在参数列表中的索引
    )
    private RenderType modifyRenderType(RenderType renderType) {
        if (GUITweenUtility.hasItemAlpha()) {
            TextureAtlasSprite sprite = gUITween$bakedModel.getParticleIcon(ModelData.EMPTY);
            if (sprite == null) {
                // 如果模型没有 particle，使用默认缺失纹理
                sprite = net.minecraft.client.Minecraft.getInstance().getModelManager().getMissingModel().getParticleIcon();
            }

            ResourceLocation resourceLocation = sprite.atlasLocation();
            RenderType transRenderType = gUITween$cacheTransRenderType.getOrDefault(resourceLocation, null);
            if (transRenderType == null) {
                transRenderType = RenderType.ENTITY_TRANSLUCENT_CULL.apply(resourceLocation);
                gUITween$cacheTransRenderType.put(resourceLocation, transRenderType);
            }
            return transRenderType;
        }
        return renderType;
    }

    @ModifyArg(
            method = "renderQuadList",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;FFFFIIZ)V"),
            index = 5
    )
    private float modifyQuadAlpha(float alpha) {
        if (GUITweenUtility.hasItemAlpha()) {
            alpha = alpha * GUITweenUtility.peekItemAlpha();
        }

        return alpha;
    }
}
