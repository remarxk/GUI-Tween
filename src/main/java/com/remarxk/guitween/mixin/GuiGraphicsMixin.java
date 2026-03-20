package com.remarxk.guitween.mixin;

import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.mixinAccess.GuiItemRenderStateMixinAccess;
import com.remarxk.guitween.mixinAccess.GuiRenderStateMixinAccess;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    @Shadow
    private Runnable deferredTooltip;

    @Inject(
            method = "<init>(Lnet/minecraft/client/Minecraft;Lorg/joml/Matrix3x2fStack;Lnet/minecraft/client/gui/render/state/GuiRenderState;II)V",
            at = @At(
                    value = "TAIL"
            )
    )
    private void initAfter(Minecraft minecraft, Matrix3x2fStack pose, GuiRenderState guiRenderState, int mouseX, int mouseY, CallbackInfo ci) {
        if (guiRenderState instanceof GuiRenderStateMixinAccess access) {
            access.setGUITween$GuiGraphics((GuiGraphics) ((Object) this));
        }
    }

    @ModifyVariable(
            method = "drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;IIIZ)V",
            at = @At(
                    value = "HEAD"
            ),
            index = 5,
            argsOnly = true
    )
    private int modifyFontColor(int color) {
        if (GUITweenUtility.hasFontAlpha()) {
            int a = (int)(((color >> 24) & 0xFF) * GUITweenUtility.peekFontAlpha());
            color = (a << 24) | (color & 0x00FFFFFF);
        }

        return color;
    }

    @Redirect(
            method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/lang/String;Lorg/joml/Matrix3x2f;Lnet/minecraft/client/renderer/item/TrackingItemStackRenderState;IILnet/minecraft/client/gui/navigation/ScreenRectangle;)Lnet/minecraft/client/gui/render/state/GuiItemRenderState;")
    )
    private GuiItemRenderState redirectNewGuiItemRenderState(String name, Matrix3x2f pose, TrackingItemStackRenderState itemStackRenderState, int x, int y, ScreenRectangle scissorArea) {
        GuiItemRenderState renderState = new GuiItemRenderState(name, pose, itemStackRenderState, x, y, scissorArea);

        if (GUITweenUtility.hasItemAlpha()) {
            if ((Object) renderState instanceof GuiItemRenderStateMixinAccess access) {
                int alpha = (int) (GUITweenUtility.peekItemAlpha() * 255);
                access.setGUITween$alpha(alpha);
            }
        }

        return renderState;
    }

    @ModifyVariable(
            method = "innerBlit",
            at = @At(
                    value = "HEAD"
            ),
            index = 11,
            argsOnly = true)
    private int modifyInnerAlpha(int color) {
        if (GUITweenUtility.hasSpriteAlpha()) {
            int a = (int)(((color >> 24) & 0xFF) * GUITweenUtility.peekSpriteAlpha());
            color = (a << 24) | (color & 0x00FFFFFF);
        }

        return color;
    }

    @ModifyVariable(
            method = "fill(Lcom/mojang/blaze3d/pipeline/RenderPipeline;IIIII)V",
            at = @At(
                    value = "HEAD"
            ),
            index = 6,
            argsOnly = true
    )
    private int modifyFillAlpha(int color) {
        if (GUITweenUtility.hasSpriteAlpha()) {
            int a = (int)(((color >> 24) & 0xFF) * GUITweenUtility.peekSpriteAlpha());
            color = (a << 24) | (color & 0x00FFFFFF);
        }

        return color;
    }

    @Inject(
            method = "setTooltipForNextFrameInternal",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;deferredTooltip:Ljava/lang/Runnable;",
                    shift = At.Shift.AFTER
            )
    )
    private void setTooltipForNextFrameInternalAfter(Font font, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner positioner, @Nullable Identifier background, boolean focused, CallbackInfo ci) {
        if (!GUITweenUtility.inTooltipTween)
            return;

        Runnable curDeferredTooltip = deferredTooltip;

        float duration = GUITweenConfig.windowItem.tooltipDuration.get().floatValue();
        float progress = GUITweenUtility.tooltipTweenTick / duration;
        float alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, progress, GUITweenConfig.windowItem.tooltipEase.get());

        deferredTooltip = () -> {
            GUITweenUtility.pushSpriteAlpha(alpha);
            GUITweenUtility.pushFontAlpha(alpha);

            if (curDeferredTooltip != null) {
                curDeferredTooltip.run();
            }

            GUITweenUtility.popSpriteAlpha();
            GUITweenUtility.popFontAlpha();
        };
    }
}
