package com.remarxk.guitween.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin {
    @Unique
    private Float gUITween$alpha;

    @Unique
    private HashMap<UUID, Float> gUITween$lastHps = new HashMap<>();

    @Unique
    private HashMap<UUID, Float> gUITween$addTweenTicks = new HashMap<>();

    @Unique
    private HashMap<UUID, Float> gUITween$removeTweenTicks = new HashMap<>();

    @Unique
    private HashMap<UUID, Float> gUITween$shakeTweenTicks = new HashMap<>();

    @Unique
    private Queue<UUID> gUITween$removeQueue = new ArrayDeque<>();

    @Shadow
    @Final
    private Map<UUID, LerpingBossEvent> events;

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;drawBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/BossEvent;)V")
    )
    private void wrapExtractBar(BossHealthOverlay instance, GuiGraphics graphics, int x, int y, BossEvent event, Operation<Void> original) {
        boolean haveTween = false;
        float dx = 0;
        float dy = 0;
        float scale = 1;

        UUID uuid = event.getId();

        Float addTick = gUITween$addTweenTicks.get(uuid);
        if (addTick != null && addTick < GUITween.CONFIG.getBossShowMaxDuration()) {
            haveTween = true;

            float progress = addTick / GUITween.CONFIG.bossShowDuration;
            scale = TweenUtil.tween(0, 1, progress, GUITween.CONFIG.bossShowEase.get());

            float alphaProgress = addTick / GUITween.CONFIG.bossShowFadeDuration;
            gUITween$alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, alphaProgress, GUITween.CONFIG.bossShowFadeEase.get());
        }

        Float removeTick = gUITween$removeTweenTicks.get(uuid);
        if (removeTick != null && removeTick < GUITween.CONFIG.getBossHideMaxDuration()) {
            haveTween = true;

            float progress = removeTick / GUITween.CONFIG.bossHideDuration;
            scale = TweenUtil.tween(1, 0, progress, GUITween.CONFIG.bossHideEase.get());

            float alphaProgress = removeTick / GUITween.CONFIG.bossHideFadeDuration;
            gUITween$alpha = TweenUtil.tween(1, GUITweenUtility.fFontMinAlpha, alphaProgress, GUITween.CONFIG.bossHideFadeEase.get());
        }

        Float lastHp = gUITween$lastHps.get(uuid);
        Float shakeTick = gUITween$shakeTweenTicks.get(uuid);
        if (GUITween.CONFIG.isEnableBossHurt()) {
            if (lastHp != null) {
                if (lastHp > event.getProgress()) {
                    shakeTick = 0f;
                    gUITween$shakeTweenTicks.put(uuid, 0f);
                }

                if (shakeTick != null && shakeTick < GUITween.CONFIG.bossHurtDuration) {
                    haveTween = true;

                    float shakeMul = Math.max((event.getProgress() - lastHp) / 0.003f, 1);
                    float duration = GUITween.CONFIG.bossHurtDuration;
                    float strength = GUITween.CONFIG.bossHurtShakeStrength;
                    dx = TweenUtil.shake(x, shakeTick, duration, strength * shakeMul);
                    dy = TweenUtil.shake(y, shakeTick, duration, strength * shakeMul);
                }
            }

            gUITween$lastHps.put(uuid, event.getProgress());
        }

        PoseStack poseStack = graphics.pose();

        if (haveTween) {
            poseStack.pushPose();

            poseStack.translate(dx, dy, 0);

            float centerX = x + 182f / 2;
            float centerY = y + 5f / 2;

            poseStack.translate(centerX, centerY, 0);
            poseStack.scale(scale, 1, 0);
            poseStack.translate(-centerX, -centerY, 0);
        }

        original.call(instance, graphics, x, y, event);

        if (haveTween) {
            poseStack.popPose();

            if (addTick != null) {
                float nextTick = addTick + GUITweenUtility.getDeltaTicks();
                if (nextTick < GUITween.CONFIG.getBossShowMaxDuration()) {
                    gUITween$addTweenTicks.put(uuid, nextTick);
                }
                else {
                    gUITween$addTweenTicks.remove(uuid);
                }
            }

            if (removeTick != null) {
                float nextTick = removeTick + GUITweenUtility.getDeltaTicks();
                if (nextTick < GUITween.CONFIG.getBossHideMaxDuration()) {
                    gUITween$removeTweenTicks.put(uuid, nextTick);
                }
                else {
                    gUITween$removeTweenTicks.remove(uuid);

                    gUITween$removeQueue.add(uuid);
                }
            }

            if (shakeTick != null) {
                float nextTick = shakeTick + GUITweenUtility.getDeltaTicks();
                if (nextTick < GUITween.CONFIG.bossHurtDuration) {
                    gUITween$shakeTweenTicks.put(uuid, nextTick);
                }
                else {
                    gUITween$shakeTweenTicks.remove(uuid);
                }
            }
        }
    }

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"
            )
    )
    private int wrapText(GuiGraphics instance, Font font, Component text, int x, int y, int color, Operation<Integer> original) {
        if (gUITween$alpha != null) {
            GUITweenUtility.pushFontAlpha(gUITween$alpha);
        }

        original.call(instance, font, text, x, y, color);

        if (gUITween$alpha != null) {
            GUITweenUtility.popFontAlpha();

            gUITween$alpha = null;
        }
        return x;
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "TAIL"
            )
    )
    private void extractRenderStateAfter(GuiGraphics guiGraphics, CallbackInfo ci) {
        while (!gUITween$removeQueue.isEmpty()) {
            var uuid = gUITween$removeQueue.remove();
            events.remove(uuid);
        }
    }

    @WrapOperation(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/game/ClientboundBossEventPacket;dispatch(Lnet/minecraft/network/protocol/game/ClientboundBossEventPacket$Handler;)V")
    )
    private void redirectUpdate(ClientboundBossEventPacket instance, ClientboundBossEventPacket.Handler handler, Operation<Void> original) {
        if (!GUITween.CONFIG.isEnable()) {
            original.call(instance, handler);
        }

        instance.dispatch(new ClientboundBossEventPacket.Handler() {
            public void add(UUID id, Component name, float progress, BossEvent.BossBarColor color, BossEvent.BossBarOverlay overlay, boolean darkenScreen, boolean playMusic, boolean createWorldFog) {
                handler.add(id, name, progress, color, overlay, darkenScreen, playMusic, createWorldFog);

                if (GUITween.CONFIG.isEnableBossShow()) {
                    gUITween$addTweenTicks.put(id, 0f);
                }

                gUITween$removeTweenTicks.remove(id);
            }

            public void remove(UUID id) {
                if (GUITween.CONFIG.isEnableBossHide()) {
                    gUITween$removeTweenTicks.put(id, 0f);
                }
                else {
                    handler.remove(id);
                }

                gUITween$lastHps.remove(id);
                gUITween$shakeTweenTicks.remove(id);
                gUITween$addTweenTicks.remove(id);
            }

            public void updateProgress(UUID id, float progress) {
                handler.updateProgress(id, progress);
            }

            public void updateName(UUID id, Component name) {
                handler.updateName(id, name);
            }

            public void updateStyle(UUID id, BossEvent.BossBarColor color, BossEvent.BossBarOverlay overlay) {
                handler.updateStyle(id, color, overlay);
            }

            public void updateProperties(UUID id, boolean darkenScreen, boolean playMusic, boolean createWorldFog) {
                handler.updateProperties(id, darkenScreen, playMusic, createWorldFog);
            }
        });
    }
}
