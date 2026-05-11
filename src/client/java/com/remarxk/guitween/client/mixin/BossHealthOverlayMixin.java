package com.remarxk.guitween.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.util.TweenUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(BossBarHud.class)
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
    private Map<UUID, ClientBossBar> bossBars;

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/BossBarHud;renderBossBar(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/entity/boss/BossBar;)V")
    )
    private void wrapExtractBar(BossBarHud instance, DrawContext graphics, int x, int y, BossBar event, Operation<Void> original) {
        boolean haveTween = false;
        float dx = 0;
        float dy = 0;
        float scale = 1;

        UUID uuid = event.getUuid();

        Float addTick = gUITween$addTweenTicks.get(uuid);
        if (addTick != null && addTick < GUITweenClient.CONFIG.getBossShowMaxDuration()) {
            haveTween = true;

            float progress = addTick / GUITweenClient.CONFIG.bossShowDuration;
            scale = TweenUtil.tween(0, 1, progress, GUITweenClient.CONFIG.bossShowEase.get());

            float alphaProgress = addTick / GUITweenClient.CONFIG.bossShowFadeDuration;
            gUITween$alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, alphaProgress, GUITweenClient.CONFIG.bossShowFadeEase.get());
        }

        Float removeTick = gUITween$removeTweenTicks.get(uuid);
        if (removeTick != null && removeTick < GUITweenClient.CONFIG.getBossHideMaxDuration()) {
            haveTween = true;

            float progress = removeTick / GUITweenClient.CONFIG.bossHideDuration;
            scale = TweenUtil.tween(1, 0, progress, GUITweenClient.CONFIG.bossHideEase.get());

            float alphaProgress = removeTick / GUITweenClient.CONFIG.bossHideFadeDuration;
            gUITween$alpha = TweenUtil.tween(1, GUITweenUtility.fFontMinAlpha, alphaProgress, GUITweenClient.CONFIG.bossHideFadeEase.get());
        }

        Float lastHp = gUITween$lastHps.get(uuid);
        Float shakeTick = gUITween$shakeTweenTicks.get(uuid);
        if (GUITweenClient.CONFIG.isEnableBossHurt()) {
            if (lastHp != null) {
                if (lastHp > event.getPercent()) {
                    shakeTick = 0f;
                    gUITween$shakeTweenTicks.put(uuid, 0f);
                }

                if (shakeTick != null && shakeTick < GUITweenClient.CONFIG.bossHurtDuration) {
                    haveTween = true;

                    float shakeMul = Math.max((event.getPercent() - lastHp) / 0.003f, 1);
                    float duration = GUITweenClient.CONFIG.bossHurtDuration;
                    float strength = GUITweenClient.CONFIG.bossHurtShakeStrength;
                    dx = TweenUtil.shake(x, shakeTick, duration, strength * shakeMul);
                    dy = TweenUtil.shake(y, shakeTick, duration, strength * shakeMul);
                }
            }

            gUITween$lastHps.put(uuid, event.getPercent());
        }

        MatrixStack poseStack = graphics.getMatrices();

        if (haveTween) {
            poseStack.push();

            poseStack.translate(dx, dy, 0);

            float centerX = x + 182f / 2;
            float centerY = y + 5f / 2;

            poseStack.translate(centerX, centerY, 0);
            poseStack.scale(scale, 1, 0);
            poseStack.translate(-centerX, -centerY, 0);
        }

        original.call(instance, graphics, x, y, event);

        if (haveTween) {
            poseStack.pop();
        }
    }

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I"
            )
    )
    private int wrapText(DrawContext instance, TextRenderer font, Text text, int x, int y, int color, Operation<Integer> original) {
        if (gUITween$alpha != null) {
            GUITweenUtility.pushFontAlpha(gUITween$alpha);
        }

        int value = original.call(instance, font, text, x, y, color);

        if (gUITween$alpha != null) {
            GUITweenUtility.popFontAlpha();

            gUITween$alpha = null;
        }
        return value;
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "TAIL"
            )
    )
    private void extractRenderStateAfter(DrawContext guiGraphics, CallbackInfo ci) {
        for(ClientBossBar ClientBossBar : this.bossBars.values()) {
            UUID uuid = ClientBossBar.getUuid();

            Float addTick = gUITween$addTweenTicks.get(uuid);
            if (addTick != null) {
                float nextTick = addTick + GUITweenUtility.getDeltaTicks();
                if (nextTick < GUITweenClient.CONFIG.getBossShowMaxDuration()) {
                    gUITween$addTweenTicks.put(uuid, nextTick);
                }
                else {
                    gUITween$addTweenTicks.remove(uuid);
                }
            }

            Float removeTick = gUITween$removeTweenTicks.get(uuid);
            if (removeTick != null) {
                float nextTick = removeTick + GUITweenUtility.getDeltaTicks();
                if (nextTick < GUITweenClient.CONFIG.getBossHideMaxDuration()) {
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
                if (nextTick < GUITweenClient.CONFIG.bossHurtDuration) {
                    gUITween$shakeTweenTicks.put(uuid, nextTick);
                }
                else {
                    gUITween$shakeTweenTicks.remove(uuid);
                }
            }
        }

        while (!gUITween$removeQueue.isEmpty()) {
            var uuid = gUITween$removeQueue.remove();
            bossBars.remove(uuid);
        }
    }

    @WrapOperation(
            method = "handlePacket",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/packet/s2c/play/BossBarS2CPacket;accept(Lnet/minecraft/network/packet/s2c/play/BossBarS2CPacket$Consumer;)V")
    )
    private void redirectUpdate(BossBarS2CPacket instance, BossBarS2CPacket.Consumer handler, Operation<Void> original) {
        if (!GUITweenClient.CONFIG.isEnable()) {
            original.call(instance, handler);
            return;
        }

        instance.accept(new BossBarS2CPacket.Consumer() {
            public void add(UUID id, Text name, float progress, BossBar.Color color, BossBar.Style overlay, boolean darkenScreen, boolean playMusic, boolean createWorldFog) {
                handler.add(id, name, progress, color, overlay, darkenScreen, playMusic, createWorldFog);

                if (GUITweenClient.CONFIG.isEnableBossShow()) {
                    gUITween$addTweenTicks.put(id, 0f);
                }

                gUITween$removeTweenTicks.remove(id);
            }

            public void remove(UUID id) {
                if (GUITweenClient.CONFIG.isEnableBossHide()) {
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

            public void updateName(UUID id, Text name) {
                handler.updateName(id, name);
            }

            public void updateStyle(UUID id, BossBar.Color color, BossBar.Style overlay) {
                handler.updateStyle(id, color, overlay);
            }

            public void updateProperties(UUID id, boolean darkenScreen, boolean playMusic, boolean createWorldFog) {
                handler.updateProperties(id, darkenScreen, playMusic, createWorldFog);
            }
        });
    }
}
