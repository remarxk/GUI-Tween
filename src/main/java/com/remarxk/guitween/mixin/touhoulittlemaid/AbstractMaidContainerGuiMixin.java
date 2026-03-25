package com.remarxk.guitween.mixin.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.AbstractMaidContainerGui;
import com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button.TouhouImageButton;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.AbstractMaidContainer;
import com.remarxk.guitween.mixinAccess.AbstractContainerScreenMixinAccess;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMaidContainerGui.class)
public abstract class AbstractMaidContainerGuiMixin<T extends AbstractMaidContainer> extends AbstractContainerScreen<T> {
    public AbstractMaidContainerGuiMixin(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Unique
    private float gUITween$lastOpenTick;

    @Inject(
            method = "taskPageDown",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/github/tartaricacid/touhoulittlemaid/client/gui/entity/maid/AbstractMaidContainerGui;init()V"
            )
    )
    private void taskPageDownBefore(CallbackInfo ci) {
        if ((Object)this instanceof AbstractContainerScreenMixinAccess access) {
            gUITween$lastOpenTick = access.getGUITween$openTick();
        }
    }

    @Inject(
            method = "taskPageDown",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/github/tartaricacid/touhoulittlemaid/client/gui/entity/maid/AbstractMaidContainerGui;init()V",
                    shift = At.Shift.AFTER
            )
    )
    private void taskPageDownAfter(CallbackInfo ci) {
        if ((Object)this instanceof AbstractContainerScreenMixinAccess access) {
            access.setGUITween$openTick(gUITween$lastOpenTick);
        }
    }

    @Inject(
            method = "taskPageUp",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/github/tartaricacid/touhoulittlemaid/client/gui/entity/maid/AbstractMaidContainerGui;init()V"
            )
    )
    private void taskPageUpBefore(CallbackInfo ci) {
        if ((Object)this instanceof AbstractContainerScreenMixinAccess access) {
            gUITween$lastOpenTick = access.getGUITween$openTick();
        }
    }

    @Inject(
            method = "taskPageUp",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/github/tartaricacid/touhoulittlemaid/client/gui/entity/maid/AbstractMaidContainerGui;init()V",
                    shift = At.Shift.AFTER
            )
    )
    private void taskPageUpAfter(CallbackInfo ci) {
        if ((Object)this instanceof AbstractContainerScreenMixinAccess access) {
            access.setGUITween$openTick(gUITween$lastOpenTick);
        }
    }

    @Unique
    private TouhouImageButton gUITween$warpNewTouhouImageButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex, ResourceLocation pResourceLocation, Button.OnPress pOnPress) {
        Button.OnPress newOnPress = pOnPress;

        if ((Object)this instanceof AbstractContainerScreenMixinAccess access) {
            newOnPress = button -> {
                gUITween$lastOpenTick = access.getGUITween$openTick();
                pOnPress.onPress(button);
                access.setGUITween$openTick(gUITween$lastOpenTick);
            };
        }

        return new TouhouImageButton(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffTex, pResourceLocation, newOnPress);
    }

    @Redirect(
            method = "addTaskControlButton",
            at = @At(
                    value = "NEW",
                    target = "(IIIIIIILnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/gui/components/Button$OnPress;)Lcom/github/tartaricacid/touhoulittlemaid/client/gui/widget/button/TouhouImageButton;",
                    ordinal = 2
            )
    )
    private TouhouImageButton redirectAddTaskControlButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex, ResourceLocation pResourceLocation, Button.OnPress pOnPress) {
        return gUITween$warpNewTouhouImageButton(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffTex, pResourceLocation, pOnPress);
    }

    @Redirect(
            method = "addTaskSwitchButton",
            at = @At(
                    value = "NEW",
                    target = "(IIIIIIILnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/gui/components/Button$OnPress;)Lcom/github/tartaricacid/touhoulittlemaid/client/gui/widget/button/TouhouImageButton;"
            )
    )
    private TouhouImageButton redirectAddTaskSwitchButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex, ResourceLocation pResourceLocation, Button.OnPress pOnPress) {
        return gUITween$warpNewTouhouImageButton(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffTex, pResourceLocation, pOnPress);
    }
}
