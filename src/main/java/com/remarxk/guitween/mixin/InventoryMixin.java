package com.remarxk.guitween.mixin;

import com.remarxk.guitween.eventListener.HotbarChangeListener;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public class InventoryMixin {
    @Shadow
    public int selected;

    @Inject(
            method = "swapPaint",
            at = @At(
                    value = "TAIL"
            )
    )
    private void swapPaintAfter(double pDirection, CallbackInfo ci) {
        HotbarChangeListener.scrollDir = pDirection > 0 ? -1 : 1;
        HotbarChangeListener.scrollSelected = selected;
    }
}
