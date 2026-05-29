package com.remarxk.guitween.mixin.obscuriaapi;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.obscuria.obscureapi.api.BossBarsRenderManager;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(
        value = BossHealthOverlay.class,
        priority = -50
)
public abstract class BossHealthOverlayMixin {
    @Shadow
    @Final
    private static ResourceLocation GUI_BARS_LOCATION;
    @Shadow
    @Final
    Map<UUID, LerpingBossEvent> events;
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(
            method = {"render"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void render(GuiGraphics context, CallbackInfo info) {
        info.cancel();
        if (!this.events.isEmpty()) {
            int width = this.minecraft.getWindow().getGuiScaledWidth();
            int left = width / 2 - 91;
            int top = 12;

            for(LerpingBossEvent bossEvent : this.events.values()) {
                Optional<BossBarsRenderManager.Style> style = BossBarsRenderManager.getStyle(bossEvent.getName());
                if (style.isPresent()) {
                    Component component = bossEvent.getName();
                    if (((BossBarsRenderManager.Style)style.get()).shouldRenderBar()) {
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                        RenderSystem.setShaderTexture(0, GUI_BARS_LOCATION);
                        gUITween$drawBarBefore(context, left, top, bossEvent);
                        this.drawBar(context, left, top, bossEvent);
                        gUITween$drawBarAfter(context, left, top, bossEvent);
                    }

                    ((BossBarsRenderManager.Style)style.get()).getFunction().render(this.minecraft, context, left, top, bossEvent, component);
                    if (((BossBarsRenderManager.Style)style.get()).shouldRenderName()) {
                        int x = width / 2 - this.minecraft.font.width(component) / 2;
                        int y = top - 9;
                        gUITween$drawStringBefore();
                        context.drawString(this.minecraft.font, component, x, y, 16777215);
                        gUITween$drawStringAfter();
                    }

                    top += ((BossBarsRenderManager.Style)style.get()).getIncrement(this.minecraft);
                } else {
                    Window var10001 = this.minecraft.getWindow();
                    Objects.requireNonNull(this.minecraft.font);
                    CustomizeGuiOverlayEvent.BossEventProgress event = ForgeHooksClient.onCustomizeBossEventProgress(context, var10001, bossEvent, left, top, 10 + 9);
                    if (!event.isCanceled()) {
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                        RenderSystem.setShaderTexture(0, GUI_BARS_LOCATION);
                        gUITween$drawBarBefore(context, left, top, bossEvent);
                        this.drawBar(context, left, top, bossEvent);
                        gUITween$drawBarAfter(context, left, top, bossEvent);
                        Component component = bossEvent.getName();
                        int x = width / 2 - this.minecraft.font.width(component) / 2;
                        int y = top - 9;
                        gUITween$drawStringBefore();
                        context.drawString(this.minecraft.font, component, x, y, 16777215);
                        gUITween$drawStringAfter();
                    }

                    top += event.getIncrement();
                }

                if (top >= this.minecraft.getWindow().getGuiScaledHeight() / 3) {
                    break;
                }
            }

            gUITween$renderAfter();
        }

    }

    @Shadow
    protected abstract void drawBar(GuiGraphics context, int left, int top, BossEvent bossEvent);

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

    @Unique
    private boolean gUITween$haveTween;

    @Unique
    private void gUITween$drawBarBefore(GuiGraphics graphics, int x, int y, BossEvent event) {
        gUITween$haveTween = false;
        float dx = 0;
        float dy = 0;
        float scale = 1;

        UUID uuid = event.getId();

        Float addTick = gUITween$addTweenTicks.get(uuid);
        if (addTick != null && addTick < GUITween.CONFIG.getBossShowMaxDuration()) {
            gUITween$haveTween = true;

            float progress = addTick / GUITween.CONFIG.bossShowDuration;
            scale = TweenUtil.tween(0, 1, progress, GUITween.CONFIG.bossShowEase.get());

            float alphaProgress = addTick / GUITween.CONFIG.bossShowFadeDuration;
            gUITween$alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, alphaProgress, GUITween.CONFIG.bossShowFadeEase.get());
        }

        Float removeTick = gUITween$removeTweenTicks.get(uuid);
        if (removeTick != null && removeTick < GUITween.CONFIG.getBossHideMaxDuration()) {
            gUITween$haveTween = true;

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
                    gUITween$haveTween = true;

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

        if (gUITween$haveTween) {
            poseStack.pushPose();

            poseStack.translate(dx, dy, 0);

            float centerX = x + 182f / 2;
            float centerY = y + 5f / 2;

            poseStack.translate(centerX, centerY, 0);
            poseStack.scale(scale, 1, 0);
            poseStack.translate(-centerX, -centerY, 0);
        }
    }

    @Unique
    private void gUITween$drawBarAfter(GuiGraphics graphics, int x, int y, BossEvent event) {
        if (gUITween$haveTween) {
            PoseStack poseStack = graphics.pose();
            poseStack.popPose();
        }
    }

    @Unique
    private void gUITween$drawStringBefore() {
        if (gUITween$alpha != null) {
            GUITweenUtility.pushFontAlpha(gUITween$alpha);
        }
    }

    @Unique
    private void gUITween$drawStringAfter() {
        if (gUITween$alpha != null) {
            GUITweenUtility.popFontAlpha();

            gUITween$alpha = null;
        }
    }

    @Unique
    private void gUITween$renderAfter() {
        for (LerpingBossEvent lerpingbossevent : this.events.values()) {
            UUID uuid = lerpingbossevent.getId();

            Float addTick = gUITween$addTweenTicks.get(uuid);
            if (addTick != null) {
                float nextTick = addTick + GUITweenUtility.getDeltaTicks();
                if (nextTick < GUITween.CONFIG.getBossShowMaxDuration()) {
                    gUITween$addTweenTicks.put(uuid, nextTick);
                }
                else {
                    gUITween$addTweenTicks.remove(uuid);
                }
            }

            Float removeTick = gUITween$removeTweenTicks.get(uuid);
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

            Float shakeTick = gUITween$shakeTweenTicks.get(uuid);
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
            return;
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
