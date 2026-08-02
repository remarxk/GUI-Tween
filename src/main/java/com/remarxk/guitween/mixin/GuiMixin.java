package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.event.PlayGuiSoundEvent;
import com.remarxk.guitween.eventListener.HotbarChangeListener;
import com.remarxk.guitween.anim.AttackTween;
import com.remarxk.guitween.anim.Tween;
import com.remarxk.guitween.anim.UseTween;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.anim.TweenPool;
import com.remarxk.guitween.util.Ease;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow
    @Final
    private Minecraft minecraft;
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
            if (tween != null && (!GUITweenConfig.isEnableSelectMove() || HotbarChangeListener.scrollDir == 0)) {
                hasTween = true;

                scale = TweenUtil.tween(tween.startValue, tween.stopValue, tween.tick, tween.totalTick, tween.ease);

                if (!tween.rewind && tween.name.equals("zoomIn") && tween.tick == 0) {
                    NeoForge.EVENT_BUS.post(new PlayGuiSoundEvent(PlayGuiSoundEvent.SoundType.SELECT_ITEM));
                }

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

        if (GUITweenConfig.isEnableAttack()) {
            AttackTween attackTween = GUITweenUtility.getAttackTween();

            if (attackTween.isRunning() && !stack.isEmpty() && slot == attackTween.slot) {
                gUITween$inItemTween = true;
                angle = attackTween.getAngle();
                attackTween.update();
            }
//            else {
//                attackTween.stop();
//            }
        }

        if (GUITweenConfig.isEnableUse()) {
            UseTween usingTween = GUITweenUtility.getUsingTween();

            if (usingTween.isRunning() && !stack.isEmpty() && slot == usingTween.slot) {
                gUITween$inItemTween = true;
                scale = usingTween.getScale();
                usingTween.update();
            }
//                else {
//                    usingTween.stop();
//                }
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
            if (tween != null && (!GUITweenConfig.isEnableSelectMove() || HotbarChangeListener.scrollDir == 0)) {
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
                            tween.name = "zoomOut";
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

    @Unique
    private float gUITween$curSelectPos = -1000;

    @Redirect(
            method = "renderItemHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V",
                    ordinal = 1
            )
    )
    public void renderHotbarSelect(GuiGraphics guiGraphics, ResourceLocation sprite, int pX, int pY, int pUWidth, int pVHeight) {
        if (!GUITweenConfig.isEnableSelectMove()) {
            guiGraphics.blitSprite(sprite, pX, pY, pUWidth, pVHeight);
            return;
        }

        Player player = minecraft.player;
        if (player == null) {
            guiGraphics.blitSprite(sprite, pX, pY, pUWidth, pVHeight);
            return;
        }

        if (Math.abs(gUITween$curSelectPos - pX) < 1) {
            gUITween$curSelectPos = pX;
            HotbarChangeListener.scrollSelected = -1;
            HotbarChangeListener.scrollDir = 0;
            guiGraphics.blitSprite(sprite, pX, pY, pUWidth, pVHeight);
            return;
        }

        // 重置滚动状态
        if (HotbarChangeListener.scrollSelected >= 0 && HotbarChangeListener.scrollSelected != player.getInventory().selected) {
            HotbarChangeListener.scrollSelected = -1;
            HotbarChangeListener.scrollDir = 0;
        }

        int left = guiGraphics.guiWidth() / 2 - 91 - 1;
        int width = 9 * 20;
        int right = left + width + (pUWidth - 20) / 2;

        if (gUITween$curSelectPos == -1000) {
            gUITween$curSelectPos = pX;
        }

        float target = pX;

        float delta = GUITweenUtility.getDeltaTicks(); // 每帧经过的时间，单位秒
        float speed = GUITweenConfig.hotbar.selectMoveSpeed.get().floatValue(); // 过渡速度，每秒接近目标的比例

        // 根据 scrollDir 判断循环距离
        if (HotbarChangeListener.scrollDir != 0) {
            if (HotbarChangeListener.scrollDir < 0 && gUITween$curSelectPos < target) {
                target = left - (right - target) - 0.5f;
            }
            else if (HotbarChangeListener.scrollDir > 0 && gUITween$curSelectPos > target) {
                target = right + (target - left) + 0.5f;
            }

            gUITween$curSelectPos += (target - gUITween$curSelectPos) * (1 - (float)Math.exp(-speed * delta));

            if (gUITween$curSelectPos <= left - 20) {
                gUITween$curSelectPos = right - (left - gUITween$curSelectPos);
            }
            else if (gUITween$curSelectPos >= right) {
                gUITween$curSelectPos = left + gUITween$curSelectPos - right;
            }
        } else {
            gUITween$curSelectPos += (target - gUITween$curSelectPos) * (1 - (float)Math.exp(-speed * delta));
        }

        boolean needScissor = false;
        float mirrorX = 0;

        if (gUITween$curSelectPos < left) {
            needScissor = true;
            mirrorX = right - (left - gUITween$curSelectPos);
        } else if (gUITween$curSelectPos > right - pUWidth) {
            needScissor = true;
            mirrorX = left - (right - gUITween$curSelectPos);
        }

        if (needScissor) {
            guiGraphics.enableScissor(left + (pUWidth - 20) / 2, pY, right, pY + pVHeight);
            guiGraphics.blitSprite(sprite, (int) mirrorX, pY, pUWidth, pVHeight);
        }

        guiGraphics.blitSprite(sprite, (int) gUITween$curSelectPos, pY, pUWidth, pVHeight);

        if (needScissor) {
            guiGraphics.disableScissor();
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
            )
    )
    private static void renderArmorBefore(GuiGraphics guiGraphics, Player player, int y, int heartRows, int height, int x, CallbackInfo ci) {
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
            gUITween$inArmorTween = true;

            gUITween$armorIsUp = gUITween$curArmorValue > gUITween$lastArmorValue;

            float originScale = GUITweenConfig.hotbar.upArmorScale.get().floatValue();
            gUITween$armorScale = gUITween$armorIsUp ? TweenUtil.tween(originScale, 1, progress, GUITweenConfig.hotbar.upArmorEase.get()) : 1;

            float shakeStrength = GUITweenConfig.hotbar.downArmorShakeStrength.get().floatValue();
            gUITween$armorDx = !gUITween$armorIsUp ? TweenUtil.shake(0, gUITween$armorChangeTick, duration, shakeStrength) : 0;
            gUITween$armorDy = !gUITween$armorIsUp ? TweenUtil.shake(1, gUITween$armorChangeTick, duration, shakeStrength) : 0;
        }
    }

    @ModifyVariable(
            method = "renderArmor",
            at = @At(
                    value = "STORE"
            ),
            ordinal = 4
    )
    private static int modifyRenderArmorValue(int value) {
        if (gUITween$inArmorTween) {
            value = gUITween$armorIsUp ? gUITween$curArmorValue : gUITween$lastArmorValue;
        }

        return value;
    }

    @Unique
    private static void gUITween$renderAnimArmor(GuiGraphics guiGraphics, ResourceLocation sprite, int x, int y, int width, int height) {
        PoseStack poseStack = guiGraphics.pose();

        boolean needPlayTween = gUITween$inArmorTween;

        // 在渲染之前做自定义处理
        if (needPlayTween) {
            poseStack.pushPose();

            if (gUITween$armorIsUp) {
                float centerX = x + 4.5f;
                float centerY = y + 4.5f;

                poseStack.translate(centerX, centerY, 0);
                poseStack.scale(gUITween$armorScale, gUITween$armorScale, 1);
                poseStack.translate(-centerX, -centerY, 0);

            }
            else {
                poseStack.translate(gUITween$armorDx, gUITween$armorDy, 0);
            }
        }

        // 原始调用
        guiGraphics.blitSprite(sprite, x, y, width, height);

        if (needPlayTween) {
            poseStack.popPose();
        }
    }

    @Redirect(
            method = "renderArmor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V",
                    ordinal = 0
            )
    )
    private static void redirectBlitFullSprite(GuiGraphics guiGraphics, ResourceLocation sprite, int x, int y, int width, int height) {
        gUITween$renderAnimArmor(guiGraphics, sprite, x, y, width, height);
    }

    @Redirect(
            method = "renderArmor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V",
                    ordinal = 1
            )
    )
    private static void redirectBlitHalfSprite(GuiGraphics guiGraphics, ResourceLocation sprite, int x, int y, int width, int height) {
        gUITween$renderAnimArmor(guiGraphics, sprite, x, y, width, height);
    }

    @Inject(
            method = "renderArmor",
            at = @At(
                    value = "TAIL"
            )
    )
    private static void renderArmorAfter(GuiGraphics guiGraphics, Player player, int y, int heartRows, int height, int x, CallbackInfo ci) {
        if (gUITween$inArmorTween) {
            gUITween$inArmorTween = false;

            gUITween$armorChangeTick += GUITweenUtility.getDeltaTicks();
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
