package com.remarxk.guitween.util;

import com.remarxk.guitween.Constants;

public class DebugUtil {
    // 获取并打印调用栈的方法
    public static void printCurrentStackTrace(String desc) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        Constants.LOGGER.info("{},模组调用栈", desc);
        for (StackTraceElement elem : stackTrace) {
            Constants.LOGGER.info("{}#{} (行号: {})",
                    elem.getClassName(), elem.getMethodName(), elem.getLineNumber());
        }
    }
}
