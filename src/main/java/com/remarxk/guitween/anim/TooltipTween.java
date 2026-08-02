package com.remarxk.guitween.anim;

import com.remarxk.guitween.GUITween;
import com.remarxk.guitween.GUITweenUtility;
import com.remarxk.guitween.config.GUITweenConfig;
import com.remarxk.guitween.util.TweenUtil;
import net.minecraft.util.Mth;

public class TooltipTween {
    private boolean initSize;

    public boolean inTween;

    private boolean leftSide;

    private boolean shrinking;

    private float x;
    private float y;
    private float width;
    private float height;

    public int targetX;
    public int targetY;
    public int targetWidth;
    public int targetHeight;

    private float renderWidth;

    public int lastZ;
    public int lastBgColor;
    public int lastBorderTop;
    public int lastBorderBottom;
    public int lastBorderCenter;

    public boolean isLeftSide() {
        return leftSide;
    }

    public float getRenderWidth() {
        return renderWidth;
    }

    public boolean isShrinking() {
        return shrinking;
    }

    public boolean isShrinkComplete() {
        return shrinking && isSame(width, 0) && isSame(height, 0);
    }

    public void startShrink(int mouseX, int mouseY) {
        shrinking = true;
        shrinkLastMouseX = mouseX;
        shrinkLastMouseY = mouseY;
        this.targetX = (int) x;
        this.targetY = (int) y;
        this.targetWidth = 0;
        this.targetHeight = 0;
    }

    public void updateShrinkPosition(int mouseX, int mouseY) {
        if (!shrinking) return;
        int dx = mouseX - shrinkLastMouseX;
        int dy = mouseY - shrinkLastMouseY;
        if (dx != 0 || dy != 0) {
            this.targetX += dx;
            this.targetY += dy;
            shrinkLastMouseX = mouseX;
            shrinkLastMouseY = mouseY;
        }
    }

    private int shrinkLastMouseX;
    private int shrinkLastMouseY;

    public void reset(boolean initSize) {
        this.initSize = initSize;
        this.shrinking = false;
    }

    public int getX() {
        return leftSide ? (int) (x - width) : (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public int getWidth() {
        return (int) width;
    }

    public int getHeight() {
        return (int) height;
    }

    public void updateSize(int targetX, int targetY, int targetWidth, int targetHeight, int mouseX) {
        boolean newLeftSide = mouseX > targetX;

        if (initSize) {
            initSize = false;
            width = 0;
            height = 0;
            x = targetX;
            y = targetY;
            leftSide = newLeftSide;
            renderWidth = targetWidth;
        } else if (newLeftSide != leftSide) {
            if (newLeftSide) {
                if (getX() + getWidth() < mouseX) {
                    x = x + width;
                    leftSide = true;
                    renderWidth = targetWidth;
                }
            } else {
                if (getX() > mouseX) {
                    x = x - width;
                    leftSide = false;
                    renderWidth = targetWidth;
                }
            }
        }

        this.targetX = leftSide ? targetX + targetWidth : targetX;
        this.targetY = targetY;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
    }

    public void updateTick() {
        if (isSame(x, targetX) && isSame(y, targetY) && isSame(width, targetWidth) && isSame(height, targetHeight) && isSame(renderWidth, this.targetWidth)) {
            return;
        }

        x = Mth.lerp(0.12f, x, targetX);
        y = Mth.lerp(0.12f, y, targetY);
        width = Mth.lerp(0.12f, width, targetWidth);
        height = Mth.lerp(0.12f, height, targetHeight);
        renderWidth = Mth.lerp(0.12f, renderWidth, this.targetWidth);
    }

    private boolean isSame(float start, int end) {
        return Mth.abs(start - end) < 0.1f;
    }
}
