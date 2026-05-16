package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.anim.AttackTween;
import com.remarxk.guitween.client.anim.Tween;
import com.remarxk.guitween.client.anim.TweenPool;
import com.remarxk.guitween.client.anim.UseTween;
import com.remarxk.guitween.client.eventListener.HotbarChangeListener;
import com.remarxk.guitween.client.util.Axis;
import com.remarxk.guitween.client.util.TweenUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(InGameHud.class)
public class GuiMixin {
    @Shadow
    @Final
    private MinecraftClient client;
    @Unique
    private int gUITween$lastLevel = -1;

    @Unique
    private boolean gUITween$inLevelTextTween;

    @Unique
    private float gUITween$levelTextTick;

    @Inject(method = "renderHotbarItem", at = @At("HEAD"))
    public void renderSlotBefore(DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci){
        boolean hasTween = false;

        float scale = 1;

        int slot = seed - 1;

        if (GUITweenClient.CONFIG.isEnableHoldItem()) {
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

            MatrixStack poseStack = context.getMatrices();
            poseStack.push();
            poseStack.translate(centerX, centerY, 0);
            poseStack.scale(scale, scale, 1.0F);
            poseStack.translate(-centerX, -centerY, 0);
        }
    }

    @Unique
    private boolean gUITween$inItemTween;

