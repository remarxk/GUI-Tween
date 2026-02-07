package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.HotbarChangeListener;
import com.remarxk.guitween.anim.Tween;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.anim.TweenPool;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.DeltaTracker;
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
    public void renderSlotBefore(GuiGraphics guiGraphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack stack, int seed, CallbackInfo ci){
        if (!GUITweenConfig.isEnableHoldItem())
            return;

        int slot = seed - 1;

        Tween tween = HotbarChangeListener.hotbarAnimStateMap.getOrDefault(slot, null);
        if (tween == null) {
            return;
        }

        float centerX = x + 8;
        float centerY = y + 8;

        float scale = TweenUtil.tween(tween.startValue, tween.stopValue, tween.tick, tween.totalTick, tween.ease);

        float deltaTicks = GUITweenUtility.getDeltaTicks();
        if (!tween.rewind) {
            tween.tick += deltaTicks;
        }
        else {
            tween.tick -= deltaTicks;
        }

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, 0);
        poseStack.scale(scale, scale, 1.0F);
        poseStack.translate(-centerX, -centerY, 0);
    }

    @Inject(method = "renderSlot", at = @At("TAIL"))
    public void renderSlotAfter(GuiGraphics guiGraphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack stack, int seed, CallbackInfo ci){
        if (!GUITweenConfig.isEnableHoldItem())
            return;

        int slot = seed - 1;
        Tween tween = HotbarChangeListener.hotbarAnimStateMap.getOrDefault(slot, null);
        if (tween == null)
            return;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.popPose();

        if (tween.rewind) {
            if (tween.tick <= 0) {
                if (tween.startValue <= 1) { // 放大动画倒转，结束后直接销毁
                    TweenPool.releaseTween(tween);
                    HotbarChangeListener.hotbarAnimStateMap.remove(slot);
                }
                else { // 缩小动画倒转，结束后继续缩小
                    tween.rewind = false;
                }
            }
        }
        else {
            if (tween.tick >= tween.totalTick) { // 放大结束进入缩小动画
                if (tween.stopValue > 1) {
                    tween.ease = GUITweenConfig.hotbar.holdZoomOutEase.get();
                    tween.tick = 0;
                    tween.totalTick = GUITweenConfig.hotbar.holdZoomOutDuration.get().floatValue();
                    tween.startValue = tween.stopValue;
                    tween.stopValue = 1;
                    tween.rewind = false;
                }
                else { // 缩小结束，直接销毁
                    TweenPool.releaseTween(tween);
                    HotbarChangeListener.hotbarAnimStateMap.remove(slot);
                }
            }
        }
    }

    @Unique
    private float gUITween$selectTick;

    @Unique
    private boolean gUITween$inLackTween;

    @Inject(
            method = "renderItemHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V",
                    ordinal = 1
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void renderItemHotbarSelectBefore(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci, Player player, ItemStack itemStack, HumanoidArm humanoidArm, int i) {
        if (!GUITweenConfig.isEnableLack())
            return;

        if (HotbarChangeListener.lackTick >= GUITweenConfig.hotbar.lackDuration.get())
            return;

        gUITween$inLackTween = true;

        PoseStack poseStack = guiGraphics.pose();

        float dx = TweenUtil.shake(0, HotbarChangeListener.lackTick, 6, GUITweenConfig.hotbar.lackShakeStrength.get().floatValue());
        float dy = TweenUtil.shake(1, HotbarChangeListener.lackTick, 6, GUITweenConfig.hotbar.lackShakeStrength.get().floatValue());

        poseStack.pushPose();
        poseStack.translate(dx, dy, 0);

        HotbarChangeListener.lackTick += GUITweenUtility.getDeltaTicks();
    }

    @Inject(
            method = "renderItemHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    public void renderItemHotbarSelectAfter(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!gUITween$inLackTween)
            return;

        gUITween$inLackTween = false;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.popPose();
    }

    @Inject(
            method = "renderExperienceLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I",
                    ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void renderExperienceLevelBefore(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci, int i, String s, int j, int k) {
        if (!GUITweenConfig.isEnableExp())
            return;

        if (gUITween$lastLevel == -1) {
            gUITween$lastLevel = i;
            gUITween$levelTextTick = GUITweenConfig.hotbar.expDuration.get().floatValue();
            return;
        }

        if (gUITween$lastLevel != i) {
            gUITween$lastLevel = i;
            gUITween$levelTextTick = 0;
        }

        if (gUITween$levelTextTick >= GUITweenConfig.hotbar.expDuration.get().floatValue()) {
            return;
        }

        gUITween$inLevelTextTween = true;

        Gui gui = (Gui) ((Object) this);

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        // 缩放中心为文本中心
        float cx = j + gui.getFont().width(s) / 2f;
        float cy = k + gui.getFont().lineHeight / 2f;

        float progress = gUITween$levelTextTick / GUITweenConfig.hotbar.expDuration.get().floatValue();
        float scale = TweenUtil.tween(GUITweenConfig.hotbar.expScale.get().floatValue(), 1, progress, GUITweenConfig.hotbar.expEase.get());

        poseStack.translate(cx, cy, 0);
        poseStack.scale(scale, scale, 1);
        poseStack.translate(-cx, -cy, 0);

        gUITween$levelTextTick += GUITweenUtility.getDeltaTicks();
    }

    @Inject(
            method = "renderExperienceLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I",
                    ordinal = 4,
                    shift = At.Shift.AFTER
            )
    )
    public void renderExperienceLevelAfter(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!gUITween$inLevelTextTween)
            return;

        gUITween$inLevelTextTween = false;
        guiGraphics.pose().popPose();
    }

    @Inject(
            method = "clear",
            at = @At(value = "TAIL")
    )
    public void onClear(CallbackInfo ci) {
        gUITween$lastLevel = -1;
    }
}
