package com.remarxk.guitween.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.anim.AttackTween;
import com.remarxk.guitween.anim.UseTween;
import com.remarxk.guitween.event.PostScreenTickEvent;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    public Screen screen;

    @Unique
    private Screen gUITween$screen;

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void postScreenTick(CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new PostScreenTickEvent(screen));

        if (screen instanceof AbstractContainerScreenMixinAccess access) {
            if (access.gUITween$inCloseTween()) {
                gUITween$screen = screen;
                screen = null;
            }
        }
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "RETURN"
            )
    )
    private void restoreClosingScreen(CallbackInfo ci) {
        if (gUITween$screen != null) {
            screen = gUITween$screen;
            gUITween$screen = null;
        }
    }

    @Inject(
            method = "handleKeybinds",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;startUseItem()V"
            )
    )
    public void onStartUseItem(CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
            return;

        UseTween usingTween = GUITweenUtility.getUsingTween();
        usingTween.use(player.getInventory().selected);
    }

    @Inject(
            method = "handleKeybinds",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;startAttack()Z"
            )
    )
    public void onStartAttack(CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
            return;

        AttackTween attackTween = GUITweenUtility.getAttackTween();
        attackTween.resetProgress(player.getInventory().selected);
    }

    @Inject(
            method = "continueAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/BlockHitResult;getBlockPos()Lnet/minecraft/core/BlockPos;"
            )
    )
    public void onContinueAttack(CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
            return;

        AttackTween attackTween = GUITweenUtility.getAttackTween();
        attackTween.resetProgress(player.getInventory().selected);
    }
}
