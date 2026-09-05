package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.anim.AttackTween;
import com.remarxk.guitween.anim.Tween;
import com.remarxk.guitween.anim.TweenPool;
import com.remarxk.guitween.anim.UseTween;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.event.PlayGuiSoundEvent;
import com.remarxk.guitween.eventListener.HotbarChangeListener;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "extractSlot", at = @At("HEAD"))
    public void renderSlotBefore(GuiGraphicsExtractor graphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack itemStack, int seed, CallbackInfo ci){
        boolean hasTween = false;

        float scale = 1;

        int slot = seed - 1;

        if (GUITweenConfig.isEnableHoldItem()) {
            Tween tween = HotbarChangeListener.hotbarAnimStateMap.getOrDefault(slot, null);
            if (tween != null && (!GUITweenConfig.isEnableSelectMove() || HotbarChangeListener.scrollDir == 0)) {
                hasTween = true;

                if (!tween.rewind && tween.name.equals("zoomIn") && tween.tick == 0) {
                    PlayGuiSoundEvent.post(new PlayGuiSoundEvent(PlayGuiSoundEvent.SoundType.SELECT_ITEM));
                }

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

            Matrix3x2fStack poseStack = graphics.pose();
            poseStack.pushMatrix();
            poseStack.translate(centerX, centerY);
            poseStack.scale(scale, scale);
            poseStack.translate(-centerX, -centerY);
        }
    }

    @Unique
    private boolean gUITween$inItemTween;

    @Inject(
            method = "extractSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V"
            )
    )
    public void renderSlotItemBefore(GuiGraphicsExtractor graphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack itemStack, int seed, CallbackInfo ci) {
        float angle = 0;
        float scale = 1;

        int slot = seed - 1;

        if (GUITweenConfig.isEnableAttack()) {
            AttackTween attackTween = GUITweenUtility.getAttackTween();

            if (attackTween.isRunning() && !itemStack.isEmpty() && slot == attackTween.slot) {
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

            if (usingTween.isRunning() && !itemStack.isEmpty() && slot == usingTween.slot) {
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

            Matrix3x2fStack poseStack = graphics.pose();
            poseStack.pushMatrix();
            poseStack.translate(centerX, centerY);
            if (scale != 1) {
                poseStack.scale(scale, scale);
            }
            if (angle != 0) {
                poseStack.rotate((float) Math.toRadians(angle));
            }
            poseStack.translate(-centerX, -centerY);
        }
    }

    @Inject(
            method = "extractSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V",
                    shift = At.Shift.AFTER
            )
    )
    public void renderSlotItemAfter(GuiGraphicsExtractor graphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack itemStack, int seed, CallbackInfo ci) {
        if (gUITween$inItemTween) {
            gUITween$inItemTween = false;
            graphics.pose().popMatrix();
        }
    }

    @Inject(method = "extractSlot", at = @At("RETURN"))
    public void renderSlotAfter(GuiGraphicsExtractor graphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack itemStack, int seed, CallbackInfo ci){
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
                            tween.ease = GUITweenConfig.holdZoomOutEase();
                            tween.tick = 0;
                            tween.totalTick = GUITweenConfig.holdZoomOutDuration();
                            tween.startValue = tween.stopValue;
                            tween.stopValue = 1;
                            tween.rewind = false;
                        }
                    }
                }
            }
        }

        if (hasTween) {
            Matrix3x2fStack poseStack = graphics.pose();
            poseStack.popMatrix();
        }
    }

    @Unique
    private boolean gUITween$inLackTween;

    @Inject(
            method = "extractItemHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
                    ordinal = 1
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void renderItemHotbarSelectBefore(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci, Player player, ItemStack offhand, HumanoidArm offhandArm, int screenCenter, int hotbarWidth, int halfHotbar) {
        Matrix3x2fStack poseStack = graphics.pose();

        if (GUITweenConfig.isEnableLack()) {
            if (HotbarChangeListener.lackTick < GUITweenConfig.lackDuration()) {
                gUITween$inLackTween = true;

                float duration = GUITweenConfig.lackDuration();
                float strength = GUITweenConfig.lackShakeStrength();

                float dx = TweenUtil.shake(0, HotbarChangeListener.lackTick, duration, strength);
                float dy = TweenUtil.shake(1, HotbarChangeListener.lackTick, duration, strength);

                poseStack.pushMatrix();
                poseStack.translate(dx, dy);

                HotbarChangeListener.lackTick += GUITweenUtility.getDeltaTicks();
            }
        }
    }

    @Unique
    private float gUITween$curSelectPos = -1000;

    @Redirect(
            method = "extractItemHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
                    ordinal = 1
            )
    )
    public void renderHotbarSelect(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, Identifier location, int x, int y, int width, int height) {
        if (!GUITweenConfig.isEnableSelectMove()) {
            guiGraphics.blitSprite(renderPipeline, location, x, y, width, height);
            return;
        }

        Player player = minecraft.player;
        if (player == null) {
            guiGraphics.blitSprite(renderPipeline, location, x, y, width, height);
            return;
        }

        if (Math.abs(gUITween$curSelectPos - x) < 1) {
            gUITween$curSelectPos = x;
            HotbarChangeListener.scrollSelected = -1;
            HotbarChangeListener.scrollDir = 0;
            guiGraphics.blitSprite(renderPipeline, location, x, y, width, height);
            return;
        }

        // 重置滚动状态
        if (HotbarChangeListener.scrollSelected >= 0 && HotbarChangeListener.scrollSelected != player.getInventory().getSelectedSlot()) {
            HotbarChangeListener.scrollSelected = -1;
            HotbarChangeListener.scrollDir = 0;
        }

        int left = guiGraphics.guiWidth() / 2 - 91 - 1;
        int hotbarWidth = 9 * 20;
        int right = left + hotbarWidth + (width - 20) / 2;

        if (gUITween$curSelectPos == -1000) {
            gUITween$curSelectPos = x;
        }

        float target = x;

        float delta = GUITweenUtility.getDeltaTicks(); // 每帧经过的时间，单位秒
        float speed = GUITweenConfig.selectMoveSpeed(); // 过渡速度，每秒接近目标的比例

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
        } else if (gUITween$curSelectPos > right - width) {
            needScissor = true;
            mirrorX = left - (right - gUITween$curSelectPos);
        }

        if (needScissor) {
            guiGraphics.enableScissor(left + (width - 20) / 2, y, right, y + height);
            guiGraphics.blitSprite(renderPipeline, location, (int) mirrorX, y, width, height);
        }

        guiGraphics.blitSprite(renderPipeline, location, (int) gUITween$curSelectPos, y, width, height);

        if (needScissor) {
            guiGraphics.disableScissor();
        }
    }

    @Inject(
            method = "extractItemHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    public void renderItemHotbarSelectAfter(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Matrix3x2fStack poseStack = graphics.pose();

        if (gUITween$inLackTween) {
            gUITween$inLackTween = false;
            poseStack.popMatrix();
        }
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
            method = "extractArmor",
            at = @At(
                    value = "HEAD"
            )
    )
    private static void renderArmorBefore(GuiGraphicsExtractor graphics, Player player, int yLineBase, int numHealthRows, int healthRowHeight, int xLeft, CallbackInfo ci) {
        int i = player.getArmorValue();

        float duration = GUITweenConfig.armorDuration();

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

            float originScale = GUITweenConfig.upArmorScale();
            gUITween$armorScale = gUITween$armorIsUp ? TweenUtil.tween(originScale, 1, progress, GUITweenConfig.upArmorEase()) : 1;

            float shakeStrength = GUITweenConfig.downArmorShakeStrength();
            gUITween$armorDx = !gUITween$armorIsUp ? TweenUtil.shake(0, gUITween$armorChangeTick, duration, shakeStrength) : 0;
            gUITween$armorDy = !gUITween$armorIsUp ? TweenUtil.shake(1, gUITween$armorChangeTick, duration, shakeStrength) : 0;
        }
    }

    @ModifyVariable(
            method = "extractArmor",
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
    private static void gUITween$renderAnimArmor(GuiGraphicsExtractor guiGraphics, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height) {
        Matrix3x2fStack poseStack = guiGraphics.pose();

        boolean needPlayTween = gUITween$inArmorTween;

        // 在渲染之前做自定义处理
        if (needPlayTween) {
            poseStack.pushMatrix();

            if (gUITween$armorIsUp) {
                float centerX = x + 4.5f;
                float centerY = y + 4.5f;

                poseStack.translate(centerX, centerY);
                poseStack.scale(gUITween$armorScale, gUITween$armorScale);
                poseStack.translate(-centerX, -centerY);

            }
            else {
                poseStack.translate(gUITween$armorDx, gUITween$armorDy);
            }
        }

        // 原始调用
        guiGraphics.blitSprite(pipeline, sprite, x, y, width, height);

        if (needPlayTween) {
            poseStack.popMatrix();
        }
    }

    @Redirect(
            method = "extractArmor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
                    ordinal = 0
            )
    )
    private static void redirectBlitFullSprite(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, Identifier location, int x, int y, int width, int height) {
        gUITween$renderAnimArmor(guiGraphics, renderPipeline, location, x, y, width, height);
    }

    @Redirect(
            method = "extractArmor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
                    ordinal = 1
            )
    )
    private static void redirectBlitHalfSprite(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, Identifier location, int x, int y, int width, int height) {
        gUITween$renderAnimArmor(guiGraphics, renderPipeline, location, x, y, width, height);
    }

    @Inject(
            method = "extractArmor",
            at = @At(
                    value = "RETURN"
            )
    )
    private static void renderArmorAfter(GuiGraphicsExtractor graphics, Player player, int yLineBase, int numHealthRows, int healthRowHeight, int xLeft, CallbackInfo ci) {
        if (gUITween$inArmorTween) {
            gUITween$inArmorTween = false;

            gUITween$armorChangeTick += GUITweenUtility.getDeltaTicks();
        }
    }
}
