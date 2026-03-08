package com.remarxk.guitween.mixin.emi;

import com.remarxk.guitween.compat.EmiCompat;
import dev.emi.emi.screen.StackBatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StackBatcher.class, remap = false)
public class EmiStackBatcherMixin {
    @Shadow
    public void repopulate() {}

    @Inject(
            method = "draw",
            at = @At(
                    value = "HEAD"
            )
    )
    private void drawBefore(CallbackInfo ci) {
        if (EmiCompat.inTween) {
            repopulate();
        }
    }
}
