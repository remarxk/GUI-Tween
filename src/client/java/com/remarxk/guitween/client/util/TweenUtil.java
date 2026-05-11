package com.remarxk.guitween.client.util;

import net.minecraft.util.math.MathHelper;

public class TweenUtil {
    /**
     * 核心方法
     * @param start 起始值
     * @param end 结束值
     * @param elapsed 已经经过的时间
     * @param duration 总持续时间
     * @param easeType 缓动类型
     * @return 当前值
     */
    public static float tween(float start, float end, float elapsed, float duration, Ease easeType) {
        if(duration <= 0f) return end;
        return tween(start, end, (elapsed / duration), easeType);
    }

    /**
     * 核心方法
     * @param start 起始值
     * @param end 结束值
     * @param progress 进度(0 - 1)
     * @param easeType 缓动类型
     * @return 当前值
     */
    public static float tween(float start, float end, float progress, Ease easeType) {
        float t = Math.clamp(progress, 0f, 1f);
        float delta = end - start;
        float eased = applyEase(t, easeType); // 可以大于1或小于0
        return start + delta * eased; // 起点 + 增量
    }

    /**
     * 应用缓动曲线
     */
    public static float applyEase(float t, Ease type) {
        switch(type) {
            case LINEAR: return t;

            // Quadratic
            case IN_QUAD: return t*t;
            case OUT_QUAD: return t*(2-t);
            case IN_OUT_QUAD: return t<0.5f ? 2*t*t : -1 + (4-2*t)*t;

            // Cubic
            case IN_CUBIC: return t*t*t;
            case OUT_CUBIC: t-=1; return t*t*t+1;
            case IN_OUT_CUBIC: return t<0.5f ? 4*t*t*t : (t-1)*(2*t-2)*(2*t-2)+1;

            // Quart
            case IN_QUART: return t*t*t*t;
            case OUT_QUART: t-=1; return 1 - t*t*t*t;
            case IN_OUT_QUART: return t<0.5f ? 8*t*t*t*t : 1 - 8*(t-1)*(t-1)*(t-1)*(t-1);

            // Quint
            case IN_QUINT: return t*t*t*t*t;
            case OUT_QUINT: t-=1; return 1 + t*t*t*t*t;
            case IN_OUT_QUINT: return t<0.5f ? 16*t*t*t*t*t : 1 + 16*(t-1)*(t-1)*(t-1)*(t-1)*(t-1);

            // Sine
            case IN_SINE: return (float)(1 - Math.cos((t*Math.PI)/2));
            case OUT_SINE: return (float)Math.sin((t*Math.PI)/2);
            case IN_OUT_SINE: return (float)(-(Math.cos(Math.PI*t)-1)/2);

            // Exponential
            case IN_EXPO: return (float)(t==0?0:Math.pow(2,10*(t-1)));
            case OUT_EXPO: return (float)(t==1?1:1-Math.pow(2,-10*t));
            case IN_OUT_EXPO:
                if(t==0) return 0;
                if(t==1) return 1;
                if(t<0.5f) return (float)(Math.pow(2,20*t-10)/2);
                return (float)((2-Math.pow(2,-20*t+10))/2);

            // Circular
            case IN_CIRC: return (float)(1 - Math.sqrt(1-t*t));
            case OUT_CIRC: t-=1; return (float)Math.sqrt(1 - t*t);
            case IN_OUT_CIRC:
                t*=2;
                if(t<1) return (float)(-(Math.sqrt(1-t*t)-1)/2);
                t-=2;
                return (float)((Math.sqrt(1-t*t)+1)/2);

            // Back
            case IN_BACK: return t*t*((2.70158f)*t-1.70158f);
            case OUT_BACK: t-=1; return t*t*((2.70158f)*t+1.70158f)+1;
            case IN_OUT_BACK:
                t*=2;
                if(t<1) return 0.5f*t*t*((2.70158f)*t-1.70158f);
                t-=2; return 0.5f*(t*t*((2.70158f)*t+1.70158f)+2);

            // Elastic
            case IN_ELASTIC: return (float)(Math.sin(13*Math.PI/2*t)*Math.pow(2,10*(t-1)));
            case OUT_ELASTIC: return (float)(Math.sin(-13*Math.PI/2*(t+1))*Math.pow(2,-10*t)+1);
            case IN_OUT_ELASTIC:
                if(t<0.5f) return 0.5f*(float)(Math.sin(13*Math.PI*t)*Math.pow(2,20*t-10));
                return 0.5f*(float)(Math.sin(-13*Math.PI*(2*t-1+1))*Math.pow(2,-20*t+10)+2);

            // Bounce
            case OUT_BOUNCE: return bounceOut(t);
            case IN_BOUNCE: return 1 - bounceOut(1-t);
            case IN_OUT_BOUNCE:
                if(t<0.5f) return 0.5f*(1-bounceOut(1-2*t));
                return 0.5f*(bounceOut(2*t-1)+1);
            default: return t;
        }
    }

    private static float bounceOut(float t) {
        if(t < 1/2.75f) return 7.5625f*t*t;
        else if(t < 2/2.75f) { t -= 1.5f/2.75f; return 7.5625f*t*t + 0.75f; }
        else if(t < 2.5/2.75f) { t -= 2.25f/2.75f; return 7.5625f*t*t + 0.9375f; }
        else { t -= 2.625f/2.75f; return 7.5625f*t*t + 0.984375f; }
    }


    public static float punch(float strength, int vibrato, float progress) {
        if (progress >= 1f) return 1f;

        float decay = 1f - progress;

        float oscillation =
                (float) Math.sin(progress * vibrato * Math.PI * 2);

        return 1f + oscillation * strength * decay;
    }

    public static final long DEFAULT_SEED = 1337L;

    public static float shake(int axis, float time, float duration, float strength) {
        return shake(axis, time, duration, strength, 10f, DEFAULT_SEED);
    }

    public static float shake(int axis, float time, float duration, float strength, float frequency, long seed) {
        if (time <= 0 || time >= duration) return 0f;

        float ft = time * frequency;
        int frame = (int)Math.floor(ft);
        float frac = ft - frame;

        float n1 = noise(seed, axis, frame);
        float n2 = noise(seed, axis, frame + 1);

        // 线性插值
        float n = n1 + (n2 - n1) * frac;

        float t = time / duration;
        float decay = 1f - t;

        return n * strength * decay;
    }

    private static float noise(long seed, int axis, int frame) {
        long h = seed;
        h ^= axis * 0x632BE5ABL;
        h ^= frame * 0x9E3779B97F4A7C15L;

        h ^= (h >> 33);
        h *= 0xff51afd7ed558ccdL;
        h ^= (h >> 33);
        h *= 0xc4ceb9fe1a85ec53L;
        h ^= (h >> 33);

        // 映射到 [-1, 1]
        return ((h & 0xFFFFFF) / (float)0x7FFFFF) - 1f;
    }

    public static int blinkWhiteRedstonePingPong(int color, float time, float duration) {
        float t = (time % duration) / duration;   // 0~1
        t = t < 0.5f ? t * 2f : 2f - t * 2f;       // PingPong

        int rgb = color & 0x00FFFFFF;
        int targetR = (rgb >> 16) & 0xFF;
        int targetG = (rgb >> 8)  & 0xFF;
        int targetB = rgb & 0xFF;

        int r = (int) MathHelper.lerp(t, 255, targetR);
        int g = (int) MathHelper.lerp(t, 255, targetG);
        int b = (int) MathHelper.lerp(t, 255, targetB);

        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }
}