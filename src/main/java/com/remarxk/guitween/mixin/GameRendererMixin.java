package com.remarxk.guitween.mixin;

import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * 容器界面播放关闭动画时，鼠标会被强行抓到屏幕中央，
 * 若直接使用真实坐标会让界面认为有槽位被悬停，从而弹出 tooltip。
 * 这里把传给界面的鼠标坐标挪到屏幕外，保证关闭过程中不再出现 tooltip。
 * <p>
 * 1.21.1 的 NeoForge 把界面渲染从 {@code Gui} 移到了
 * {@code GameRenderer#render} -> {@code ClientHooks.drawScreen}。
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @ModifyArgs(
            method = "render(Lnet/minecraft/client/DeltaTracker;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/client/ClientHooks;drawScreen(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/gui/GuiGraphics;IIF)V"
            )
    )
    private void gUITween$modifyScreenMousePos(Args args) {
        var screen = args.get(0);
        if (screen instanceof AbstractContainerScreenMixinAccess access) {
            if (access.gUITween$inCloseTween()) {
                args.set(2, -9999);
                args.set(3, -9999);
            }
        }
    }
}
