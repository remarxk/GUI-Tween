package com.remarxk.guitween.mixin.overflowingbars;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.TweenUtil;
import fuzs.overflowingbars.client.gui.ArmorBarRenderer;
import fuzs.overflowingbars.config.ClientConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ArmorBarRenderer.class)
public class ArmorBarRendererMixin {
    @Unique
    private static int gUITween$curArmorValue = -1;

    @Unique
    private static int gUITween$lastArmorValue = -1;

    @Unique
    private static float gUITween$armorChangeTick;

    @Unique
    private static boolean gUITween$armorIsUp;

    @Unique
    private static float gUITween$armorScale;

    @Unique
    private static float gUITween$armorDx;

    @Unique
    private static float gUITween$armorDy;

    @Unique
    private static boolean gUITween$inArmorTween;

    @ModifyArg(
            method = "renderArmorBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/entity/player/Player;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lfuzs/overflowingbars/client/gui/ArmorBarRenderer;renderArmorBar(Lnet/minecraft/client/gui/GuiGraphics;IIIIZZLfuzs/overflowingbars/config/ClientConfig$AbstractArmorRowConfig;)V"
            ),
            index = 4
    )
    private static int modifyArmorValue(int armorPoints) {
        float duration = GUITweenConfig.hotbar.armorDuration.get().floatValue();

        if (GUITweenConfig.isEnableArmor()) {
            if (armorPoints != gUITween$curArmorValue) {
                if (gUITween$curArmorValue != -1) {
                    gUITween$lastArmorValue = gUITween$curArmorValue;
                    gUITween$armorChangeTick = 0;
                }
                else {
                    gUITween$lastArmorValue = armorPoints;
                    gUITween$armorChangeTick = duration;
                }

                gUITween$curArmorValue = armorPoints;
            }
        }

        float progress = gUITween$armorChangeTick / duration;
        if (GUITweenConfig.isEnableArmor() && progress < 1) {
            gUITween$inArmorTween = true;

            gUITween$armorIsUp = gUITween$curArmorValue > gUITween$lastArmorValue;

            float originScale = GUITweenConfig.hotbar.upArmorScale.get().floatValue();
            gUITween$armorScale = gUITween$armorIsUp ? TweenUtil.tween(originScale, 1, progress, GUITweenConfig.hotbar.upArmorEase.get()) : 1;

            float shakeStrength = GUITweenConfig.hotbar.downArmorShakeStrength.get().floatValue();
            gUITween$armorDx = !gUITween$armorIsUp ? TweenUtil.shake(0, gUITween$armorChangeTick, duration, shakeStrength) : 0;
            gUITween$armorDy = !gUITween$armorIsUp ? TweenUtil.shake(1, gUITween$armorChangeTick, duration, shakeStrength) : 0;

            return gUITween$armorIsUp ? gUITween$curArmorValue : gUITween$lastArmorValue;
        }

        return armorPoints;
    }

    @Unique
    private static void gUITween$blitWrap(GuiGraphics guiGraphics, RenderPipeline pipeline, Identifier atlas, int pX, int pY, float pUOffset, float pVOffset, int pWidth, int pHeight, int pTextureWidth, int pTextureHeight) {
        Matrix3x2fStack poseStack = guiGraphics.pose();

        boolean needPlayTween = gUITween$inArmorTween;

        // 在渲染之前做自定义处理
        if (needPlayTween) {
            poseStack.pushMatrix();

            if (gUITween$armorIsUp) {
                float centerX = pX + 4.5f;
                float centerY = pY + 4.5f;

                poseStack.translate(centerX, centerY);
                poseStack.scale(gUITween$armorScale, gUITween$armorScale);
                poseStack.translate(-centerX, -centerY);
            }
            else {
                poseStack.translate(gUITween$armorDx, gUITween$armorDy);
            }
        }

        guiGraphics.blit(pipeline, atlas, pX, pY, pUOffset, pVOffset, pWidth, pHeight, pTextureWidth, pTextureHeight);

        if (needPlayTween) {
            poseStack.popMatrix();
        }
    }

    @Redirect(
            method = "renderArmorBar(Lnet/minecraft/client/gui/GuiGraphics;IIIIZZLfuzs/overflowingbars/config/ClientConfig$AbstractArmorRowConfig;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V",
                    ordinal = 0
            )
    )
    private static void blit0(GuiGraphics guiGraphics, RenderPipeline pipeline, Identifier atlas, int pX, int pY, float pUOffset, float pVOffset, int pWidth, int pHeight, int pTextureWidth, int pTextureHeight) {
        gUITween$blitWrap(guiGraphics, pipeline, atlas, pX, pY, pUOffset, pVOffset, pWidth, pHeight, pTextureWidth, pTextureHeight);
    }

    @Redirect(
            method = "renderArmorBar(Lnet/minecraft/client/gui/GuiGraphics;IIIIZZLfuzs/overflowingbars/config/ClientConfig$AbstractArmorRowConfig;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V",
                    ordinal = 1
            )
    )
    private static void blit1(GuiGraphics guiGraphics, RenderPipeline pipeline, Identifier atlas, int pX, int pY, float pUOffset, float pVOffset, int pWidth, int pHeight, int pTextureWidth, int pTextureHeight) {
        gUITween$blitWrap(guiGraphics, pipeline, atlas, pX, pY, pUOffset, pVOffset, pWidth, pHeight, pTextureWidth, pTextureHeight);
    }

    @Redirect(
            method = "renderArmorBar(Lnet/minecraft/client/gui/GuiGraphics;IIIIZZLfuzs/overflowingbars/config/ClientConfig$AbstractArmorRowConfig;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V",
                    ordinal = 2
            )
    )
    private static void blit2(GuiGraphics guiGraphics, RenderPipeline pipeline, Identifier atlas, int pX, int pY, float pUOffset, float pVOffset, int pWidth, int pHeight, int pTextureWidth, int pTextureHeight) {
        gUITween$blitWrap(guiGraphics, pipeline, atlas, pX, pY, pUOffset, pVOffset, pWidth, pHeight, pTextureWidth, pTextureHeight);
    }

    @Redirect(
            method = "renderArmorBar(Lnet/minecraft/client/gui/GuiGraphics;IIIIZZLfuzs/overflowingbars/config/ClientConfig$AbstractArmorRowConfig;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V",
                    ordinal = 3
            )
    )
    private static void blit3(GuiGraphics guiGraphics, RenderPipeline pipeline, Identifier atlas, int pX, int pY, float pUOffset, float pVOffset, int pWidth, int pHeight, int pTextureWidth, int pTextureHeight) {
        gUITween$blitWrap(guiGraphics, pipeline, atlas, pX, pY, pUOffset, pVOffset, pWidth, pHeight, pTextureWidth, pTextureHeight);
    }

    @Redirect(
            method = "renderArmorBar(Lnet/minecraft/client/gui/GuiGraphics;IIIIZZLfuzs/overflowingbars/config/ClientConfig$AbstractArmorRowConfig;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V",
                    ordinal = 4
            )
    )
    private static void blit4(GuiGraphics guiGraphics, RenderPipeline pipeline, Identifier atlas, int pX, int pY, float pUOffset, float pVOffset, int pWidth, int pHeight, int pTextureWidth, int pTextureHeight) {
        gUITween$blitWrap(guiGraphics, pipeline, atlas, pX, pY, pUOffset, pVOffset, pWidth, pHeight, pTextureWidth, pTextureHeight);
    }

    @Redirect(
            method = "renderArmorBar(Lnet/minecraft/client/gui/GuiGraphics;IIIIZZLfuzs/overflowingbars/config/ClientConfig$AbstractArmorRowConfig;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V",
                    ordinal = 5
            )
    )
    private static void blit5(GuiGraphics guiGraphics, RenderPipeline pipeline, Identifier atlas, int pX, int pY, float pUOffset, float pVOffset, int pWidth, int pHeight, int pTextureWidth, int pTextureHeight) {
        gUITween$blitWrap(guiGraphics, pipeline, atlas, pX, pY, pUOffset, pVOffset, pWidth, pHeight, pTextureWidth, pTextureHeight);
    }

    @Inject(
            method = "renderArmorBar(Lnet/minecraft/client/gui/GuiGraphics;IIIIZZLfuzs/overflowingbars/config/ClientConfig$AbstractArmorRowConfig;)V",
            at = @At(
                    value = "TAIL"
            )
    )
    private static void renderArmorBarAfter(GuiGraphics guiGraphics, int posX, int posY, int vOffset, int armorPoints, boolean left, boolean vanillaLike, ClientConfig.AbstractArmorRowConfig config, CallbackInfo ci) {
        if (gUITween$inArmorTween) {
            gUITween$inArmorTween = false;

            gUITween$armorChangeTick += GUITweenUtility.getDeltaTicks();
        }
    }
}
