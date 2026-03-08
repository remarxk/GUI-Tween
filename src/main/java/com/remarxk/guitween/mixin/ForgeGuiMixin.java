package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeGui.class)
public abstract class ForgeGuiMixin extends Gui {
    public ForgeGuiMixin(Minecraft pMinecraft, ItemRenderer pItemRenderer) {
        super(pMinecraft, pItemRenderer);
    }

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

    @Inject(
            method = "renderArmor",
            at = @At(
                    value = "HEAD"
            ),
            remap = false
    )
    private void renderArmorBefore(GuiGraphics guiGraphics, int width, int height, CallbackInfo ci) {
        LocalPlayer player = minecraft.player;
        if (player == null)
            return;

        int i = player.getArmorValue();

        float duration = GUITween.CONFIG.armorDuration;

        if (GUITween.CONFIG.isEnableArmor()) {
            if (i != gUITween$curArmorValue) {
                if (gUITween$curArmorValue != -1) {
                    gUITween$lastArmorValue = gUITween$curArmorValue;
                    gUITween$armorChangeTick = 0;
                }
                else {
                    gUITween$lastArmorValue = i;
                    gUITween$armorChangeTick = duration;
                }

                gUITween$curArmorValue = i;
            }
        }

        float progress = gUITween$armorChangeTick / duration;
        if (GUITween.CONFIG.isEnableArmor() && progress < 1) {
            gUITween$inArmorTween = true;

            gUITween$armorIsUp = gUITween$curArmorValue > gUITween$lastArmorValue;

            float originScale = GUITween.CONFIG.upArmorScale;
            gUITween$armorScale = gUITween$armorIsUp ? TweenUtil.tween(originScale, 1, progress, GUITween.CONFIG.upArmorEase.get()) : 1;

            float shakeStrength = GUITween.CONFIG.downArmorShakeStrength;
            gUITween$armorDx = !gUITween$armorIsUp ? TweenUtil.shake(0, gUITween$armorChangeTick, duration, shakeStrength) : 0;
            gUITween$armorDy = !gUITween$armorIsUp ? TweenUtil.shake(1, gUITween$armorChangeTick, duration, shakeStrength) : 0;
        }
    }

    @ModifyVariable(
            method = "renderArmor",
            at = @At(
                    value = "STORE"
            ),
            ordinal = 4,
            remap = false
    )
    private int modifyRenderArmorValue(int value) {
        if (gUITween$inArmorTween) {
            value = gUITween$armorIsUp ? gUITween$curArmorValue : gUITween$lastArmorValue;
        }

        return value;
    }

    @Unique
    private void gUITween$renderAnimArmor(GuiGraphics guiGraphics, ResourceLocation pAtlasLocation, int pX, int pY, int pUOffset, int pVOffset, int pUWidth, int pVHeight) {
        PoseStack poseStack = guiGraphics.pose();

        boolean needPlayTween = gUITween$inArmorTween;

        // 在渲染之前做自定义处理
        if (needPlayTween) {
            poseStack.pushPose();

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

        // 原始调用
        guiGraphics.blit(pAtlasLocation, pX, pY, pUOffset, pVOffset, pUWidth, pVHeight);

        if (needPlayTween) {
            poseStack.popPose();
        }
    }

    @Redirect(
            method = "renderArmor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V",
                    ordinal = 0
            )
    )
    private void redirectBlitFullSprite(GuiGraphics guiGraphics, ResourceLocation pAtlasLocation, int pX, int pY, int pUOffset, int pVOffset, int pUWidth, int pVHeight) {
        gUITween$renderAnimArmor(guiGraphics, pAtlasLocation, pX, pY, pUOffset, pVOffset, pUWidth, pVHeight);
    }

    @Redirect(
            method = "renderArmor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V",
                    ordinal = 1
            )
    )
    private void redirectBlitHalfSprite(GuiGraphics guiGraphics, ResourceLocation pAtlasLocation, int pX, int pY, int pUOffset, int pVOffset, int pUWidth, int pVHeight) {
        gUITween$renderAnimArmor(guiGraphics, pAtlasLocation, pX, pY, pUOffset, pVOffset, pUWidth, pVHeight);
    }

    @Inject(
            method = "renderArmor",
            at = @At(
                    value = "TAIL"
            ),
            remap = false
    )
    private void renderArmorAfter(GuiGraphics guiGraphics, int width, int height, CallbackInfo ci) {
        if (gUITween$inArmorTween) {
            gUITween$inArmorTween = false;

            gUITween$armorChangeTick += GUITweenUtility.getDeltaTicks();
        }
    }
}
