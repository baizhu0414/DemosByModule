package com.example.library;

import android.util.Log;

public class LogUtil {

    public static void e(String TAG, String msg) {
        Log.e(TAG, msg);
    }

    public static void w(String TAG, String msg) {
        Log.w(TAG, msg);
    }

    public static void d(String TAG, String msg) {
        Log.d(TAG, msg);
    }
}
