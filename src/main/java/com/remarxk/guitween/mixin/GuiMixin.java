package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.HotbarChangeListener;
import com.remarxk.guitween.anim.Tween;
import com.remarxk.guitween.anim.TweenPool;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.Ease;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Gui.class)
public class GuiMixin {
    @Unique
    private int gUITween$lastLevel = -1;

    @Unique
    private boolean gUITween$inLevelTextTween;

    @Unique
    private float gUITween$levelTextTick;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "renderSlot", at = @At("HEAD"))
    public void renderSlotBefore(GuiGraphics pGuiGraphics, int pX, int pY, float pPartialTick, Player pPlayer, ItemStack pStack, int pSeed, CallbackInfo ci){
        if (!GUITween.CONFIG.isEnableHoldItem())
            return;

        int slot = pSeed - 1;

        Tween tween = HotbarChangeListener.hotbarAnimStateMap.getOrDefault(slot, null);
        if (tween == null) {
            return;
        }

        float centerX = pX + 8;
        float centerY = pY + 8;

        float scale = TweenUtil.tween(tween.startValue, tween.stopValue, tween.tick, tween.totalTick, tween.ease);

        float deltaTicks = GUITweenUtility.getDeltaTicks();
        if (!tween.rewind) {
            tween.tick += deltaTicks;
            if (tween.tick >= tween.totalTick) {
                if (tween.stopValue > 1) {
                    tween.ease = GUITween.CONFIG.holdZoomOutEase.get();
                    tween.tick = 0;
                    tween.totalTick = GUITween.CONFIG.holdZoomOutDuration;
                    tween.startValue = tween.stopValue;
                    tween.stopValue = 1;
                    tween.rewind = false;
                }
            }
        }
        else {
            tween.tick -= deltaTicks;
        }

        PoseStack poseStack = pGuiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, 0);
        poseStack.scale(scale, scale, 1.0F);
        poseStack.translate(-centerX, -centerY, 0);
    }

    @Inject(method = "renderSlot", at = @At("TAIL"))
    public void renderSlotAfter(GuiGraphics pGuiGraphics, int pX, int pY, float pPartialTick, Player pPlayer, ItemStack pStack, int pSeed, CallbackInfo ci){
        if (!GUITween.CONFIG.isEnableHoldItem())
            return;

        int slot = pSeed - 1;
        Tween tween = HotbarChangeListener.hotbarAnimStateMap.getOrDefault(slot, null);
        if (tween == null)
            return;

        PoseStack poseStack = pGuiGraphics.pose();
        poseStack.popPose();

        if (tween.rewind) {
            if (tween.startValue <= 1 && tween.tick <= 0) {
                TweenPool.releaseTween(tween);
                HotbarChangeListener.hotbarAnimStateMap.remove(slot);
            }
        }
        else {
            if (tween.stopValue <= 1 && tween.tick >= tween.totalTick) {
                TweenPool.releaseTween(tween);
                HotbarChangeListener.hotbarAnimStateMap.remove(slot);
            }
        }
    }

    @Unique
    private float gUITween$selectTick;

    @Unique
    private boolean gUITween$inLackTween;

    @Inject(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V",
                    ordinal = 1
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void renderItemHotbarSelectBefore(float pPartialTick, GuiGraphics pGuiGraphics, CallbackInfo ci, Player player, ItemStack itemStack, HumanoidArm humanoidArm, int i) {
        if (!GUITween.CONFIG.isEnableLack())
            return;

        if (HotbarChangeListener.lackTick >= GUITween.CONFIG.lackDuration)
            return;

        gUITween$inLackTween = true;

        PoseStack poseStack = pGuiGraphics.pose();

        float dx = TweenUtil.shake(0, HotbarChangeListener.lackTick, 6, GUITween.CONFIG.lackShakeStrength);
        float dy = TweenUtil.shake(1, HotbarChangeListener.lackTick, 6, GUITween.CONFIG.lackShakeStrength);

        poseStack.pushPose();
        poseStack.translate(dx, dy, 0);

        HotbarChangeListener.lackTick += GUITweenUtility.getDeltaTicks();
    }

    @Inject(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    public void renderItemHotbarSelectAfter(float pPartialTick, GuiGraphics pGuiGraphics, CallbackInfo ci) {
        if (!gUITween$inLackTween)
            return;

        gUITween$inLackTween = false;

        PoseStack poseStack = pGuiGraphics.pose();
        poseStack.popPose();
    }

    @Inject(
            method = "renderExperienceBar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I",
                    ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void renderExperienceLevelBefore(GuiGraphics pGuiGraphics, int pX, CallbackInfo ci, int i, String s, int i1, int j1) {
        if (!GUITween.CONFIG.isEnableExp())
            return;

        if (minecraft.player == null)
            return;

        int level = minecraft.player.experienceLevel;

        if (gUITween$lastLevel == -1) {
            gUITween$lastLevel = level;
            gUITween$levelTextTick = GUITween.CONFIG.expDuration;
            return;
        }

        if (gUITween$lastLevel != level) {
            gUITween$lastLevel = level;
            gUITween$levelTextTick = 0;
        }

        if (gUITween$levelTextTick >= GUITween.CONFIG.expDuration) {
            return;
        }

        gUITween$inLevelTextTween = true;

        Gui gui = (Gui) ((Object) this);

        PoseStack poseStack = pGuiGraphics.pose();
        poseStack.pushPose();

        // 缩放中心为文本中心
        float cx = i1 + gui.getFont().width(s) / 2f;
        float cy = j1 + gui.getFont().lineHeight / 2f;

        float progress = gUITween$levelTextTick / GUITween.CONFIG.expDuration;
        float scale = TweenUtil.tween(GUITween.CONFIG.expScale, 1, progress, GUITween.CONFIG.expEase.get());

        poseStack.translate(cx, cy, 0);
        poseStack.scale(scale, scale, 1);
        poseStack.translate(-cx, -cy, 0);

        gUITween$levelTextTick += GUITweenUtility.getDeltaTicks();
    }

    @Inject(
            method = "renderExperienceBar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I",
                    ordinal = 4,
                    shift = At.Shift.AFTER
            )
    )
    public void renderExperienceLevelAfter(GuiGraphics pGuiGraphics, int pX, CallbackInfo ci) {
        if (!gUITween$inLevelTextTween)
            return;

        gUITween$inLevelTextTween = false;
        pGuiGraphics.pose().popPose();
    }

    @Inject(
            method = "clear",
            at = @At(value = "TAIL")
    )
    public void onClear(CallbackInfo ci) {
        gUITween$lastLevel = -1;
    }
}
