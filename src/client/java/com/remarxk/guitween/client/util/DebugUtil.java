package com.remarxk.guitween.client.util;

import com.remarxk.guitween.GUITween;

public class DebugUtil {
    // 获取并打印调用栈的方法
    public static void printCurrentStackTrace(String desc) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        GUITween.LOGGER.info("{},模组调用栈", desc);
        for (StackTraceElement elem : stackTrace) {
            GUITween.LOGGER.info("{}#{} (行号: {})",
                    elem.getClassName(), elem.getMethodName(), elem.getLineNumber());
        }
    }
}
