package com.remarxk.guitween;

public class GUITweenAPI {
    /**
     * This boolean is used to determine whether the screen is currently undergoing a transition animation.
     */
    protected static boolean IN_TRANSITION = false;

    /**
     * This float is used to determine the progress of the transition animation.
     * If the float is -1, the screen is not undergoing a transition animation.
     */
    protected static float TRANSITION_PROGRESS = -1f;

    /**
     * This boolean is used to determine whether the screen is currently closing.
     */
    protected static boolean IS_CLOSING = false;

    public static boolean DISABLE_TEMPORARILY = false;

    /**
     * This method is used to get the progress of the transition animation.
     * @return The progress of the transition animation.
     */
    public static float getTransitionProgress() {
        return TRANSITION_PROGRESS;
    }

    /**
     * This method is used to set the progress of the transition animation.
     * @param progress The progress of the transition animation.
     */
    public static void setTransitionProgress(float progress) {
        TRANSITION_PROGRESS = progress;
    }

    /**
     * This method is used to get whether the screen is currently closing.
     */
    public static boolean isClosing() {
        return IS_CLOSING;
    }

    /**
     * This method is used to set whether the screen is currently closing.
     * @param closing Whether the screen is currently closing.
     */
    public static void setClosing(boolean closing) {
        IS_CLOSING = closing;
    }

    public static void toggleTemporaryDisable() {
        DISABLE_TEMPORARILY = !DISABLE_TEMPORARILY;
    }

    /**
     * This method is used to determine whether the screen is currently undergoing a transition animation.
     * @return Whether the screen is currently undergoing a transition animation.
     */
    public static boolean isInTransition() {
        return IN_TRANSITION;
    }

    /**
     * This method is used to set whether the screen is currently undergoing a transition animation.
     * @param inTransition Whether the screen is currently undergoing a transition animation.
     */
    public static void setInTransition(boolean inTransition) {
        IN_TRANSITION = inTransition;

        if(!IN_TRANSITION) {
            TRANSITION_PROGRESS = -1f;
        }
    }
}