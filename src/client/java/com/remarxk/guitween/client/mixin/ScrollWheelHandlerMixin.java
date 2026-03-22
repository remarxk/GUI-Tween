package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.client.eventListener.HotbarChangeListener;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.Scroller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Scroller.class)
public class ScrollWheelHandlerMixin {
    @Inject(
            method = "scrollCycling",
            at = @At(
                    value = "TAIL"
            )
    )
    private static void scrollWheelSelectionAfter(double amount, int selectedIndex, int total, CallbackInfoReturnable<Integer> cir) {
        HotbarChangeListener.scrollDir = amount > 0 ? -1 : 1;
        HotbarChangeListener.scrollSelected = selectedIndex;
    }
}
