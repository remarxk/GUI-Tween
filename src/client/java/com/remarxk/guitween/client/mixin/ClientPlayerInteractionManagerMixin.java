package com.remarxk.guitween.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.anim.ContainerItemTween;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(
            method = "clickSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
            )
    )
    private void handleContainerClick(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci,
                                      @Local ScreenHandler menu, @Local List<ItemStack> list) {
        try {
            if (!GUITweenClient.CONFIG.isEnableMoveItem() && !GUITweenClient.CONFIG.isEnablePickupItem() && !GUITweenClient.CONFIG.isEnableQuickCraft()) {
                return;
            }

            ContainerItemTween tween = GUITweenUtility.getMoveItemTween();

            List<Integer> changed = new ArrayList<>();
            for (int i = 0; i < menu.slots.size(); i++) {
                if (!ItemStack.areEqual(list.get(i), menu.getSlot(i).getStack())) {
                    changed.add(i);
                }
            }

            if (System.getProperty("guitween.debug.movetween") != null) {
                GUITween.LOGGER.info("[MoveTweenDbg] click menu={} type={} slotId={} slots={} changed={} cursor={}",
                        menu.getClass().getSimpleName(), actionType, slotId, menu.slots.size(), changed, menu.getCursorStack());
            }

            switch (actionType) {
                case QUICK_MOVE -> {
                    if (!GUITweenClient.CONFIG.isEnableMoveItem()) {
                        return;
                    }
                    for (int slot : changed) {
                        if (slot == slotId) {
                            continue;
                        }

                        int movedCount = gUITween$getItemStackCount(menu.getSlot(slot).getStack()) - gUITween$getItemStackCount(list.get(slot));
                        if (movedCount <= 0) {
                            continue;
                        }

                        ItemStack moveItem = list.get(slotId).copy();
                        moveItem.setCount(movedCount);
                        tween.addMoveTween(slotId, slot, moveItem);

                        ItemStack fakeItem = list.get(slot);
                        if (fakeItem == null || fakeItem.isEmpty()) {
                            fakeItem = moveItem.copy();
                            fakeItem.setCount(0);
                        }
                        tween.addFakeItem(menu.getSlot(slot), fakeItem);
                    }
                }
                case SWAP -> {
                    if (!GUITweenClient.CONFIG.isEnableMoveItem()) {
                        return;
                    }
                    int to = -1;
                    for (int slot : changed) {
                        if (slot != slotId) {
                            to = slot;
                            break;
                        }
                    }

                    if (to == -1) {
                        return;
                    }

                    Slot slot1 = menu.getSlot(slotId);
                    Slot slot2 = menu.getSlot(to);

                    if (!tween.swapMoveTween(slot1, slot2)) {
                        tween.removeTween(slotId, to);
                        tween.removeFakeItem(slot2);
                        tween.removeTween(to, slotId);
                        tween.removeFakeItem(slot1);

                        ItemStack itemStack1 = list.get(slotId);
                        if (itemStack1 != null && !itemStack1.isEmpty()) {
                            tween.addMoveTween(slotId, to, itemStack1.copy());
                            tween.addFakeItem(menu.getSlot(to), ItemStack.EMPTY);
                        }

                        ItemStack itemStack2 = list.get(to);
                        if (itemStack2 != null && !itemStack2.isEmpty()) {
                            tween.addMoveTween(to, slotId, itemStack2.copy());
                            tween.addFakeItem(menu.getSlot(slotId), ItemStack.EMPTY);
                        }
                    }
                }
                case PICKUP_ALL -> {
                    if (!GUITweenClient.CONFIG.isEnableMoveItem()) {
                        return;
                    }
                    for (int slot : changed) {
                        int movedCount = gUITween$getItemStackCount(list.get(slot)) - gUITween$getItemStackCount(menu.getSlot(slot).getStack());
                        if (movedCount <= 0) {
                            continue;
                        }

                        ItemStack moveItem = list.get(slot).copy();
                        moveItem.setCount(movedCount);
                        tween.addMoveTween(slot, -1, moveItem);
                    }
                }
                case QUICK_CRAFT -> {
                    if (!GUITweenClient.CONFIG.isEnableQuickCraft()) {
                        return;
                    }
                    for (int slot : changed) {
                        tween.addQuickCraftTween(menu.getSlot(slot));
                    }
                }
                case PICKUP -> {
                    if (!GUITweenClient.CONFIG.isEnablePickupItem()) {
                        return;
                    }
                    for (int slot : changed) {
                        if (!menu.getSlot(slot).getStack().isEmpty()) {
                            tween.addPickupTween(menu.getSlot(slot));
                        }
                    }
                }
                default -> {
                }
            }
        } catch (Exception e) {
            GUITween.LOGGER.warn("GUITween: 容器点击动画处理异常", e);
        }
    }

    @Unique
    private int gUITween$getItemStackCount(ItemStack itemStack) {
        return (itemStack == null || itemStack.isEmpty()) ? 0 : itemStack.getCount();
    }
}
