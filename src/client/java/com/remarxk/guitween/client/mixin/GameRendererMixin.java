package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.client.mixinAccess.AbstractContainerScreenMixinAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * 容器界面播放关闭动画时，鼠标会被强行抓到屏幕中央，
 * 若直接使用真实坐标会让界面认为有槽位被悬停，从而弹出 tooltip。
 * 这里把传给界面的鼠标坐标挪到屏幕外，保证关闭过程中不再出现 tooltip。
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @ModifyArgs(
            method = "render(Lnet/minecraft/client/render/RenderTickCounter;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;renderWithTooltip(Lnet/minecraft/client/gui/DrawContext;IIF)V"
            )
    )
    private void gUITween$modifyScreenMousePos(Args args) {
        var screen = MinecraftClient.getInstance().currentScreen;
        if (screen instanceof AbstractContainerScreenMixinAccess access && access.gUITween$inCloseTween()) {
            args.set(1, -9999);
            args.set(2, -9999);
        }
    }
}
