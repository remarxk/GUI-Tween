package com.remarxk.guitween.mixin.sophisticated;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.anim.ContainerItemTween;
import com.remarxk.guitween.anim.Tween;
import com.remarxk.guitween.anim.TweenPool;
import com.remarxk.guitween.compat.CompatUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.util.Ease;
import com.remarxk.guitween.util.Tuple;
import com.remarxk.guitween.util.TweenUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.InventoryScrollPanel;
import net.p3pp3rf1y.sophisticatedcore.common.gui.StorageContainerMenuBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

@Mixin(value = StorageScreenBase.class)
public abstract class StorageScreenBaseMixin<S extends StorageContainerMenuBase<?>> extends AbstractContainerScreen<S> implements InventoryScrollPanel.IInventoryScreen {
    public StorageScreenBaseMixin(S menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/p3pp3rf1y/sophisticatedcore/client/gui/UpgradeSettingsTabControl;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"
            )
    )
    public void renderBefore(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!(this instanceof AbstractContainerScreenMixinAccess access))
            return;

        GUITweenUtility.setOpenScreen(access.getGUITween$screenName(), GUITweenUtility.openScreenTick);
        GUITweenUtility.jeiOpenTick = Math.max(GUITweenUtility.jeiOpenTick, GUITweenUtility.openScreenTick);

        if (!GUITweenConfig.isEnableWindow())
            return;

        if (access.getGUITween$inTween()) { // 某些界面重写了render方法，导致没有取消渲染动画，需要强行终止
            access.setGUITween$inTween(false);
            access.setGUITween$isDisableScreenTween(true);

            GUITweenUtility.popAlpha();

            CompatUtility.endOpenTween();
        }

        if (access.getGUITween$isDisableScreenTween())
            return;

        float moveProgress = GUITweenUtility.openScreenTick / GUITweenConfig.window.moveDuration.get().floatValue();
        float gradientProgress = GUITweenUtility.openScreenTick / GUITweenConfig.window.gradientDuration.get().floatValue();

        if (moveProgress >= 1 && gradientProgress >= 1)
            return;

        access.setGUITween$inTween(true);

        float dx = TweenUtil.tween(GUITweenConfig.window.moveX.get().floatValue(), 0, moveProgress, GUITweenConfig.window.moveEase.get());
        float dy = TweenUtil.tween(GUITweenConfig.window.moveY.get().floatValue(), 0, moveProgress, GUITweenConfig.window.moveEase.get());
        float alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, gradientProgress, GUITweenConfig.window.gradientEase.get());

        CompatUtility.startOpenTween(dx, dy, alpha);

        PoseStack poseStack = guiGraphics.pose();

        // 动画变换
        poseStack.pushPose();
        poseStack.translate(dx, dy, 0);  // 上移

        GUITweenUtility.pushAlpha(alpha);
    }

    @Nullable
    @Shadow
    public Slot findSlot(double mouseX, double mouseY) { return null; }

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

        Slot hoveredSlot = findSlot(pMouseX, pMouseY);

        Slot gUITween$lastHoverSlot = access.getGUITween$lastHoverSlot();
        HashMap<Slot, Tween> gUITween$hoverSlotMap = access.getGUITween$hoverSlotMap();

        if (hoveredSlot != gUITween$lastHoverSlot) {

            if (gUITween$lastHoverSlot != null) {
                Tween tween = gUITween$hoverSlotMap.getOrDefault(gUITween$lastHoverSlot, null);
                if (tween != null) {
                    tween.rewind = true;
                }
            }

            if (GUITweenConfig.isEnableTooltip()) {
                if (gUITween$lastHoverSlot == null || !gUITween$lastHoverSlot.hasItem()) {
                    access.setGUITween$tooltipShowTick(0);
                }
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

    @Inject(
            method = "renderSuper",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V",
                    ordinal = 1
            )
    )
    private void renderMoveItem(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!(this instanceof AbstractContainerScreenMixinAccess access))
            return;

        ContainerItemTween tween = GUITweenUtility.getMoveItemTween();
        HashMap<Integer, HashMap<Integer, ContainerItemTween.Single>> toMap = tween.getToMap();
        toMap.forEach((to, fromList) -> {
            int targetX, targetY;
            if (to != -1) {
                Slot toSlot = menu.getSlot(to);
                targetX = toSlot.x;
                targetY = toSlot.y;
            }
            else {
                int i2 = access.gUITween$getDraggingItem().isEmpty() ? 8 : 16;
                targetX = mouseX - leftPos - 8;
                targetY = mouseY - topPos - i2;
            }

            for (ContainerItemTween.Single single : fromList.values()) {
                Slot fromSlot = menu.getSlot(single.from);
                Tuple<Integer, Integer> pos = tween.getMoveTweenValue(fromSlot.x, fromSlot.y, targetX, targetY, single, to, menu);
                if (pos != null) {
                    access.gUITween$renderFloatingItem(guiGraphics, single.itemStack, pos.getA(), pos.getB(), null);
                }
            }
        });

        tween.removeUnuseFakeItem(menu);
    }

    @Redirect(
            method = "renderSuper",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/p3pp3rf1y/sophisticatedcore/client/gui/StorageScreenBase;renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;IIII)V"
            )
    )
    private void disableRenderSlotHighlight(GuiGraphics guiGraphics, int pX, int pY, int pBlitOffset, int color) {
        if (!GUITweenConfig.isEnableHoverItem()) {
            AbstractContainerScreen.renderSlotHighlight(guiGraphics, pX, pY, pBlitOffset, color);
        }
    }

    @Inject(
            method = "renderSlot",
            at = @At(
                    value = "HEAD"
            )
    )
    private void renderSlotBefore(GuiGraphics pGuiGraphics, Slot pSlot, CallbackInfo ci) {
        if (!(this instanceof AbstractContainerScreenMixinAccess access))
            return;

        boolean haveTween = false;
        float scale = 1;
        //        float angle = 0;
        float dx = 0;
        float dy = 0;

        PoseStack poseStack = pGuiGraphics.pose();
        float itemSize = 16f; // 物品渲染尺寸（固定16x16）
        float centerX = pSlot.x + itemSize / 2; // 物品中心X
        float centerY = pSlot.y + itemSize / 2; // 物品中心Y

        boolean isEmpty = !pSlot.hasItem();

        HashMap<Slot, Tween> gUITween$hoverSlotMap = access.getGUITween$hoverSlotMap();

        if (GUITweenConfig.isEnableHoverItem()) {
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

        ContainerItemTween containerItemTween = GUITweenUtility.getMoveItemTween();

        if (menu.getSlot(pSlot.index) == pSlot) {
            Float quickScale = containerItemTween.getQuickCraftScale(pSlot);
            if (quickScale != null) {
                scale = quickScale;
                haveTween = true;
            }

            Float pickUpScale = containerItemTween.getPickupScale(pSlot);
            if (pickUpScale != null) {
                scale = pickUpScale;
                haveTween = true;
            }
        }

        Float finishScale = containerItemTween.getFinishScale(pSlot);
        if (finishScale != null) {
            scale = finishScale;
            haveTween = true;
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

            poseStack.pushPose();

            // 矩阵操作：平移到中心 → 缩放 → 平移回原位置
            poseStack.translate(centerX, centerY, 0);
            poseStack.scale(scale, scale, 1.0f); // Z轴缩放不影响2D渲染，设为1
            //            poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
            poseStack.translate(-centerX, -centerY, 50);

            poseStack.translate(dx, dy, 50);
        }
    }

    @WrapOperation(
            method = "renderSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/Slot;getItem()Lnet/minecraft/world/item/ItemStack;",
                    ordinal = 0
            )
    )
    public ItemStack renderItemFake(Slot instance, Operation<ItemStack> original, @Local(argsOnly = true) Slot slot) {
        ContainerItemTween containerItemTween = GUITweenUtility.getMoveItemTween();
        ItemStack itemStack = containerItemTween.getFakeItem(instance);
        if (itemStack != null) {
            return itemStack;
        }

        return original.call(instance);
    }

    @Inject(
            method = "renderSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/p3pp3rf1y/sophisticatedcore/common/gui/StorageContainerMenuBase;getQuickCraftPlaceCount(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/item/ItemStack;)I"
            )
    )
    public void renderQuickItem(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        if (!(this instanceof AbstractContainerScreenMixinAccess access))
            return;

        if (!GUITweenConfig.isEnableQuickCraft())
            return;

        access.setGUITween$isRenderQuick(true);
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
            PoseStack poseStack = guiGraphics.pose();

            float centerX = x + 8;
            float centerY = y + 8;
            float scale = GUITweenConfig.windowItem.clickItemScale.get().floatValue();

            poseStack.pushPose();

            poseStack.translate(centerX, centerY , 0);
            poseStack.scale(scale, scale, 1);
            poseStack.translate(-centerX, -centerY, 0);
        }
    }

    @Inject(
            method = "renderSlot",
            at = @At(
                    value = "TAIL"
            )
    )
    private void renderSlotAfter(GuiGraphics pGuiGraphics, Slot pSlot, CallbackInfo ci) {
        if (!(this instanceof AbstractContainerScreenMixinAccess access))
            return;

        if (access.getGUITween$isRenderQuick()) {
            access.setGUITween$isRenderQuick(false);
            pGuiGraphics.pose().popPose();
        }

        if (access.getGUITween$inSlotTween()) {
            access.setGUITween$inSlotTween(false);
            pGuiGraphics.pose().popPose();
        }

        if (GUITweenConfig.isEnableHoverItem() && access.getGUITween$lastHoverSlot() == pSlot) {
            AbstractContainerScreen.renderSlotHighlight(pGuiGraphics, pSlot.x, pSlot.y, 0, getSlotColor(pSlot.index));
        }

        if (GUITweenConfig.enableDebugWindow.get()) {
            Font font = Minecraft.getInstance().font;

            // 获取格子左上角坐标
            int x = pSlot.x; // Slot 的 x 坐标
            int y = pSlot.y; // Slot 的 y 坐标

            String text = String.valueOf(pSlot.index);
            int color = 0xFF0000; // 白色文字
            boolean shadow = true; // 阴影，让文字在物品上更清晰

            PoseStack poseStack = pGuiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate(0, 0, 1000);
            pGuiGraphics.drawString(font, text, x + 1, y + 1, color, shadow);
            poseStack.popPose();
        }
    }

    @Inject(
            method = "handleInventoryMouseClick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"
            )
    )
    private void handleInventoryMouseClickBefore(int slotNumber, int mouseButton, ClickType type, CallbackInfo ci,
                                                 @Local(index = 5) List<ItemStack> inventoryItems,
                                                 @Local(index = 6) List<ItemStack> upgradeItems,
                                                 @Local(index = 7) Int2ObjectMap<ItemStack> changedSlotIndexes) {
        ContainerItemTween tween = GUITweenUtility.getMoveItemTween();

        switch (type) {
            case QUICK_MOVE -> {
                changedSlotIndexes.forEach((slot, itemStack) -> {
                    if (slot != slotNumber) {
                        ItemStack beforeStack = gUITween$getBeforeStack(inventoryItems, upgradeItems, slot);

                        ItemStack moveItem = itemStack.copy();
                        moveItem.setCount(itemStack.getCount() - gUITween$getItemStackCount(beforeStack));
                        tween.addMoveTween(slotNumber, slot, moveItem);

                        ItemStack fakeItem = beforeStack;
                        if (fakeItem == null || fakeItem.isEmpty()) {
                            fakeItem = itemStack.copy();
                            fakeItem.setCount(0);
                        }
                        tween.addFakeItem(menu.getSlot(slot), fakeItem);
                    }
                });
            }
            case SWAP -> {
                int to = -1;
                for (Int2ObjectMap.Entry<ItemStack> entry : changedSlotIndexes.int2ObjectEntrySet()) {
                    if (entry.getIntKey() != slotNumber) {
                        to = entry.getIntKey();
                        break;
                    }
                }

                if (to == -1) {
                    return;
                }

                Slot slot1 = menu.getSlot(slotNumber);
                Slot slot2 = menu.getSlot(to);

                if (!tween.swapMoveTween(slot1, slot2)) {
                    tween.removeTween(slotNumber, to);
                    tween.removeFakeItem(slot2);
                    tween.removeTween(to, slotNumber);
                    tween.removeFakeItem(slot1);

                    ItemStack itemStack1 = gUITween$getBeforeStack(inventoryItems, upgradeItems, slotNumber);
                    if (itemStack1 != null && !itemStack1.isEmpty()) {
                        tween.addMoveTween(slotNumber, to, itemStack1.copy());
                        tween.addFakeItem(menu.getSlot(to), ItemStack.EMPTY);
                    }

                    ItemStack itemStack2 = gUITween$getBeforeStack(inventoryItems, upgradeItems, to);
                    if (itemStack2 != null && !itemStack2.isEmpty()) {
                        tween.addMoveTween(to, slotNumber, itemStack2.copy());
                        tween.addFakeItem(menu.getSlot(slotNumber), ItemStack.EMPTY);
                    }
                }
            }
            case PICKUP_ALL -> {
                changedSlotIndexes.forEach((slot, itemStack) -> {
                    ItemStack beforeStack = gUITween$getBeforeStack(inventoryItems, upgradeItems, slot);
                    ItemStack moveItem = beforeStack.copy();
                    moveItem.setCount(gUITween$getItemStackCount(beforeStack) - itemStack.getCount());

                    tween.addMoveTween(slot, -1, moveItem);
                });
            }
            case QUICK_CRAFT -> {
                changedSlotIndexes.forEach((slot, itemStack) -> {
                    tween.addQuickCraftTween(menu.getSlot(slot));
                });
            }
            case PICKUP -> {
                changedSlotIndexes.forEach((slot, itemStack) -> {
                    if (!itemStack.isEmpty()) {
                        tween.addPickupTween(menu.getSlot(slot));
                    }
                });
            }
        }
    }

    @Unique
    private ItemStack gUITween$getBeforeStack(List<ItemStack> inventoryItems, List<ItemStack> upgradeItems, int slot) {
        if (slot >= 0 && slot < inventoryItems.size()) {
            return inventoryItems.get(slot);
        }

        int upgradeIndex = slot - this.menu.getInventorySlotsSize();
        if (upgradeIndex >= 0 && upgradeIndex < upgradeItems.size()) {
            return upgradeItems.get(upgradeIndex);
        }

        return ItemStack.EMPTY;
    }

    @Unique
    private int gUITween$getItemStackCount(ItemStack itemStack) {
        return (itemStack == null || itemStack.isEmpty()) ? 0 : itemStack.getCount();
    }
}
