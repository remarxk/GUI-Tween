package com.remarxk.guitween.client.anim;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.client.GUITweenClient;
import com.remarxk.guitween.client.GUITweenUtility;
import com.remarxk.guitween.client.util.TweenUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;

public class UseTween {
    public final static HashMap<Class<?>, UseAnimation> animMap = new HashMap<>();

    private UseAnimation animation;
    public int slot;
    private float tick;
    private boolean isUse;
    private boolean nextUse;

    static {
        animMap.put(LoopScaleAnim.class, new LoopScaleAnim());
        animMap.put(ContinuesScaleAnim.class, new ContinuesScaleAnim());
    }

    public boolean isRunning() {
        return isUse || nextUse;
    }

    public void stop() {
        tick = 0;
    }

    private Class<?> getAnimType(ItemStack itemStack) {
        if (itemStack.getItem() instanceof BlockItem)
            return LoopScaleAnim.class;

        if (itemStack.get(DataComponentTypes.FOOD) != null)
            return LoopScaleAnim.class;

        return ContinuesScaleAnim.class;
    }

    public void use(int slot) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null)
            return;

        ItemStack itemStack;

        if (slot < 9) {
            itemStack = player.getInventory().getStack(slot);
        }
        else {
            itemStack = player.getOffHandStack();
        }

        use(slot, getAnimType(itemStack));
    }

    public void use(int slot, Class<?> animType) {
        animation = animMap.get(animType);
        if (animation == null) {
            animation = animMap.get(ContinuesScaleAnim.class);
        }
        animation.setTween(this);

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

    public float getScale() {
        return animation.getScale();
    }

    public void update() {
        animation.update();
    }

    public abstract static class UseAnimation {
        protected UseTween tween;

        public void setTween(UseTween tween) {
            this.tween = tween;
        }

        public abstract void update();

        public abstract float getScale();
    }

    public static class LoopScaleAnim extends UseAnimation {

        @Override
        public void update() {
            if (tween.tick >= 4f) {
                if (tween.nextUse) {
                    tween.tick -= 4f;

                    tween.nextUse = false;
                    tween.isUse = true;
                }
                else {
                    tween.tick = 0f;
                    tween.isUse = false;
                }
            }
            else {
                if (tween.isUse) {
                    tween.tick += GUITweenUtility.getDeltaTicks();
                }
                else {
                    tween.tick -= GUITweenUtility.getDeltaTicks();
                }
            }
        }

        @Override
        public float getScale() {
            return TweenUtil.punch(GUITweenClient.CONFIG.useStrength, 1, tween.tick / 4f);
        }
    }

    public static class ContinuesScaleAnim extends UseAnimation {
        private final static float speed = (MathHelper.PI * 2) / 4f;

        private float getDeltaTicks() {
            return GUITweenUtility.getDeltaTicks() * speed;
        }

        @Override
        public void update() {
            if (tween.tick >= MathHelper.PI * 2) {
                tween.isUse = false;
                tween.tick = 0;
            }

            if (tween.isUse) {
                if (MinecraftClient.getInstance().player.isUsingItem()) {
                    if (tween.tick <= MathHelper.PI / 2) {
                        tween.tick += getDeltaTicks();
                        tween.tick = Math.min(tween.tick, MathHelper.PI / 2 - 0.01f);
                    }
                    else if (tween.tick <= 3 * MathHelper.PI / 2) {
                        tween.tick -= getDeltaTicks();
                        tween.tick = Math.max(tween.tick, MathHelper.PI / 2 - 0.01f);
                    }
                    else {
                        tween.tick += getDeltaTicks();
                    }
                }
                else {
                    tween.tick += getDeltaTicks();
                }
            }
            else if (tween.tick > 0) {
                tween.tick += getDeltaTicks();
            }
        }

        @Override
        public float getScale() {
            return 1 + MathHelper.sin(tween.tick) * GUITweenClient.CONFIG.useStrength;
        }
    }
}
