package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.HotbarChangeListener;
import com.remarxk.guitween.anim.AttackTween;
import com.remarxk.guitween.anim.Tween;
import com.remarxk.guitween.anim.UseTween;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.anim.TweenPool;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
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

    @Inject(method = "renderSlot", at = @At("HEAD"))
    public void renderSlotBefore(GuiGraphics guiGraphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack stack, int seed, CallbackInfo ci){
        boolean hasTween = false;

        float scale = 1;

        int slot = seed - 1;

        if (GUITweenConfig.isEnableHoldItem()) {
            Tween tween = HotbarChangeListener.hotbarAnimStateMap.getOrDefault(slot, null);
            if (tween != null) {
                hasTween = true;

                scale = TweenUtil.tween(tween.startValue, tween.stopValue, tween.tick, tween.totalTick, tween.ease);

                float deltaTicks = GUITweenUtility.getDeltaTicks();
                if (!tween.rewind) {
                    tween.tick += deltaTicks;
                }
                else {
                    tween.tick -= deltaTicks;
                }
            }
        }

        if (hasTween) {
            float centerX = x + 8;
            float centerY = y + 8;

            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate(centerX, centerY, 0);
            poseStack.scale(scale, scale, 1.0F);
            poseStack.translate(-centerX, -centerY, 0);
        }
    }

    @Unique
    private boolean gUITween$inItemTween;

    @Inject(
            method = "renderSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V"
            )
    )
    public void renderSlotItemBefore(GuiGraphics guiGraphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack stack, int seed, CallbackInfo ci) {
        float angle = 0;
        float scale = 1;

        int slot = seed - 1;

        if (HotbarChangeListener.lastSelected == slot) {
            AttackTween attackTween = GUITweenUtility.getAttackTween();
            if (GUITweenConfig.isEnableAttack() && attackTween.isRunning()) {
                if (!stack.isEmpty() && slot == attackTween.slot) {
                    gUITween$inItemTween = true;
                    angle = attackTween.getAngle();
                    attackTween.update();
                }
                else {
                    attackTween.stop();
                }
            }

            UseTween usingTween = GUITweenUtility.getUsingTween();
            if (GUITweenConfig.isEnableUse() && usingTween.isRunning()) {
                if (!stack.isEmpty() && slot == usingTween.slot) {
                    gUITween$inItemTween = true;
                    scale = usingTween.getScale();
                    usingTween.update();
                }
                else {
                    usingTween.stop();
                }
            }
        }

        if (gUITween$inItemTween) {
            float centerX = x + 8;
            float centerY = y + 8;

            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate(centerX, centerY, 0);
            if (scale != 1) {
                poseStack.scale(scale, scale, 1);
            }
            if (angle != 0) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
            }
            poseStack.translate(-centerX, -centerY, 0);
        }
    }

    @Inject(
            method = "renderSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V",
                    shift = At.Shift.AFTER
            )
    )
    public void renderSlotItemAfter(GuiGraphics guiGraphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack stack, int seed, CallbackInfo ci) {
        if (gUITween$inItemTween) {
            gUITween$inItemTween = false;
            guiGraphics.pose().popPose();
        }
    }

    @Inject(method = "renderSlot", at = @At("TAIL"))
    public void renderSlotAfter(GuiGraphics guiGraphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack stack, int seed, CallbackInfo ci){
        boolean hasTween = false;

        if (GUITweenConfig.isEnableHoldItem()) {
            int slot = seed - 1;
            Tween tween = HotbarChangeListener.hotbarAnimStateMap.getOrDefault(slot, null);
            if (tween != null) {
                hasTween = true;

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
                        if (tween.stopValue <= 1) {
                            TweenPool.releaseTween(tween);
                            HotbarChangeListener.hotbarAnimStateMap.remove(slot);
                        }
                        else {
                            tween.ease = GUITweenConfig.hotbar.holdZoomOutEase.get();
                            tween.tick = 0;
                            tween.totalTick = GUITweenConfig.hotbar.holdZoomOutDuration.get().floatValue();
                            tween.startValue = tween.stopValue;
                            tween.stopValue = 1;
                            tween.rewind = false;
                        }
                    }
                }
            }
        }

        if (hasTween) {
            PoseStack poseStack = guiGraphics.pose();
            poseStack.popPose();
        }
    }

    @Unique
    private boolean gUITween$inSelectedItemNameTween;

    @Inject(
            method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V",
            at = @At(
                    value = "HEAD"
            )
    )
    public void renderSelectedItemNameBefore(GuiGraphics guiGraphics, int yShift, CallbackInfo ci) {
        if (!GUITweenConfig.isEnableSelectedItemName())
            return;

        // 如果动画结束，直接正常绘制
        if (HotbarChangeListener.animTick > GUITweenConfig.getSelectedItemNameDuration()) {
            return;
        }

        gUITween$inSelectedItemNameTween = true;

        float progress = HotbarChangeListener.animTick / GUITweenConfig.hotbar.selectedItemNameMoveDuration.get().floatValue();
        float dy = TweenUtil.tween(GUITweenConfig.hotbar.selectedItemNameMoveY.get().floatValue(), 0, progress, GUITweenConfig.hotbar.selectedItemNameMoveEase.get());

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        poseStack.translate(0, dy, 0);
    }

    @ModifyArg(
            method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/FastColor$ARGB32;color(II)I"
            ),
            index = 0
    )
    private int modifySelectedItemNameAlpha(int alpha) {
        if (gUITween$inSelectedItemNameTween) {
            float progress = HotbarChangeListener.animTick / GUITweenConfig.hotbar.selectedItemNameAlphaDuration.get().floatValue();
            alpha = (int) TweenUtil.tween(GUITweenUtility.iFontMinAlpha, alpha, progress, GUITweenConfig.hotbar.selectedItemNameAlphaEase.get());
        }

        return alpha;
    }

    @Inject(
            method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V",
            at = @At(
                    value = "TAIL"
            )
    )
    public void renderSelectedItemNameAfter(GuiGraphics guiGraphics, int yShift, CallbackInfo ci) {
        if (!gUITween$inSelectedItemNameTween) {
            return;
        }

        gUITween$inSelectedItemNameTween = false;

        // 推进动画时间
        HotbarChangeListener.animTick += GUITweenUtility.getDeltaTicks();

        PoseStack poseStack = guiGraphics.pose();
        poseStack.popPose();
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
        PoseStack poseStack = guiGraphics.pose();

        if (GUITweenConfig.isEnableLack()) {
            if (HotbarChangeListener.lackTick < GUITweenConfig.hotbar.lackDuration.get()) {
                gUITween$inLackTween = true;

                float duration = GUITweenConfig.hotbar.lackDuration.get().floatValue();
                float strength = GUITweenConfig.hotbar.lackShakeStrength.get().floatValue();

                float dx = TweenUtil.shake(0, HotbarChangeListener.lackTick, duration, strength);
                float dy = TweenUtil.shake(1, HotbarChangeListener.lackTick, duration, strength);

                poseStack.pushPose();
                poseStack.translate(dx, dy, 0);

                HotbarChangeListener.lackTick += GUITweenUtility.getDeltaTicks();
            }
        }
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
        PoseStack poseStack = guiGraphics.pose();

        if (gUITween$inLackTween) {
            gUITween$inLackTween = false;
            poseStack.popPose();
        }
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

    @Unique
    private static int gUITween$curArmorValue = -1;

    @Unique
    private static int gUITween$lastArmorValue = -1;

    @Unique
    private static float gUITween$armorChangeTick;

    @Final
    @Shadow
    private static ResourceLocation ARMOR_EMPTY_SPRITE;

    @Final
    @Shadow
    private static ResourceLocation ARMOR_HALF_SPRITE;

    @Final
    @Shadow
    private static ResourceLocation ARMOR_FULL_SPRITE;

    /**
     * @author remarxk
     * @reason add animation
     */
    @Overwrite
    private static void renderArmor(GuiGraphics guiGraphics, Player player, int y, int heartRows, int height, int x) {
        int i = player.getArmorValue();

        float duration = GUITweenConfig.hotbar.armorDuration.get().floatValue();

        if (GUITweenConfig.isEnableArmor()) {
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
        if (GUITweenConfig.isEnableArmor() && progress < 1) {
            RenderSystem.enableBlend();
            int j = y - (heartRows - 1) * height - 10;

            boolean isUp = gUITween$curArmorValue > gUITween$lastArmorValue;

            float originScale = GUITweenConfig.hotbar.upArmorScale.get().floatValue();
            float scale = isUp ? TweenUtil.tween(originScale, 1, progress, GUITweenConfig.hotbar.upArmorEase.get()) : 1;

            float shakeStrength = GUITweenConfig.hotbar.downArmorShakeStrength.get().floatValue();
            float dx = !isUp ? TweenUtil.shake(0, gUITween$armorChangeTick, duration, shakeStrength) : 0;
            float dy = !isUp ? TweenUtil.shake(1, gUITween$armorChangeTick, duration, shakeStrength) : 0;

            PoseStack poseStack = guiGraphics.pose();

            int targetArmor = isUp ? gUITween$curArmorValue : gUITween$lastArmorValue;

            for (int k = 0; k < 10; k++) {
                int l = x + k * 8;
                int showArmor = k * 2 + 1;

                if (showArmor <= targetArmor) {
                    ResourceLocation sprite = showArmor == targetArmor ? ARMOR_HALF_SPRITE : ARMOR_FULL_SPRITE;

                    poseStack.pushPose();

                    if (isUp) {
                        float centerX = l + 4.5f;
                        float centerY = j + 4.5f;

                        poseStack.translate(centerX, centerY, 0);
                        poseStack.scale(scale, scale, 1);
                        poseStack.translate(-centerX, -centerY, 0);

                    }
                    else {
                        poseStack.translate(dx, dy, 0);

                    }
                    guiGraphics.blitSprite(sprite, l, j, 9, 9);
                    poseStack.popPose();
                }

                if (showArmor > targetArmor) {
                    guiGraphics.blitSprite(ARMOR_EMPTY_SPRITE, l, j, 9, 9);
                }
            }

            RenderSystem.disableBlend();

            gUITween$armorChangeTick += GUITweenUtility.getDeltaTicks();
        }
        else {
            if (i > 0) {
                RenderSystem.enableBlend();
                int j = y - (heartRows - 1) * height - 10;

                for (int k = 0; k < 10; k++) {
                    int l = x + k * 8;
                    if (k * 2 + 1 < i) {
                        guiGraphics.blitSprite(ARMOR_FULL_SPRITE, l, j, 9, 9);
                    }

                    if (k * 2 + 1 == i) {
                        guiGraphics.blitSprite(ARMOR_HALF_SPRITE, l, j, 9, 9);
                    }

                    if (k * 2 + 1 > i) {
                        guiGraphics.blitSprite(ARMOR_EMPTY_SPRITE, l, j, 9, 9);
                    }
                }

                RenderSystem.disableBlend();
            }
        }
    }

    @Inject(
            method = "clear",
            at = @At(value = "TAIL")
    )
    public void onClear(CallbackInfo ci) {
        gUITween$lastLevel = -1;
    }
}
