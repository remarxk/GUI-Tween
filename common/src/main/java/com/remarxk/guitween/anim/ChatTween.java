package com.remarxk.guitween.anim;

import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.util.Mth;

public class ChatTween {
    public interface AlphaCalculator {
        AlphaCalculator FULLY_VISIBLE = (p_458184_) -> 1.0F;

        static AlphaCalculator timeBased(int tickCount) {
            return (line) -> {
                int i = tickCount - line.addedTime();
                double d0 = (double)i / (double)200.0F;
                d0 = (double)1.0F - d0;
                d0 *= (double)10.0F;
                d0 = Mth.clamp(d0, (double)0.0F, (double)1.0F);
                d0 *= d0;
                return (float)d0;
            };
        }

        float calculate(GuiMessage.Line var1);
    }

    public interface LineConsumer {
        void accept(GuiMessage.Line line, int index, float alpha);
    }

    public static class Result {
        public boolean inTween;
        public float dx;
        public float dy;
        public float alpha;
    }
}
