package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.mixinAccess.AbstractContainerScreenMixinAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 关闭动画期间让鼠标像在游戏中一样工作。
 *
 * <p>原版 {@code Mouse.tick()} 的顺序：
 * <pre>
 *   30:  Screen screen = client.currentScreen;   // 一开始就抓取
 *  296:  if (isCursorLocked()) ...
 *  314:      updateMouse(delta)                   // 转视角只在这条分支里
 * </pre>
 * 所以必须在 {@code tick()} 开始前就把 {@code cursorLocked} 置为 true，
 * 否则 {@code updateMouse()} 根本不会被调用。屏幕本身由
 * {@link MinecraftMixin} 在 {@code MinecraftClient.render()} 调用 {@code mouse.tick()}
 * 之前置空，因此这里用 {@link GUITweenUtility#isWindowClosing} 判断。
 */
@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private boolean cursorLocked;

    @Shadow
    private double x;

    @Shadow
    private double y;

    @Unique
    private Screen gUITween$screen;

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void gUITween$grabCursorWhileClosing(CallbackInfo ci) {
        if (!GUITweenUtility.isWindowClosing) {
            return;
        }

        if (client.isWindowFocused() && !cursorLocked) {
            cursorLocked = true;
            x = client.getWindow().getWidth() / 2.0;
            y = client.getWindow().getHeight() / 2.0;
            InputUtil.setCursorParameters(client.getWindow(), 212995, x, y);
        }
    }

    @Unique
    private void gUITween$switchToGameMouse() {
        Screen screen = client.currentScreen;
        if (!(screen instanceof AbstractContainerScreenMixinAccess access)) {
            return;
        }

        if (!access.gUITween$inCloseTween()) {
            return;
        }

        // 处理期间临时当作“没有打开屏幕”，让点击/滚轮走游戏输入逻辑
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

    @Inject(
            method = "onMouseButton",
            at = @At("HEAD")
    )
    private void modifyMouseButtonBefore(long window, MouseInput input, int action, CallbackInfo ci) {
        gUITween$switchToGameMouse();
    }

    @Inject(
            method = "onMouseButton",
            at = @At("RETURN")
    )
    private void modifyMouseButtonAfter(long window, MouseInput input, int action, CallbackInfo ci) {
        gUITween$restoreScreen();
    }

    @Inject(
            method = "onMouseScroll",
            at = @At("HEAD")
    )
    private void modifyMouseScrollBefore(long window, double horizontal, double vertical, CallbackInfo ci) {
        gUITween$switchToGameMouse();
    }

    @Inject(
            method = "onMouseScroll",
            at = @At("RETURN")
    )
    private void modifyMouseScrollAfter(long window, double horizontal, double vertical, CallbackInfo ci) {
        gUITween$restoreScreen();
    }
}
