package com.remarxk.guitween.mixin;

import com.mojang.authlib.GameProfile;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.eventListener.HotbarChangeListener;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin extends AbstractClientPlayer {
    public LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(
            method = "drop",
            at = @At(
                    value = "TAIL"
            )
    )
    public void onDropAfter(boolean fullStack, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            if (GUITweenConfig.isEnableExp())
                HotbarChangeListener.lackTick = 0;
        }
    }
}
