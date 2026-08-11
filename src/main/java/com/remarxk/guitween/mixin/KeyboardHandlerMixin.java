package com.remarxk.guitween.mixin;

import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private Screen gUITween$screen;

    @Inject(
            method = "keyPress",
            at = @At(
                    value = "HEAD"
            )
    )
    private void modifyScreen(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        Screen screen = minecraft.screen;
        if (screen instanceof AbstractContainerScreenMixinAccess access) {
            if (access.gUITween$inCloseTween()) {
                gUITween$screen = screen;
                minecraft.screen = null;
            }
        }
    }

    @Inject(
            method = "keyPress",
            at = @At(
                    value = "RETURN"
            )
    )
    private void restoreScreen(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (gUITween$screen != null) {
            minecraft.screen = gUITween$screen;
            gUITween$screen = null;
        }
    }
}
