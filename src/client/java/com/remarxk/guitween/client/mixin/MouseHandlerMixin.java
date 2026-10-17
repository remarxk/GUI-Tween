package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.client.mixinAccess.AbstractContainerScreenMixinAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 与 {@link KeyboardHandlerMixin} 配套：
 * 容器界面播放关闭动画时，屏幕尚未真正关闭，但应让鼠标像在游戏中一样工作，
 * 即抓取鼠标并转动视角，而不是把鼠标移动/点击交给仍处于关闭动画中的界面。
 */
@Mixin(Mouse.class)
public class MouseHandlerMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private boolean cursorLocked;

    @Shadow
    public void lockCursor() {
    }

    @Unique
    private Screen gUITween$screen;

    @Unique
    private void gUITween$switchToGameMouse() {
        Screen screen = client.currentScreen;
        if (!(screen instanceof AbstractContainerScreenMixinAccess access)) {
            return;
        }

        if (!access.gUITween$inCloseTween()) {
            return;
        }

        // 只抓取鼠标、隐藏光标，但不调用 setScreen(null)，界面仍然需要被渲染
        if (client.isWindowFocused() && !cursorLocked) {
            lockCursor();
        }

        // 处理期间临时当作“没有打开屏幕”，让 Mouse 走游戏输入逻辑（转动视角、攻击等）
        gUITween$screen = screen;
        client.currentScreen = null;
    }

    @Unique
    private void gUITween$restoreScreen() {
        if (gUITween$screen != null) {
            client.currentScreen = gUITween$screen;
            gUITween$screen = null;
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void modifyMouseTickBefore(CallbackInfo ci) {
        gUITween$switchToGameMouse();
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void modifyMouseTickAfter(CallbackInfo ci) {
        gUITween$restoreScreen();
    }

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void modifyMouseButtonBefore(long window, int button, int action, int mods, CallbackInfo ci) {
        gUITween$switchToGameMouse();
    }

    @Inject(method = "onMouseButton", at = @At("RETURN"))
    private void modifyMouseButtonAfter(long window, int button, int action, int mods, CallbackInfo ci) {
        gUITween$restoreScreen();
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"))
    private void modifyMouseScrollBefore(long window, double horizontal, double vertical, CallbackInfo ci) {
        gUITween$switchToGameMouse();
    }

    @Inject(method = "onMouseScroll", at = @At("RETURN"))
    private void modifyMouseScrollAfter(long window, double horizontal, double vertical, CallbackInfo ci) {
        gUITween$restoreScreen();
    }
}
