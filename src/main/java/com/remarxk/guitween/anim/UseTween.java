package com.remarxk.guitween.anim;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class UseTween {

    public int slot;
    private float tick;
    private boolean isUse;
    private boolean nextUse;

    public boolean isRunning() {
        return isUse || nextUse;
    }

    public void stop() {
        tick = 0;
    }

    public float getScale() {
        return TweenUtil.punch(GUITweenConfig.hotbar.useStrength.get().floatValue(), 1, tick / 4f);
    }

    public void use(int slot) {
        this.slot = slot;

        if (isUse) {
            nextUse = true;
        }
        else {
            tick = 0;
            isUse = true;
            nextUse = false;
        }
    }

    public void update() {
        if (tick >= 4f) {
            tick = 0f;
            isUse = false;

            if (nextUse) {
                nextUse = false;
                isUse = true;
            }
        }
        else {
            if (isUse) {
                tick += GUITweenUtility.getDeltaTicks();
            }
            else {
                tick -= GUITweenUtility.getDeltaTicks();
            }
        }
    }
}