    @Inject(
            method = "renderHotbarItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;III)V"
            )
    )
    public void renderSlotItemBefore(DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
        float angle = 0;
        float scale = 1;

        int slot = seed - 1;

        if (GUITweenClient.CONFIG.isEnableAttack()) {
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

        if (GUITweenClient.CONFIG.isEnableUse()) {
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

            MatrixStack poseStack = context.getMatrices();
            poseStack.push();
            poseStack.translate(centerX, centerY, 0);
            if (scale != 1) {
                poseStack.scale(scale, scale, 1);
            }
            if (angle != 0) {
                poseStack.multiply(Axis.ZP.rotationDegrees(angle));
            }
            poseStack.translate(-centerX, -centerY, 0);
        }
    }

    @Inject(
            method = "renderHotbarItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;III)V",
                    shift = At.Shift.AFTER
            )
    )
    public void renderSlotItemAfter(DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
        if (gUITween$inItemTween) {
            gUITween$inItemTween = false;
            context.getMatrices().pop();
        }
    }

    @Inject(method = "renderHotbarItem", at = @At("RETURN"))
    public void renderSlotAfter(DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci){
        boolean hasTween = false;

        if (GUITweenClient.CONFIG.isEnableHoldItem()) {
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
                            tween.ease = GUITweenClient.CONFIG.holdZoomOutEase.get();
                            tween.tick = 0;
                            tween.totalTick = GUITweenClient.CONFIG.holdZoomOutDuration;
                            tween.startValue = tween.stopValue;
                            tween.stopValue = 1;
                            tween.rewind = false;
                        }
                    }
                }
            }
        }

        if (hasTween) {
            MatrixStack poseStack = context.getMatrices();
            poseStack.pop();
        }
    }

    @Unique
    private boolean gUITween$inSelectedItemNameTween;

    @Inject(
            method = "renderHeldItemTooltip",
            at = @At(
                    value = "HEAD"
            )
    )
    public void renderSelectedItemNameBefore(DrawContext context, CallbackInfo ci) {
        if (!GUITweenClient.CONFIG.isEnableSelectedItemName())
            return;

        // 如果动画结束，直接正常绘制
        if (HotbarChangeListener.animTick > GUITweenClient.CONFIG.getSelectedItemNameDuration()) {
            return;
        }

        gUITween$inSelectedItemNameTween = true;

        float progress = HotbarChangeListener.animTick / GUITweenClient.CONFIG.selectedItemNameMoveDuration;
        float dy = TweenUtil.tween(GUITweenClient.CONFIG.selectedItemNameMoveY, 0, progress, GUITweenClient.CONFIG.selectedItemNameMoveEase.get());

        MatrixStack poseStack = context.getMatrices();
        poseStack.push();

        poseStack.translate(0, dy, 0);
    }

    @ModifyArg(
            method = "renderHeldItemTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/ColorHelper$Argb;withAlpha(II)I"
            ),
            index = 0
    )
    private int modifySelectedItemNameAlpha(int alpha) {
        if (gUITween$inSelectedItemNameTween) {
            float progress = HotbarChangeListener.animTick / GUITweenClient.CONFIG.selectedItemNameAlphaDuration;
            alpha = (int) TweenUtil.tween(GUITweenUtility.iFontMinAlpha, alpha, progress, GUITweenClient.CONFIG.selectedItemNameAlphaEase.get());
        }

        return alpha;
    }

    @Inject(
            method = "renderHeldItemTooltip",
            at = @At(
                    value = "RETURN"
            )
    )
    public void renderSelectedItemNameAfter(DrawContext context, CallbackInfo ci) {
        if (!gUITween$inSelectedItemNameTween) {
            return;
        }

        gUITween$inSelectedItemNameTween = false;

        // 推进动画时间
        HotbarChangeListener.animTick += GUITweenUtility.getDeltaTicks();

        MatrixStack poseStack = context.getMatrices();
        poseStack.pop();
    }

    @Unique
    private boolean gUITween$inLackTween;

    @Inject(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V",
                    ordinal = 1
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void renderItemHotbarSelectBefore(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MatrixStack poseStack = context.getMatrices();

        if (GUITweenClient.CONFIG.isEnableLack()) {
            if (HotbarChangeListener.lackTick < GUITweenClient.CONFIG.lackDuration) {
                gUITween$inLackTween = true;

                float duration = GUITweenClient.CONFIG.lackDuration;
                float strength = GUITweenClient.CONFIG.lackShakeStrength;

                float dx = TweenUtil.shake(0, HotbarChangeListener.lackTick, duration, strength);
                float dy = TweenUtil.shake(1, HotbarChangeListener.lackTick, duration, strength);

                poseStack.push();
                poseStack.translate(dx, dy, 0);

                HotbarChangeListener.lackTick += GUITweenUtility.getDeltaTicks();
            }
        }
    }

    @Unique
    private float gUITween$curSelectPos = -1000;

    @Redirect(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V",
                    ordinal = 1
            )
    )
    public void renderHotbarSelect(DrawContext instance, Identifier sprite, int pX, int pY, int pUWidth, int pVHeight) {
        if (!GUITweenClient.CONFIG.isEnableSelectMove()) {
            instance.drawGuiTexture(sprite, pX, pY, pUWidth, pVHeight);
            return;
        }

        ClientPlayerEntity player = client.player;
        if (player == null) {
            instance.drawGuiTexture(sprite, pX, pY, pUWidth, pVHeight);
            return;
        }

        if (Math.abs(gUITween$curSelectPos - pX) < 1) {
            gUITween$curSelectPos = pX;
            HotbarChangeListener.scrollSelected = -1;
            HotbarChangeListener.scrollDir = 0;
            instance.drawGuiTexture(sprite, pX, pY, pUWidth, pVHeight);
            return;
        }

        // 重置滚动状态
        if (HotbarChangeListener.scrollSelected >= 0 && HotbarChangeListener.scrollSelected != player.getInventory().selectedSlot) {
            HotbarChangeListener.scrollSelected = -1;
            HotbarChangeListener.scrollDir = 0;
        }

        int left = instance.getScaledWindowWidth() / 2 - 91 - 1;
        int width = 9 * 20;
        int right = left + width + (pUWidth - 20) / 2;

        if (gUITween$curSelectPos == -1000) {
            gUITween$curSelectPos = pX;
        }

        float target = pX;

        float delta = GUITweenUtility.getDeltaTicks(); // 每帧经过的时间，单位秒
        float speed = GUITweenClient.CONFIG.selectMoveSpeed; // 过渡速度，每秒接近目标的比例

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
            instance.enableScissor(left + (pUWidth - 20) / 2, pY, right, pY + pVHeight);
            instance.drawGuiTexture(sprite, (int) mirrorX, pY, pUWidth, pVHeight);
        }

        instance.drawGuiTexture(sprite, (int) gUITween$curSelectPos, pY, pUWidth, pVHeight);

        if (needScissor) {
            instance.disableScissor();
        }
    }

    @Inject(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    public void renderItemHotbarSelectAfter(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MatrixStack poseStack = context.getMatrices();

        if (gUITween$inLackTween) {
            gUITween$inLackTween = false;
            poseStack.pop();
        }
    }

    @Inject(
            method = "renderExperienceLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I",
                    ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void renderExperienceLevelBefore(DrawContext guiGraphics, RenderTickCounter deltaTracker, CallbackInfo ci, int i, String s, int j, int k) {
        if (!GUITweenClient.CONFIG.isEnableExp())
            return;

        if (gUITween$lastLevel == -1) {
            gUITween$lastLevel = i;
            gUITween$levelTextTick = GUITweenClient.CONFIG.expDuration;
            return;
        }

        if (gUITween$lastLevel != i) {
            gUITween$lastLevel = i;
            gUITween$levelTextTick = 0;
        }

        if (gUITween$levelTextTick >= GUITweenClient.CONFIG.expDuration) {
            return;
        }

        gUITween$inLevelTextTween = true;

        InGameHud gui = (InGameHud) ((Object) this);

        MatrixStack poseStack = guiGraphics.getMatrices();
        poseStack.push();

        // 缩放中心为文本中心
        float cx = j + gui.getTextRenderer().getWidth(s) / 2f;
        float cy = k + gui.getTextRenderer().fontHeight / 2f;

        float progress = gUITween$levelTextTick / GUITweenClient.CONFIG.expDuration;
        float scale = TweenUtil.tween(GUITweenClient.CONFIG.expScale, 1, progress, GUITweenClient.CONFIG.expEase.get());

        poseStack.translate(cx, cy, 0);
        poseStack.scale(scale, scale, 1);
        poseStack.translate(-cx, -cy, 0);

        gUITween$levelTextTick += GUITweenUtility.getDeltaTicks();
    }

    @Inject(
            method = "renderExperienceLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I",
                    ordinal = 4,
                    shift = At.Shift.AFTER
            )
    )
    public void renderExperienceLevelAfter(DrawContext guiGraphics, RenderTickCounter deltaTracker, CallbackInfo ci) {
        if (!gUITween$inLevelTextTween)
            return;

        gUITween$inLevelTextTween = false;
        guiGraphics.getMatrices().pop();
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
    private static void renderArmorBefore(DrawContext context, PlayerEntity player, int i, int j, int k, int x, CallbackInfo ci) {
        int armor = player.getArmor();

        float duration = GUITweenClient.CONFIG.armorDuration;

        if (GUITweenClient.CONFIG.isEnableArmor()) {
            if (armor != gUITween$curArmorValue) {
                if (gUITween$curArmorValue != -1) {
                    gUITween$lastArmorValue = gUITween$curArmorValue;
                    gUITween$armorChangeTick = 0;
                }
                else {
                    gUITween$lastArmorValue = armor;
                    gUITween$armorChangeTick = duration;
                }

                gUITween$curArmorValue = armor;
            }
        }

        float progress = gUITween$armorChangeTick / duration;
        if (GUITweenClient.CONFIG.isEnableArmor() && progress < 1) {
            gUITween$inArmorTween = true;

            gUITween$armorIsUp = gUITween$curArmorValue > gUITween$lastArmorValue;

            float originScale = GUITweenClient.CONFIG.upArmorScale;
            gUITween$armorScale = gUITween$armorIsUp ? TweenUtil.tween(originScale, 1, progress, GUITweenClient.CONFIG.upArmorEase.get()) : 1;

            float shakeStrength = GUITweenClient.CONFIG.downArmorShakeStrength;
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
    private static void gUITween$renderAnimArmor(DrawContext guiGraphics, Identifier sprite, int x, int y, int width, int height) {
        MatrixStack poseStack = guiGraphics.getMatrices();

        boolean needPlayTween = gUITween$inArmorTween;

        // 在渲染之前做自定义处理
        if (needPlayTween) {
            poseStack.push();

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
        guiGraphics.drawGuiTexture(sprite, x, y, width, height);

        if (needPlayTween) {
            poseStack.pop();
        }
    }

    @Redirect(
            method = "renderArmor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V",
                    ordinal = 0
            )
    )
    private static void redirectBlitFullSprite(DrawContext guiGraphics, Identifier sprite, int x, int y, int width, int height) {
        gUITween$renderAnimArmor(guiGraphics, sprite, x, y, width, height);
    }

    @Redirect(
            method = "renderArmor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V",
                    ordinal = 1
            )
    )
    private static void redirectBlitHalfSprite(DrawContext guiGraphics, Identifier sprite, int x, int y, int width, int height) {
        gUITween$renderAnimArmor(guiGraphics, sprite, x, y, width, height);
    }

    @Inject(
            method = "renderArmor",
            at = @At(
                    value = "TAIL"
            )
    )
    private static void renderArmorAfter(DrawContext context, PlayerEntity player, int i, int j, int k, int x, CallbackInfo ci) {
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
