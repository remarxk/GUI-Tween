package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.event.PlayGuiSoundEvent;
import com.remarxk.guitween.eventListener.HotbarChangeListener;
import com.remarxk.guitween.anim.AttackTween;
import com.remarxk.guitween.anim.Tween;
import com.remarxk.guitween.anim.TweenPool;
import com.remarxk.guitween.anim.UseTween;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
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
    protected Minecraft minecraft;

    @Shadow
    protected int screenWidth;

    @Shadow
    protected int screenHeight;

    @Shadow
    protected int displayHealth;

    @Inject(method = "renderSlot", at = @At("HEAD"))
    public void renderSlotBefore(GuiGraphics pGuiGraphics, int pX, int pY, float pPartialTick, Player pPlayer, ItemStack pStack, int pSeed, CallbackInfo ci){
        if (!GUITween.CONFIG.isEnableHoldItem())
            return;

        int slot = pSeed - 1;

        Tween tween = HotbarChangeListener.hotbarAnimStateMap.getOrDefault(slot, null);
        if (tween == null) {
            return;
        }

        if (!GUITween.CONFIG.isEnableSelectMove() || HotbarChangeListener.scrollDir == 0) {
            float centerX = pX + 8;
            float centerY = pY + 8;

            float scale = TweenUtil.tween(tween.startValue, tween.stopValue, tween.tick, tween.totalTick, tween.ease);

            if (!tween.rewind && tween.name.equals("zoomIn") && tween.tick == 0) {
                MinecraftForge.EVENT_BUS.post(new PlayGuiSoundEvent(PlayGuiSoundEvent.SoundType.SELECT_ITEM));
            }

            float deltaTicks = GUITweenUtility.getDeltaTicks();
            if (!tween.rewind) {
                tween.tick += deltaTicks;
                if (tween.tick >= tween.totalTick) {
                    if (tween.stopValue > 1) {
                        tween.name = "zoomOut";
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
    public void renderSlotItemBefore(GuiGraphics pGuiGraphics, int pX, int pY, float pPartialTick, Player pPlayer, ItemStack pStack, int pSeed, CallbackInfo ci) {
        float angle = 0;
        float scale = 1;

        int slot = pSeed - 1;

        if (GUITween.CONFIG.isEnableAttack()) {
            AttackTween attackTween = GUITweenUtility.getAttackTween();

            if (attackTween.isRunning() && !pStack.isEmpty() && slot == attackTween.slot) {
                gUITween$inItemTween = true;
                angle = attackTween.getAngle();
                attackTween.update();
            }
//            else {
//                attackTween.stop();
//            }
        }

        if (GUITween.CONFIG.isEnableUse()) {
            UseTween usingTween = GUITweenUtility.getUsingTween();

            if (usingTween.isRunning() && !pStack.isEmpty() && slot == usingTween.slot) {
                gUITween$inItemTween = true;
                scale = usingTween.getScale();
                usingTween.update();
            }
//                else {
//                    usingTween.stop();
//                }
        }

        if (gUITween$inItemTween) {
            float centerX = pX + 8;
            float centerY = pY + 8;

            PoseStack poseStack = pGuiGraphics.pose();
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
    public void renderSlotItemAfter(GuiGraphics pGuiGraphics, int pX, int pY, float pPartialTick, Player pPlayer, ItemStack pStack, int pSeed, CallbackInfo ci) {
        if (gUITween$inItemTween) {
            gUITween$inItemTween = false;
            pGuiGraphics.pose().popPose();
        }
    }

    @Inject(method = "renderSlot", at = @At("TAIL"))
    public void renderSlotAfter(GuiGraphics pGuiGraphics, int pX, int pY, float pPartialTick, Player pPlayer, ItemStack pStack, int pSeed, CallbackInfo ci){
        if (!GUITween.CONFIG.isEnableHoldItem())
            return;

        int slot = pSeed - 1;
        Tween tween = HotbarChangeListener.hotbarAnimStateMap.getOrDefault(slot, null);
        if (tween == null)
            return;

        if (!GUITween.CONFIG.isEnableSelectMove() || HotbarChangeListener.scrollDir == 0) {
            PoseStack poseStack = pGuiGraphics.pose();
            poseStack.popPose();
        }

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
    private boolean gUITween$inSelectedItemNameTween;

    @Inject(
            method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V",
            at = @At(
                    value = "HEAD"
            ),
            remap = false
    )
    public void renderSelectedItemNameBefore(GuiGraphics guiGraphics, int yShift, CallbackInfo ci) {
        if (!GUITween.CONFIG.isEnableSelectedItemName())
            return;

        // 如果动画结束，直接正常绘制
        if (HotbarChangeListener.animTick > GUITween.CONFIG.getSelectedItemNameDuration()) {
            return;
        }

        gUITween$inSelectedItemNameTween = true;

        float progress = HotbarChangeListener.animTick / GUITween.CONFIG.selectedItemNameMoveDuration;
        float dy = TweenUtil.tween(GUITween.CONFIG.selectedItemNameMoveY, 0, progress, GUITween.CONFIG.selectedItemNameMoveEase.get());

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        poseStack.translate(0, dy, 0);
    }

    @ModifyVariable(
            method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V",
            at = @At("STORE"),
            ordinal = 4,
            remap = false
    )
    private int modifySelectedItemNameAlpha(int alpha) {
        if (gUITween$inSelectedItemNameTween) {
            float progress = HotbarChangeListener.animTick / GUITween.CONFIG.selectedItemNameAlphaDuration;
            alpha = (int) TweenUtil.tween(GUITweenUtility.iFontMinAlpha, 255, progress, GUITween.CONFIG.selectedItemNameAlphaEase.get());
        }

        return alpha;
    }

    @Inject(
            method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V",
            at = @At(
                    value = "TAIL"
            ),
            remap = false
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

    @Unique
    private float gUITween$curSelectPos = -1000;

    @Redirect(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V",
                    ordinal = 1
            )
    )
    public void renderHotbarSelect(GuiGraphics guiGraphics, ResourceLocation pAtlasLocation, int pX, int pY, int pUOffset, int pVOffset, int pUWidth, int pVHeight) {
        if (!GUITween.CONFIG.isEnableSelectMove()) {
            guiGraphics.blit(pAtlasLocation, pX, pY, pUOffset, pVOffset, pUWidth, pVHeight);
            return;
        }

        Player player = minecraft.player;
        if (player == null) {
            guiGraphics.blit(pAtlasLocation, pX, pY, pUOffset, pVOffset, pUWidth, pVHeight);
            return;
        }

        if (Math.abs(gUITween$curSelectPos - pX) < 1) {
            gUITween$curSelectPos = pX;
            HotbarChangeListener.scrollSelected = -1;
            HotbarChangeListener.scrollDir = 0;
            guiGraphics.blit(pAtlasLocation, pX, pY, pUOffset, pVOffset, pUWidth, pVHeight);
            return;
        }

        // 重置滚动状态
        if (HotbarChangeListener.scrollSelected >= 0 && HotbarChangeListener.scrollSelected != player.getInventory().selected) {
            HotbarChangeListener.scrollSelected = -1;
            HotbarChangeListener.scrollDir = 0;
        }

        int left = this.screenWidth / 2 - 91 - 1;
        int width = 9 * 20;
        int right = left + width + (pUWidth - 20) / 2;

        if (gUITween$curSelectPos == -1000) {
            gUITween$curSelectPos = pX;
        }

        float target = pX;

        float delta = GUITweenUtility.getDeltaTicks(); // 每帧经过的时间，单位秒
        float speed = GUITween.CONFIG.selectMoveSpeed; // 过渡速度，每秒接近目标的比例

        // 根据 scrollDir 判断循环距离
        if (HotbarChangeListener.scrollDir != 0) {
            if (HotbarChangeListener.scrollDir < 0 && gUITween$curSelectPos < target) {
                target = left - (right - target) - 0.1f;
            }
            else if (HotbarChangeListener.scrollDir > 0 && gUITween$curSelectPos > target) {
                target = right + (target - left) + 0.1f;
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
            guiGraphics.blit(pAtlasLocation, (int) mirrorX, pY, pUOffset, pVOffset, pUWidth, pVHeight);
        }

        guiGraphics.blit(pAtlasLocation, (int) gUITween$curSelectPos, pY, pUOffset, pVOffset, pUWidth, pVHeight);

        if (needScissor) {
            guiGraphics.disableScissor();
        }
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
