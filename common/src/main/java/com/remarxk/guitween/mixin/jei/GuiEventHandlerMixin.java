package com.remarxk.guitween.mixin.jei;

import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.compat.CompatUtility;
import mezz.jei.gui.events.GuiEventHandler;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiEventHandler.class)
public class GuiEventHandlerMixin {
    @Inject(
            method = "drawForScreenForeground",
            at = @At(
                    value = "HEAD"
            ),
            require = 0
    )
    private void drawMainContentsBefore(Screen screen, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        CompatUtility.OpenTween openTween = CompatUtility.getOpenTween();

        if (openTween.inTween) {
            GUITweenUtility.popAlpha();
        }
    }

    @Inject(
            method = "drawForScreenForeground",
            at = @At(
                    value = "TAIL"
            ),
            require = 0
    )
    private void drawMainContentsAfter(Screen screen, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        CompatUtility.OpenTween openTween = CompatUtility.getOpenTween();

        if (openTween.inTween) {
            GUITweenUtility.pushAlpha(openTween.alpha);
        }
    }
}