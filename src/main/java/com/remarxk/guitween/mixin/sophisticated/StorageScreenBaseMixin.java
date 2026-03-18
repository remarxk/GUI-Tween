package com.remarxk.guitween.mixin.sophisticated;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.anim.DragTween;
import com.remarxk.guitween.anim.Tween;
import com.remarxk.guitween.anim.TweenPool;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.util.Ease;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.InventoryScrollPanel;
import net.p3pp3rf1y.sophisticatedcore.common.gui.StorageContainerMenuBase;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.HashMap;

@Mixin(value = StorageScreenBase.class)
public abstract class StorageScreenBaseMixin<S extends StorageContainerMenuBase<?>> extends AbstractContainerScreen<S> implements InventoryScrollPanel.IInventoryScreen {
    public StorageScreenBaseMixin(S menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

//    @Redirect(
//            method = "render",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/p3pp3rf1y/sophisticatedcore/client/gui/StorageScreenBase;renderBackground(Lnet/minecraft/client/gui/GuiGraphics;IIF)V")
//    )
//    public void onlyRenderBackground(StorageScreenBase instance, GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
//        this.renderTransparentBackground(guiGraphics);
//    }

    @Inject(
            method = "renderBackground",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/p3pp3rf1y/sophisticatedcore/client/gui/StorageScreenBase;renderTransparentBackground(Lnet/minecraft/client/gui/GuiGraphics;)V",
                    shift = At.Shift.AFTER
            )
    )
    public void renderBefore(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!(this instanceof AbstractContainerScreenMixinAccess access))
            return;

        GUITweenUtility.setOpenScreen(access.getGUITween$screenName(), access.getGUITween$openTick());

        if (!GUITweenConfig.isEnableWindow())
            return;

        if (access.getGUITween$isDisableScreenTween())
            return;

        float moveProgress = access.getGUITween$openTick() / GUITweenConfig.window.moveDuration.get().floatValue();
        float gradientProgress = access.getGUITween$openTick() / GUITweenConfig.window.gradientDuration.get().floatValue();

        if (moveProgress >= 1 && gradientProgress >= 1)
            return;

        access.setGUITween$inTween(true);

        float dx = TweenUtil.tween(GUITweenConfig.window.moveX.get().floatValue(), 0, moveProgress, GUITweenConfig.window.moveEase.get());
        float dy = TweenUtil.tween(GUITweenConfig.window.moveY.get().floatValue(), 0, moveProgress, GUITweenConfig.window.moveEase.get());

        Matrix3x2fStack poseStack = guiGraphics.pose();

        // 动画变换
        poseStack.pushMatrix();
        poseStack.translate(dx, dy);  // 上移

        float alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, gradientProgress, GUITweenConfig.window.gradientEase.get());
        GUITweenUtility.pushAlpha(alpha);
    }

