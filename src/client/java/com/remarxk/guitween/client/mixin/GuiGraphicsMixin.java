package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.mixinAccess.GuiItemRenderStateMixinAccess;
import com.remarxk.guitween.client.mixinAccess.GuiRenderStateMixinAccess;
import com.remarxk.guitween.client.util.TweenUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.render.item.KeyedItemRenderState;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DrawContext.class)
public class GuiGraphicsMixin {
    @Shadow
    private Runnable tooltipDrawer;

    @Inject(
            method = "<init>(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/gui/render/state/GuiRenderState;II)V",
            at = @At(
                    value = "TAIL"
            )
    )
    private void initAfter(MinecraftClient client, GuiRenderState state, int mouseX, int mouseY, CallbackInfo ci) {
        if (state instanceof GuiRenderStateMixinAccess access) {
            access.setGUITween$GuiGraphics((DrawContext) ((Object) this));
        }
    }

    @ModifyVariable(
            method = "drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;IIIZ)V",
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
            method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;III)V",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/lang/String;Lorg/joml/Matrix3x2f;Lnet/minecraft/client/render/item/KeyedItemRenderState;IILnet/minecraft/client/gui/ScreenRect;)Lnet/minecraft/client/gui/render/state/ItemGuiElementRenderState;")
    )
    private ItemGuiElementRenderState redirectNewGuiItemRenderState(String name, Matrix3x2f pose, KeyedItemRenderState state, int x, int y, ScreenRect scissor) {
        ItemGuiElementRenderState renderState = new ItemGuiElementRenderState(name, pose, state, x, y, scissor);

        if (GUITweenUtility.hasItemAlpha()) {
            if ((Object) renderState instanceof GuiItemRenderStateMixinAccess access) {
                int alpha = (int) (GUITweenUtility.peekItemAlpha() * 255);
                access.setGUITween$alpha(alpha);
            }
        }

        return renderState;
    }

    @ModifyVariable(
            method = "drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIIII)V",
            at = @At(
                    value = "HEAD"
            ),
            index = 7,
            argsOnly = true
    )
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
            method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;Lnet/minecraft/util/Identifier;Z)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/gui/DrawContext;tooltipDrawer:Ljava/lang/Runnable;",
                    shift = At.Shift.AFTER
            )
    )
    private void setTooltipForNextFrameInternalAfter(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, @Nullable Identifier texture, boolean focused, CallbackInfo ci) {
        if (!GUITweenUtility.inTooltipTween)
            return;

        Runnable curDeferredTooltip = tooltipDrawer;

        float duration = GUITweenClient.CONFIG.tooltipDuration;
        float progress = GUITweenUtility.tooltipTweenTick / duration;
        float alpha = TweenUtil.tween(GUITweenUtility.fFontMinAlpha, 1, progress, GUITweenClient.CONFIG.tooltipEase.get());

        tooltipDrawer = () -> {
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
