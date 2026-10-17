package com.remarxk.guitween.mixin;

import com.remarxk.guitween.GUITweenUtility;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
public class KeyMappingMixin {
    @Shadow
    private boolean isDown;

    @Inject(
            method = "isDown",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void ignoreConflictContextWhileClosing(CallbackInfoReturnable<Boolean> cir) {
        if (GUITweenUtility.isWindowClosing) {
            cir.setReturnValue(isDown);
        }
    }
}
