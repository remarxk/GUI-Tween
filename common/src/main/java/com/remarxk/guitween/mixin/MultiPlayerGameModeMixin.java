package com.remarxk.guitween.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.anim.ContainerItemTween;
import com.remarxk.guitween.config.GUITweenConfig;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
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
            method = "handleContainerInput",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"
            )
    )
    private void handleContainerInputBefore(int containerId, int slotId, int mouseButton, ContainerInput input, Player player, CallbackInfo ci, @Local List<ItemStack> list, @Local Int2ObjectMap<ItemStack> int2objectmap) {
        try {
            if (!GUITweenConfig.isEnableMoveItem()) {
                return;
            }

            AbstractContainerMenu menu = player.containerMenu;

            ContainerItemTween tween = GUITweenUtility.getMoveItemTween();

            switch (input) {
                case QUICK_MOVE -> {
                    int2objectmap.keySet().forEach(slot -> {
                        if (slot != slotId) {
                            int movedCount = gUITween$getItemStackCount(menu.getSlot(slot).getItem()) - gUITween$getItemStackCount(list.get(slot));
                            if (movedCount <= 0) {
                                return;
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
                    int2objectmap.keySet().forEach(slot -> {
                        int movedCount = gUITween$getItemStackCount(list.get(slot)) - gUITween$getItemStackCount(menu.getSlot(slot).getItem());
                        if (movedCount <= 0) {
                            return;
                        }

                        ItemStack moveItem = list.get(slot).copy();
                        moveItem.setCount(movedCount);
                        tween.addMoveTween(slot, -1, moveItem);
                    });
                }
                case QUICK_CRAFT -> {
                    int2objectmap.keySet().forEach(slot -> {
                        tween.addQuickCraftTween(menu.getSlot(slot));
                    });
                }
                case PICKUP -> {
                    int2objectmap.keySet().forEach(slot -> {
                        if (!menu.getSlot(slot).getItem().isEmpty()) {
                            tween.addPickupTween(menu.getSlot(slot));
                        }
                    });
                }
            }
        } catch (Exception e) {
            com.remarxk.guitween.Constants.LOGGER.warn("GUITween: 容器点击动画处理异常", e);
        }
    }

    @Unique
    private int gUITween$getItemStackCount(ItemStack itemStack) {
        return (itemStack == null || itemStack.isEmpty()) ? 0 : itemStack.getCount();
    }
}
