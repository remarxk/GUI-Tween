package com.remarxk.guitween.mixin;

import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.event.PostScreenTickEvent;
import com.remarxk.guitween.eventListener.HotbarChangeListener;
import com.remarxk.guitween.anim.AttackTween;
import com.remarxk.guitween.anim.UseTween;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.MinecraftForge;
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
        MinecraftForge.EVENT_BUS.post(new PostScreenTickEvent(screen));

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
        UseTween usingTween = GUITweenUtility.getUsingTween();
        usingTween.use(HotbarChangeListener.lastSelected);
    }

    @Inject(
            method = "handleKeybinds",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;startAttack()Z"
            )
    )
    public void onStartAttack(CallbackInfo ci) {
        AttackTween attackTween = GUITweenUtility.getAttackTween();
        attackTween.resetProgress(HotbarChangeListener.lastSelected);
    }

    @Inject(
            method = "continueAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/BlockHitResult;getBlockPos()Lnet/minecraft/core/BlockPos;"
            )
    )
    public void onContinueAttack(CallbackInfo ci) {
        AttackTween attackTween = GUITweenUtility.getAttackTween();
        attackTween.resetProgress(HotbarChangeListener.lastSelected);
    }
}
