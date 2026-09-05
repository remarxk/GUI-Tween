package com.remarxk.guitween.mixin.jei;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.compat.CompatUtility;
import mezz.jei.gui.events.GuiEventHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiEventHandler.class)
public class GuiEventHandlerMixin {
    @Inject(
            method = "drawForContainerScreen",
            at = @At(
                    value = "HEAD"
            ),
            require = 0
    )
    private void drawContainerBefore(AbstractContainerScreen<?> screen, GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        CompatUtility.OpenTween openTween = CompatUtility.getOpenTween();

        if (openTween.inTween) {
            GUITweenUtility.popAlpha();
        }
    }

    @Inject(
            method = "drawForContainerScreen",
            at = @At(
                    value = "TAIL"
            ),
            require = 0
    )
    private void drawContainerAfter(AbstractContainerScreen<?> screen, GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        CompatUtility.OpenTween openTween = CompatUtility.getOpenTween();

        if (openTween.inTween) {
            GUITweenUtility.pushAlpha(openTween.alpha);
        }
    }
}
