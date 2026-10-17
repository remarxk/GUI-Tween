package com.remarxk.guitween.mixin;

import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.neoforged.neoforge.client.ClientHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Redirect(
            method = "render(Lnet/minecraft/client/DeltaTracker;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/client/ClientHooks;drawScreen(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/gui/GuiGraphics;IIF)V"
            )
    )
    private void gUITween$drawScreen(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (screen instanceof AbstractContainerScreenMixinAccess access && access.gUITween$inCloseTween()) {
            mouseX = -9999;
            mouseY = -9999;
        }

        ClientHooks.drawScreen(screen, guiGraphics, mouseX, mouseY, partialTick);
    }
}
