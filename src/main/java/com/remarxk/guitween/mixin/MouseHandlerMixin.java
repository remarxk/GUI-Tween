package com.remarxk.guitween.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private boolean mouseGrabbed;

    @Shadow
    private double xpos;

    @Shadow
    private double ypos;

    @Shadow
    private boolean ignoreFirstMove;

    @Unique
    private Screen gUITween$screen;

    @Unique
    private void gUITween$switchToGameMouse() {
        Screen screen = minecraft.screen;
        if (!(screen instanceof AbstractContainerScreenMixinAccess access)) {
            return;
        }

        if (!access.gUITween$inCloseTween()) {
            return;
        }

        if (minecraft.isWindowActive() && !mouseGrabbed) {
            mouseGrabbed = true;
            xpos = minecraft.getWindow().getScreenWidth() / 2.0;
            ypos = minecraft.getWindow().getScreenHeight() / 2.0;
            InputConstants.grabOrReleaseMouse(minecraft.getWindow(), 212995, xpos, ypos);
            ignoreFirstMove = true;
        }

        gUITween$screen = screen;
        minecraft.screen = null;
    }

    @Unique
    private void gUITween$restoreScreen() {
        if (gUITween$screen != null) {
            minecraft.screen = gUITween$screen;
            gUITween$screen = null;
        }
    }

    @Inject(
            method = "handleAccumulatedMovement",
            at = @At("HEAD")
    )
    private void modifyMouseMoveBefore(CallbackInfo ci) {
        gUITween$switchToGameMouse();
    }

    @Inject(
            method = "handleAccumulatedMovement",
            at = @At("RETURN")
    )
    private void modifyMouseMoveAfter(CallbackInfo ci) {
        gUITween$restoreScreen();
    }

    @Inject(
            method = "onButton",
            at = @At("HEAD")
    )
    private void modifyMouseButtonBefore(long handle, MouseButtonInfo buttonInfo, int action, CallbackInfo ci) {
        gUITween$switchToGameMouse();
    }

    @Inject(
            method = "onButton",
            at = @At("RETURN")
    )
    private void modifyMouseButtonAfter(long handle, MouseButtonInfo buttonInfo, int action, CallbackInfo ci) {
        gUITween$restoreScreen();
    }

    @Inject(
            method = "onScroll",
            at = @At("HEAD")
    )
    private void modifyMouseScrollBefore(long handle, double xoffset, double yoffset, CallbackInfo ci) {
        gUITween$switchToGameMouse();
    }

    @Inject(
            method = "onScroll",
            at = @At("RETURN")
    )
    private void modifyMouseScrollAfter(long handle, double xoffset, double yoffset, CallbackInfo ci) {
        gUITween$restoreScreen();
    }
}
