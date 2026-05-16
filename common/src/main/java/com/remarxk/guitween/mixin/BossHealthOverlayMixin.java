package com.remarxk.guitween.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.remarxk.guitween.Constants;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.Ease;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.world.BossEvent;
import org.joml.Matrix3x2fStack;
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
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;extractBar(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IILnet/minecraft/world/BossEvent;)V")
    )
    private void wrapExtractBar(BossHealthOverlay instance, GuiGraphicsExtractor graphics, int x, int y, BossEvent event, Operation<Void> original) {
        boolean haveTween = false;
        float dx = 0;
        float dy = 0;
        float scale = 1;

        UUID uuid = event.getId();

        Float addTick = gUITween$addTweenTicks.get(uuid);
        if (addTick != null && addTick < GUITweenConfig.getBossShowMaxDuration()) {
            haveTween = true;

            float progress = addTick / GUITweenConfig.bossShowDuration();
            scale = TweenUtil.tween(0, 1, progress, GUITweenConfig.bossShowEase());

            float alphaProgress = addTick / GUITweenConfig.bossShowFadeDuration();
            gUITween$alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, alphaProgress, GUITweenConfig.bossShowFadeEase());
        }

        Float removeTick = gUITween$removeTweenTicks.get(uuid);
        if (removeTick != null && removeTick < GUITweenConfig.getBossHideMaxDuration()) {
            haveTween = true;

            float progress = removeTick / GUITweenConfig.bossHideDuration();
            scale = TweenUtil.tween(1, 0, progress, GUITweenConfig.bossHideEase());

            float alphaProgress = removeTick / GUITweenConfig.bossHideFadeDuration();
            gUITween$alpha = TweenUtil.tween(1, GUITweenUtility.fFontMinAlpha, alphaProgress, GUITweenConfig.bossHideFadeEase());
        }

        Float lastHp = gUITween$lastHps.get(uuid);
        Float shakeTick = gUITween$shakeTweenTicks.get(uuid);
        if (GUITweenConfig.isEnableBossHurt()) {
            if (lastHp != null) {
                if (lastHp > event.getProgress()) {
                    shakeTick = 0f;
                    gUITween$shakeTweenTicks.put(uuid, 0f);
                }

                if (shakeTick != null && shakeTick < GUITweenConfig.bossHurtDuration()) {
                    haveTween = true;

                    float shakeMul = Math.max((event.getProgress() - lastHp) / 0.003f, 1);
                    float duration = GUITweenConfig.bossHurtDuration();
                    float strength = GUITweenConfig.bossHurtShakeStrength();
                    dx = TweenUtil.shake(x, shakeTick, duration, strength * shakeMul);
                    dy = TweenUtil.shake(y, shakeTick, duration, strength * shakeMul);
                }
            }

            gUITween$lastHps.put(uuid, event.getProgress());
        }

        Matrix3x2fStack poseStack = graphics.pose();

        if (haveTween) {
            poseStack.pushMatrix();

            poseStack.translate(dx, dy);

            float centerX = x + 182f / 2;
            float centerY = y + 5f / 2;

            poseStack.translate(centerX, centerY);
            poseStack.scale(scale, 1);
            poseStack.translate(-centerX, -centerY);
        }

        original.call(instance, graphics, x, y, event);

        if (haveTween) {
            poseStack.popMatrix();
        }
    }

    @WrapOperation(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"
            )
    )
    private void wrapText(GuiGraphicsExtractor instance, Font font, Component str, int x, int y, int color, Operation<Void> original) {
        if (gUITween$alpha != null) {
            GUITweenUtility.pushFontAlpha(gUITween$alpha);
        }

        original.call(instance, font, str, x, y, color);

        if (gUITween$alpha != null) {
            GUITweenUtility.popFontAlpha();

            gUITween$alpha = null;
        }
    }

    @Inject(
            method = "extractRenderState",
            at = @At(
                    value = "TAIL"
            )
    )
    private void extractRenderStateAfter(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        for (LerpingBossEvent lerpingbossevent : this.events.values()) {
            UUID uuid = lerpingbossevent.getId();

            Float addTick = gUITween$addTweenTicks.get(uuid);
            if (addTick != null) {
                float nextTick = addTick + GUITweenUtility.getDeltaTicks();
                if (nextTick < GUITweenConfig.getBossShowMaxDuration()) {
                    gUITween$addTweenTicks.put(uuid, nextTick);
                }
                else {
                    gUITween$addTweenTicks.remove(uuid);
                }
            }

            Float removeTick = gUITween$removeTweenTicks.get(uuid);
            if (removeTick != null) {
                float nextTick = removeTick + GUITweenUtility.getDeltaTicks();
                if (nextTick < GUITweenConfig.getBossHideMaxDuration()) {
                    gUITween$removeTweenTicks.put(uuid, nextTick);
                }
                else {
                    gUITween$removeTweenTicks.remove(uuid);

                    gUITween$removeQueue.add(uuid);
                }
            }

            Float shakeTick = gUITween$shakeTweenTicks.get(uuid);
            if (shakeTick != null) {
                float nextTick = shakeTick + GUITweenUtility.getDeltaTicks();
                if (nextTick < GUITweenConfig.bossHurtDuration()) {
                    gUITween$shakeTweenTicks.put(uuid, nextTick);
                }
                else {
                    gUITween$shakeTweenTicks.remove(uuid);
                }
            }
        }

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
        if (!GUITweenConfig.enable()) {
            original.call(instance, handler);
            return;
        }

        instance.dispatch(new ClientboundBossEventPacket.Handler() {
            public void add(UUID id, Component name, float progress, BossEvent.BossBarColor color, BossEvent.BossBarOverlay overlay, boolean darkenScreen, boolean playMusic, boolean createWorldFog) {
                handler.add(id, name, progress, color, overlay, darkenScreen, playMusic, createWorldFog);

                if (GUITweenConfig.isEnableBossShow()) {
                    gUITween$addTweenTicks.put(id, 0f);
                }

                gUITween$removeTweenTicks.remove(id);
            }

            public void remove(UUID id) {
                if (GUITweenConfig.isEnableBossHide()) {
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