//    @Inject(
//            method = "render",
//            at = @At(
//                    value = "TAIL"
//            )
//    )
//    public void renderAfter(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
//        if (!(this instanceof AbstractContainerScreenMixinAccess access))
//            return;
//
//        access.setGUITween$openTick(access.getGUITween$openTick() + GUITweenUtility.getDeltaTicks());
//
//        if (!access.getGUITween$inTween())
//            return;
//
//        GUITweenUtility.popAlpha();
//
//        PoseStack poseStack = guiGraphics.pose();
//        poseStack.popPose();
//
//        access.setGUITween$inTween(false);
//    }

    @Nullable
    @Shadow
    public Slot getHoveredSlot(double mouseX, double mouseY) { return null; }

    @Inject(
            method = "renderSuper",
            at = @At(
                    value = "HEAD"
            )
    )
    private void findHoverSlot(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo ci) {
        if (!GUITweenConfig.isEnable())
            return;

        if (!(this instanceof AbstractContainerScreenMixinAccess access))
            return;

        Slot hoveredSlot = getHoveredSlot(pMouseX, pMouseY);

        Slot gUITween$lastHoverSlot = access.getGUITween$lastHoverSlot();
        HashMap<Slot, Tween> gUITween$hoverSlotMap = access.getGUITween$hoverSlotMap();

        if (hoveredSlot != gUITween$lastHoverSlot) {

            if (gUITween$lastHoverSlot != null) {
                Tween tween = gUITween$hoverSlotMap.getOrDefault(gUITween$lastHoverSlot, null);
                if (tween != null) {
                    tween.rewind = true;
                }
            }

            if (gUITween$lastHoverSlot == null || !gUITween$lastHoverSlot.hasItem()) {
                if (GUITweenConfig.isEnableTooltip())
                    access.setGUITween$tooltipShowTick(0);
            }

            access.setGUITween$lastHoverSlot(hoveredSlot);

            if (hoveredSlot != null) {
                Tween tween = gUITween$hoverSlotMap.getOrDefault(hoveredSlot, null);
                if (tween == null) {
                    tween = TweenPool.getTween();
                    tween.tick = 0;
                    tween.totalTick = GUITweenConfig.windowItem.hoverDuration.get().floatValue();
                    tween.ease = GUITweenConfig.windowItem.hoverEase.get();
                    tween.startValue = 1;
                    tween.stopValue = GUITweenConfig.windowItem.hoverScale.get().floatValue();
                    tween.rewind = false;
                    gUITween$hoverSlotMap.put(hoveredSlot, tween);
                }
                else {
                    tween.rewind = false;
                }
            }
        }

        if (GUITweenConfig.isEnableSameItem()) {
            ItemStack screenDraggingItem = access.gUITween$getDraggingItem();

            ItemStack draggingItem = screenDraggingItem.isEmpty() ? this.menu.getCarried() : screenDraggingItem;
            if (!ItemStack.isSameItemSameComponents(draggingItem, access.getGUITween$lastDraggingItem())) {
                access.setGUITween$lastDraggingItem(draggingItem);
                access.setGUITween$sameItemTick(0);
            }
            else if (!access.getGUITween$lastDraggingItem().isEmpty()) {
                access.setGUITween$sameItemTick((access.getGUITween$sameItemTick() + GUITweenUtility.getDeltaTicks()) % GUITweenConfig.getSameItemTotalDuration());;
            }
        }
    }

