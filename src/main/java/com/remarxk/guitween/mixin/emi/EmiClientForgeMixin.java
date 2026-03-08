package com.remarxk.guitween.mixin.emi;

import dev.emi.emi.EmiPort;
import dev.emi.emi.platform.forge.EmiClientForge;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.EmiScreenBase;
import dev.emi.emi.screen.EmiScreenManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.ScreenEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EmiClientForge.class)
public class EmiClientForgeMixin {
    @Inject(
            method ="renderScreenForeground",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true,
            remap = false
    )
    private static void cancelRenderScreenForeground(ContainerScreenEvent.Render.Foreground event, CallbackInfo ci) {
//        GUITween.LOGGER.info("渲染前景");
        ci.cancel();
    }

    @Inject(
            method = "postRenderScreen",
            at = @At(
                    value = "HEAD"
            ),
            remap = false
    )
    private static void renderForeground(ScreenEvent.Render.Post event, CallbackInfo ci) {
        EmiDrawContext context = EmiDrawContext.wrap(event.getGuiGraphics());
        Screen screen = event.getScreen();
        if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) {
            return;
        }

        EmiScreenBase base = EmiScreenBase.of(containerScreen);
        if (base != null) {
            Minecraft client = Minecraft.getInstance();
            EmiPort.setPositionTexShader();
            EmiScreenManager.render(context, event.getMouseX(), event.getMouseY(), client.getPartialTick());
        }
    }
}
