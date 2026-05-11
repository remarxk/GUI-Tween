package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.client.eventListener.HotbarChangeListener;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class InventoryMixin {
    @Shadow
    public int selectedSlot;

    @Inject(
            method = "scrollInHotbar",
            at = @At(
                    value = "TAIL"
            )
    )
    private void swapPaintAfter(double pDirection, CallbackInfo ci) {
        HotbarChangeListener.scrollDir = pDirection > 0 ? -1 : 1;
        HotbarChangeListener.scrollSelected = selectedSlot;
    }
}
