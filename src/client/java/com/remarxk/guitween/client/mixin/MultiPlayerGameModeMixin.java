package com.remarxk.guitween.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.anim.ContainerItemTween;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
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

import java.util.List;

@Mixin(ClientPlayerInteractionManager.class)
public class MultiPlayerGameModeMixin {
    @Inject(
            method = "clickSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
            )
    )
    private void handleInventoryMouseClickBefore(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci, @Local List<ItemStack> list, @Local Int2ObjectMap<ItemStack> int2ObjectMap) {
        if (!GUITweenClient.CONFIG.isEnableMoveItem()) {
            return;
        }

        ScreenHandler menu = player.currentScreenHandler;

        ContainerItemTween tween = GUITweenUtility.getMoveItemTween();

        switch (actionType) {
            case QUICK_MOVE -> {
                int2ObjectMap.forEach((slot, itemStack) -> {
                    if (slot != slotId) {
                        ItemStack moveItem = itemStack.copy();
                        moveItem.setCount(itemStack.getCount() - gUITween$getItemStackCount(list.get(slot)));
                        tween.addMoveTween(slotId, slot, moveItem);

                        ItemStack fakeItem = list.get(slot);
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
                for (Int2ObjectMap.Entry<ItemStack> entry : int2ObjectMap.int2ObjectEntrySet()) {
                    if (entry.getIntKey() != slotId) {
                        to = entry.getIntKey();
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
                int2ObjectMap.forEach((slot, itemStack) -> {
                    ItemStack moveItem = list.get(slot).copy();
                    moveItem.setCount(gUITween$getItemStackCount(list.get(slot)) - itemStack.getCount());

                    tween.addMoveTween(slot, -1, moveItem);
                });
            }
            case QUICK_CRAFT -> {
                int2ObjectMap.forEach((slot, itemStack) -> {
                    tween.addQuickCraftTween(menu.getSlot(slot));
                });
            }
            case PICKUP -> {
                int2ObjectMap.forEach((slot, itemStack) -> {
                    if (!itemStack.isEmpty()) {
                        tween.addPickupTween(menu.getSlot(slot));
                    }
                });
            }
        }
    }

    @Unique
    private int gUITween$getItemStackCount(ItemStack itemStack) {
        return (itemStack == null || itemStack.isEmpty()) ? 0 : itemStack.getCount();
    }
}
