package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.client.mixinAccess.AbstractContainerScreenMixinAccess;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardHandlerMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Unique
    private Screen gUITween$screen;

    @Inject(
            method = "onKey",
            at = @At(
                    value = "HEAD"
            )
    )
    private void modifyScreen(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        Screen screen = client.currentScreen;
        if (screen instanceof AbstractContainerScreenMixinAccess access) {
            if (access.gUITween$inCloseTween()) {
                gUITween$screen = screen;
                client.currentScreen = null;
            }
        }
    }

    @Inject(
            method = "onKey",
            at = @At(
                    value = "RETURN"
            )
    )
    private void restoreScreen(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (gUITween$screen != null) {
            client.currentScreen = gUITween$screen;
            gUITween$screen = null;
        }
    }
}
