package com.remarxk.guitween.client.mixin.overflowingbars;

import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.util.TweenUtil;
import fuzs.overflowingbars.client.gui.ArmorBarRenderer;
import fuzs.overflowingbars.config.ClientConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
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
            method = "renderArmorBar(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/profiler/Profiler;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lfuzs/overflowingbars/client/gui/ArmorBarRenderer;renderArmorBar(Lnet/minecraft/client/gui/DrawContext;IIIIZZLfuzs/overflowingbars/config/ClientConfig$AbstractArmorRowConfig;)V"
            ),
            index = 4
    )
    private static int modifyArmorValue(int armorPoints) {
        float duration = GUITweenClient.CONFIG.armorDuration;

        if (GUITweenClient.CONFIG.isEnableArmor()) {
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
        if (GUITweenClient.CONFIG.isEnableArmor() && progress < 1) {
            gUITween$inArmorTween = true;

            gUITween$armorIsUp = gUITween$curArmorValue > gUITween$lastArmorValue;

            float originScale = GUITweenClient.CONFIG.upArmorScale;
            gUITween$armorScale = gUITween$armorIsUp ? TweenUtil.tween(originScale, 1, progress, GUITweenClient.CONFIG.upArmorEase.get()) : 1;

            float shakeStrength = GUITweenClient.CONFIG.downArmorShakeStrength;
            gUITween$armorDx = !gUITween$armorIsUp ? TweenUtil.shake(0, gUITween$armorChangeTick, duration, shakeStrength) : 0;
            gUITween$armorDy = !gUITween$armorIsUp ? TweenUtil.shake(1, gUITween$armorChangeTick, duration, shakeStrength) : 0;

            return gUITween$armorIsUp ? gUITween$curArmorValue : gUITween$lastArmorValue;
        }

        return armorPoints;
    }

    @Unique
    private static void gUITween$blitWrap(DrawContext guiGraphics, Identifier pAtlasLocation, int pX, int pY, float pUOffset, float pVOffset, int pWidth, int pHeight, int pTextureWidth, int pTextureHeight) {
        MatrixStack poseStack = guiGraphics.getMatrices();

        boolean needPlayTween = gUITween$inArmorTween;

        // 在渲染之前做自定义处理
        if (needPlayTween) {
            poseStack.push();

            if (gUITween$armorIsUp) {
                float centerX = pX + 4.5f;
                float centerY = pY + 4.5f;

                poseStack.translate(centerX, centerY, 0);
                poseStack.scale(gUITween$armorScale, gUITween$armorScale, 1);
                poseStack.translate(-centerX, -centerY, 0);
            }
            else {
                poseStack.translate(gUITween$armorDx, gUITween$armorDy, 0);
            }
        }

        guiGraphics.drawTexture(pAtlasLocation, pX, pY, pUOffset, pVOffset, pWidth, pHeight, pTextureWidth, pTextureHeight);

        if (needPlayTween) {
            poseStack.pop();
        }
    }

    @Redirect(
            method = "renderArmorBar(Lnet/minecraft/client/gui/DrawContext;IIIIZZLfuzs/overflowingbars/config/ClientConfig$AbstractArmorRowConfig;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V",
                    ordinal = 1
            )
    )
    private static void blit1(DrawContext instance, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        gUITween$blitWrap(instance, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    @Redirect(
            method = "renderArmorBar(Lnet/minecraft/client/gui/DrawContext;IIIIZZLfuzs/overflowingbars/config/ClientConfig$AbstractArmorRowConfig;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V",
                    ordinal = 2
            )
    )
    private static void blit2(DrawContext instance, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        gUITween$blitWrap(instance, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    @Redirect(
            method = "renderArmorBar(Lnet/minecraft/client/gui/DrawContext;IIIIZZLfuzs/overflowingbars/config/ClientConfig$AbstractArmorRowConfig;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V",
                    ordinal = 3
            )
    )
    private static void blit3(DrawContext instance, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        gUITween$blitWrap(instance, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    @Redirect(
            method = "renderArmorBar(Lnet/minecraft/client/gui/DrawContext;IIIIZZLfuzs/overflowingbars/config/ClientConfig$AbstractArmorRowConfig;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V",
                    ordinal = 4
            )
    )
    private static void blit4(DrawContext instance, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        gUITween$blitWrap(instance, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    @Redirect(
            method = "renderArmorBar(Lnet/minecraft/client/gui/DrawContext;IIIIZZLfuzs/overflowingbars/config/ClientConfig$AbstractArmorRowConfig;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V",
                    ordinal = 5
            )
    )
    private static void blit5(DrawContext instance, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        gUITween$blitWrap(instance, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    @Redirect(
            method = "renderArmorBar(Lnet/minecraft/client/gui/DrawContext;IIIIZZLfuzs/overflowingbars/config/ClientConfig$AbstractArmorRowConfig;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V",
                    ordinal = 0
            )
    )
    private static void blit0(DrawContext instance, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        gUITween$blitWrap(instance, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    @Inject(
            method = "renderArmorBar(Lnet/minecraft/client/gui/DrawContext;IIIIZZLfuzs/overflowingbars/config/ClientConfig$AbstractArmorRowConfig;)V",
            at = @At(
                    value = "TAIL"
            )
    )
    private static void renderArmorBarAfter(DrawContext guiGraphics, int posX, int posY, int vOffset, int armorPoints, boolean left, boolean unmodified, ClientConfig.AbstractArmorRowConfig config, CallbackInfo ci) {
        if (gUITween$inArmorTween) {
            gUITween$inArmorTween = false;

            gUITween$armorChangeTick += GUITweenUtility.getDeltaTicks();
        }
    }
}
