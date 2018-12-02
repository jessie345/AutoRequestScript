package com.liushuo.auto.util;

public class ComicUtil {
    public static void simulateManualDelay() {
        try {
            Thread.sleep(5 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
