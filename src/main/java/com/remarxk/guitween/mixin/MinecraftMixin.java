package com.remarxk.guitween.mixin;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.HotbarChangeListener;
import com.remarxk.guitween.anim.AttackTween;
import com.remarxk.guitween.anim.UseTween;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
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
