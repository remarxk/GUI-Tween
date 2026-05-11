package com.remarxk.guitween.client.mixin;

import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.anim.AttackTween;
import com.remarxk.guitween.client.anim.UseTween;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftMixin {
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
        usingTween.use(player.getInventory().selectedSlot);
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
        attackTween.resetProgress(player.getInventory().selectedSlot);
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
        attackTween.resetProgress(player.getInventory().selectedSlot);
    }
}
