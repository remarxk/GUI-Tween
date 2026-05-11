package com.remarxk.guitween.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.remarxk.guitween.client.mixinAccess.AbstractContainerScreenMixinAccess;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractParentElement implements Drawable {
    @WrapOperation(
            method = "keyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;close()V")
    )
    private void onCloseBefore(Screen screen, Operation<Void> original) {
        if ((Object)this instanceof HandledScreen<?> containerScreen) {
            if (containerScreen instanceof AbstractContainerScreenMixinAccess access) {
                if (access.gUITween$playCloseTween()) {
                    return;
                }
            }
        }

        original.call(screen);
    }
}