//    @Redirect(
//            method = "renderSuper",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/p3pp3rf1y/sophisticatedcore/client/gui/StorageScreenBase;renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;IIII)V"
//            )
//    )
//    private void disableRenderSlotHighlight(GuiGraphics guiGraphics, int pX, int pY, int pBlitOffset, int color) {
//        if (!GUITweenConfig.isEnableHoverItem()) {
//            AbstractContainerScreen.renderSlotHighlight(guiGraphics, pX, pY, pBlitOffset, color);
//        }
//    }

    @Inject(
            method = "renderSlot",
            at = @At(
                    value = "HEAD"
            )
    )
    private void renderSlotBefore(GuiGraphics pGuiGraphics, Slot pSlot, int mouseX, int mouseY, CallbackInfo ci) {
        if (!(this instanceof AbstractContainerScreenMixinAccess access))
            return;

        boolean haveTween = false;
        float scale = 1;
        //        float angle = 0;
        float dx = 0;
        float dy = 0;

        Matrix3x2fStack poseStack = pGuiGraphics.pose();
        float itemSize = 16f; // 物品渲染尺寸（固定16x16）
        float centerX = pSlot.x + itemSize / 2; // 物品中心X
        float centerY = pSlot.y + itemSize / 2; // 物品中心Y

        boolean isEmpty = !pSlot.hasItem();

        Slot gUITween$lastHoverSlot = access.getGUITween$lastHoverSlot();
        HashMap<Slot, Tween> gUITween$hoverSlotMap = access.getGUITween$hoverSlotMap();

        if (GUITweenConfig.isEnableHoverItem()) {
            boolean isHoverSlot = gUITween$lastHoverSlot == pSlot;

            if (isHoverSlot) {
                access.gUITween$renderSlotHighlightBack(pGuiGraphics, pSlot.x - 4, pSlot.y - 4);
            }

            Tween tween = gUITween$hoverSlotMap.getOrDefault(pSlot, null);
            if (tween != null) {
                if (isEmpty) {
                    TweenPool.releaseTween(tween);
                    gUITween$hoverSlotMap.remove(pSlot);
                }
                else {
                    haveTween = true;

                    scale = TweenUtil.tween(tween.startValue, tween.stopValue, tween.tick, tween.totalTick, tween.ease);   // 放大比例

                    int sign = tween.rewind ? -1 : 1;

                    tween.tick += sign * GUITweenUtility.getDeltaTicks();
                    tween.tick = Mth.clamp(tween.tick, 0, tween.totalTick);

                    if (tween.rewind && tween.tick <= 0) {
                        TweenPool.releaseTween(tween);
                        gUITween$hoverSlotMap.remove(pSlot);
                    }
                }
            }
        }

        HashMap<Integer, Tuple<Integer, Integer>> gUITween$quickTweenSlots = access.getGUITween$quickTweenSlots();
        HashMap<Integer, Float> gUITween$quickTicks = access.getGUITween$quickTicks();

        Tuple<Integer, Integer> tuple = gUITween$quickTweenSlots.get(pSlot.index);
        if (tuple != null && menu.getSlot(pSlot.index) == pSlot) {
            if (tuple.getA().equals(tuple.getB())) {
                float quickTick = gUITween$quickTicks.getOrDefault(pSlot.index, 0f);

                float progress = quickTick / 4f;

                if (progress < 1) {
                    float clickScale = GUITweenConfig.windowItem.clickItemScale.get().floatValue();

                    scale = TweenUtil.tween(clickScale, 1f, progress, Ease.IN_OUT_SINE);

                    haveTween = true;

                    gUITween$quickTicks.put(pSlot.index, quickTick + GUITweenUtility.getDeltaTicks());
                }
                else {
                    gUITween$quickTweenSlots.remove(pSlot.index);
                    gUITween$quickTicks.remove(pSlot.index);
                }
            }
            else {
                tuple.setA(tuple.getB());
            }
        }
        
        if (!access.getGUITween$lastDraggingItem().isEmpty() && ItemStack.isSameItemSameComponents(access.getGUITween$lastDraggingItem(), pSlot.getItem())) {
            float delay = GUITweenConfig.windowItem.sameItemDelay.get().floatValue();
            float duration = GUITweenConfig.windowItem.sameItemShakeDuration.get().floatValue();

            if (access.getGUITween$sameItemTick() > delay && access.getGUITween$sameItemTick() < GUITweenConfig.windowItem.sameItemDelay.get() + duration) {
                haveTween = true;

                float strength = GUITweenConfig.windowItem.sameItemShakeStrength.get().floatValue();
                float frequency = GUITweenConfig.windowItem.sameItemShakeFrequency.get().floatValue();

                dx = TweenUtil.shake(0, access.getGUITween$sameItemTick() - delay, duration, strength, frequency, TweenUtil.DEFAULT_SEED + pSlot.index * 100L);
                dy = TweenUtil.shake(1, access.getGUITween$sameItemTick() - delay, duration, strength, frequency, TweenUtil.DEFAULT_SEED + pSlot.index * 100L);
//                angle = (TweenUtil.punch(0.15f, 2, access.getGUITween$sameItemTick() / 8) - 1) * 100;
            }
        }

        if (haveTween) {
            access.setGUITween$inSlotTween(true);

            poseStack.pushMatrix();

            // 矩阵操作：平移到中心 → 缩放 → 平移回原位置
            poseStack.translate(centerX, centerY);
            poseStack.scale(scale, scale); // Z轴缩放不影响2D渲染，设为1
            //            poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
            poseStack.translate(-centerX, -centerY);

            poseStack.translate(dx, dy);
        }
    }

    @Inject(
            method = "renderSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/p3pp3rf1y/sophisticatedcore/common/gui/StorageContainerMenuBase;getQuickCraftPlaceCount(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/item/ItemStack;)I"
            )
    )
    public void renderQuickItem(GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        if (!(this instanceof AbstractContainerScreenMixinAccess access))
            return;

        if (!GUITweenConfig.isEnableQuickCraft())
            return;

        access.setGUITween$isRenderQuick(true);

        var gUITween$quickTweenSlots = access.getGUITween$quickTweenSlots();
        Tuple<Integer, Integer> tuple = gUITween$quickTweenSlots.get(slot.index);
        if (tuple == null) {
            gUITween$quickTweenSlots.put(slot.index, new Tuple<>(-1, 0));
        }
        else {
            tuple.setB(tuple.getB() + 1);
        }
    }

    @Inject(
            method = "renderStack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V",
                    shift = At.Shift.AFTER
            )
    )
    private void renderQuickItem(GuiGraphics guiGraphics, int x, int y, ItemStack itemstack, boolean flag, String stackCountText, CallbackInfo ci) {
        if (!(this instanceof AbstractContainerScreenMixinAccess access))
            return;

        if (!GUITweenConfig.isEnableQuickCraft())
            return;

        if (access.getGUITween$isRenderQuick()) {
            Matrix3x2fStack poseStack = guiGraphics.pose();

            float centerX = x + 8;
            float centerY = y + 8;
            float scale = GUITweenConfig.windowItem.clickItemScale.get().floatValue();

            poseStack.pushMatrix();

            poseStack.translate(centerX, centerY);
            poseStack.scale(scale, scale);
            poseStack.translate(-centerX, -centerY);
        }
    }

    @Inject(
            method = "renderSlot",
            at = @At(
                    value = "TAIL"
            )
    )
    private void renderSlotAfter(GuiGraphics pGuiGraphics, Slot pSlot, int mouseX, int mouseY, CallbackInfo ci) {
        if (!(this instanceof AbstractContainerScreenMixinAccess access))
            return;

        if (access.getGUITween$isRenderQuick()) {
            access.setGUITween$isRenderQuick(false);
            pGuiGraphics.pose().popMatrix();
        }

        if (access.getGUITween$inSlotTween()) {
            access.setGUITween$inSlotTween(false);
            pGuiGraphics.pose().popMatrix();
        }

        if (GUITweenConfig.enableDebugWindow.get()) {
            Font font = Minecraft.getInstance().font;

            // 获取格子左上角坐标
            int x = pSlot.x; // Slot 的 x 坐标
            int y = pSlot.y; // Slot 的 y 坐标

            String text = String.valueOf(pSlot.index);
            int color = 0xFF0000; // 白色文字
            boolean shadow = true; // 阴影，让文字在物品上更清晰

            Matrix3x2fStack poseStack = pGuiGraphics.pose();
            poseStack.pushMatrix();
            poseStack.translate(0, 0);
            pGuiGraphics.drawString(font, text, x + 1, y + 1, color, shadow);
            poseStack.popMatrix();
        }
    }

    @Inject(
            method = "superMouseClicked",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/p3pp3rf1y/sophisticatedcore/client/gui/StorageScreenBase;lastClickSlot:Lnet/minecraft/world/inventory/Slot;",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    public void restClickTime(MouseButtonEvent event, boolean value, CallbackInfoReturnable<Boolean> cir, @Local Slot clickSlot) {
        if (!(this instanceof AbstractContainerScreenMixinAccess access))
            return;

        if (GUITweenConfig.isEnableClickItem()) {
            if (clickSlot == null) {
                access.setGUITween$clickTime(0);
            }
            else {
                access.setGUITween$clickTime(GUITweenConfig.windowItem.clickItemDuration.get().floatValue());

                DragTween dragTween = GUITweenUtility.getDragTween();
                dragTween.stop();
            }
        }
    }
}
