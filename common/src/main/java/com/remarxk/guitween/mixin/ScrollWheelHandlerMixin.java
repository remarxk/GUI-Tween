package com.remarxk.guitween.mixin;

import com.remarxk.guitween.eventListener.HotbarChangeListener;
import net.minecraft.client.ScrollWheelHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScrollWheelHandler.class)
public class ScrollWheelHandlerMixin {
    @Inject(
            method = "getNextScrollWheelSelection",
            at = @At(
                    value = "TAIL"
            )
    )
    private static void scrollWheelSelectionAfter(double yOffset, int selected, int selectionSize, CallbackInfoReturnable<Integer> cir) {
        HotbarChangeListener.scrollDir = yOffset > 0 ? -1 : 1;
        HotbarChangeListener.scrollSelected = selected;
    }
}
