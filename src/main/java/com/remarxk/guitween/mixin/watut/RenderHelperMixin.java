//package com.remarxk.guitween.mixin.watut;
//
//import com.corosus.watut.client.screen.RenderHelper;
//import com.remarxk.guitween.eventListener.ScreenRenderListener;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiGraphics;
//import net.neoforged.neoforge.client.event.ScreenEvent;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(RenderHelper.class)
//public class RenderHelperMixin {
//    @Inject(
//        method = "renderWithTooltipEnd",
//        at = @At(
//                value = "INVOKE",
//                target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"
//        )
//    )
//    private static void renderWithTooltipEndAfter(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo ci) {
//        if (Minecraft.getInstance().screen != null) {
//            ScreenEvent.Render.Post event = new ScreenEvent.Render.Post(
//                    Minecraft.getInstance().screen,
//                    pGuiGraphics,
//                    pMouseX,
//                    pMouseY,
//                    pPartialTick
//            );
//            ScreenRenderListener.postRenderScreen(event);
//        }
//    }
//}
