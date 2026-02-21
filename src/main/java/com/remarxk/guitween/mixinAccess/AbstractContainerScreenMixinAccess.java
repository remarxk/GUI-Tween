package com.remarxk.guitween.mixinAccess;

public interface AbstractContainerScreenMixinAccess {
    public String getGUITween$screenName();

    public boolean getGUITween$isDisableScreenTween();

    public boolean getGUITween$inTween();

    public void setGUITween$inTween(boolean inScale);

    public float getGUITween$openTick();

    public void setGUITween$openTick(float openTick);
}
