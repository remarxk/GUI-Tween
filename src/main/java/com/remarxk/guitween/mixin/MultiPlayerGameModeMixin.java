package com.remarxk.guitween.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.anim.ContainerItemTween;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Inject(
            method = "handleInventoryMouseClick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"
            )
    )
    private void handleInventoryMouseClickBefore(int containerId, int slotId, int mouseButton, ClickType clickType, Player player, CallbackInfo ci, @Local List<ItemStack> list, @Local Int2ObjectMap<ItemStack> int2objectmap) {
        if (!GUITween.CONFIG.isEnableMoveItem()) {
            return;
        }

        AbstractContainerMenu menu = player.containerMenu;

        ContainerItemTween tween = GUITweenUtility.getMoveItemTween();

        switch (clickType) {
            case QUICK_MOVE -> {
                int2objectmap.forEach((slot, itemStack) -> {
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
                for (Int2ObjectMap.Entry<ItemStack> entry : int2objectmap.int2ObjectEntrySet()) {
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
                int2objectmap.forEach((slot, itemStack) -> {
                    ItemStack moveItem = list.get(slot).copy();
                    moveItem.setCount(gUITween$getItemStackCount(list.get(slot)) - itemStack.getCount());

                    tween.addMoveTween(slot, -1, moveItem);
                });
            }
            case QUICK_CRAFT -> {
                int2objectmap.forEach((slot, itemStack) -> {
                    tween.addQuickCraftTween(menu.getSlot(slot));
                });
            }
            case PICKUP -> {
                int2objectmap.forEach((slot, itemStack) -> {
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
