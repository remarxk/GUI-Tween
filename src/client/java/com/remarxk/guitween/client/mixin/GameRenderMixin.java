package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.client.mixinAccess.AbstractContainerScreenMixinAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GameRenderer.class)
public class GameRenderMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @ModifyArgs(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;renderWithTooltip(Lnet/minecraft/client/gui/DrawContext;IIF)V")
    )
    private void modifyScreenMousePos(Args args) {
        if (client.currentScreen instanceof AbstractContainerScreenMixinAccess access) {
            if (access.gUITween$inCloseTween()) {
                args.set(1, -9999);
                args.set(2, -9999);
            }
        }
    }
}
