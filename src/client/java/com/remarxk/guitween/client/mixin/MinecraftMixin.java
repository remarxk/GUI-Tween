package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.anim.AttackTween;
import com.remarxk.guitween.client.anim.UseTween;
import com.remarxk.guitween.client.mixinAccess.AbstractContainerScreenMixinAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftMixin {
    @Shadow
    public Screen currentScreen;

    @Unique
    private Screen gUITween$closingScreen;

    /**
     * 关闭动画期间需要让鼠标走「游戏内」逻辑（抓取光标 + 转视角）。
     * 关键时序：{@code MinecraftClient.render()} 里是 先 tick() → 再 mouse.tick() → 再渲染，
     * 而 {@code Mouse.tick()} 一开始就会把 {@code client.currentScreen} 抓进局部变量，
     * 并且只有 {@code cursorLocked} 为 true 时才会调用 {@code updateMouse()}。
     * 因此屏幕必须在调用 {@code mouse.tick()} 之前就置空；只在按键回调用完就还原是不够的。
     */
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Mouse;tick()V",
                    shift = At.Shift.BEFORE
            )
    )
    private void gUITween$beforeMouseTick(boolean tick, CallbackInfo ci) {
        Screen screen = this.currentScreen;
        boolean closing = screen instanceof AbstractContainerScreenMixinAccess access && access.gUITween$inCloseTween();

        // 供 MouseMixin 判断是否要抓取光标（此时 currentScreen 即将被置空，不能再靠它判断）
        GUITweenUtility.isWindowClosing = closing;

        if (closing) {
            gUITween$closingScreen = screen;
            this.currentScreen = null;
        }
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Mouse;tick()V",
                    shift = At.Shift.AFTER
            )
    )
    private void gUITween$afterMouseTick(boolean tick, CallbackInfo ci) {
        if (gUITween$closingScreen != null) {
            this.currentScreen = gUITween$closingScreen;
            gUITween$closingScreen = null;
        }
    }

    @Inject(
            method = "doItemUse",
            at = @At(
                    value = "HEAD"
            )
    )
    public void onStartUseItem(CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null)
            return;

        UseTween usingTween = GUITweenUtility.getUsingTween();
        usingTween.use(player.getInventory().getSelectedSlot());
    }

    @Inject(
            method = "doAttack",
            at = @At(
                    value = "HEAD"
            )
    )
    public void onStartAttack(CallbackInfoReturnable<Boolean> cir) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null)
            return;

        AttackTween attackTween = GUITweenUtility.getAttackTween();
        attackTween.resetProgress(player.getInventory().getSelectedSlot());
    }

    @Inject(
            method = "handleBlockBreaking",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/hit/BlockHitResult;getBlockPos()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    public void onContinueAttack(CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null)
            return;

        AttackTween attackTween = GUITweenUtility.getAttackTween();
        attackTween.resetProgress(player.getInventory().getSelectedSlot());
    }
}
