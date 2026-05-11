package com.remarxk.guitween.client.mixin;

import com.mojang.authlib.GameProfile;
import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.eventListener.HotbarChangeListener;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class LocalPlayerMixin extends AbstractClientPlayerEntity {
    public LocalPlayerMixin(ClientWorld clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(
            method = "dropSelectedItem",
            at = @At(
                    value = "TAIL"
            )
    )
    public void onDropAfter(boolean fullStack, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            if (GUITweenClient.CONFIG.isEnableExp())
                HotbarChangeListener.lackTick = 0;
        }
    }
}
