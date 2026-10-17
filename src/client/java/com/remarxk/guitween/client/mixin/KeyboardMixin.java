package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.client.mixinAccess.AbstractContainerScreenMixinAccess;
import com.remarxk.guitween.client.util.DebugUtil;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
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
    private void modifyScreen(long windowPointer, int action, KeyInput key, CallbackInfo ci) {
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
    private void restoreScreen(long windowPointer, int action, KeyInput key, CallbackInfo ci) {
        if (gUITween$screen != null) {
            client.currentScreen = gUITween$screen;
            gUITween$screen = null;
        }
    }
}
