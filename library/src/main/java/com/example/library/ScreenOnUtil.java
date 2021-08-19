package com.example.library;

import android.content.Context;
import android.os.Build;
import android.os.PowerManager;

public class ScreenOnUtil {
    public static boolean isScreenOn(Context context) {
        PowerManager pm= (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
            return pm.isScreenOn();
        }else {
            return pm.isInteractive();
        }
    }
}
