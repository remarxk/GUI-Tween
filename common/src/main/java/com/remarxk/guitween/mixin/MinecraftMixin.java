package com.remarxk.guitween.mixin;

import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.anim.AttackTween;
import com.remarxk.guitween.anim.UseTween;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(
            method = "startUseItem",
            at = @At(
                    value = "HEAD"
            )
    )
    public void onStartUseItem(CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
            return;

        UseTween usingTween = GUITweenUtility.getUsingTween();
        usingTween.use(player.getInventory().getSelectedSlot());
    }

    @Inject(
            method = "startAttack",
            at = @At(
                    value = "HEAD"
            )
    )
    public void onStartAttack(CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
            return;

        AttackTween attackTween = GUITweenUtility.getAttackTween();
        attackTween.resetProgress(player.getInventory().getSelectedSlot());
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
        attackTween.resetProgress(player.getInventory().getSelectedSlot());
    }
}
