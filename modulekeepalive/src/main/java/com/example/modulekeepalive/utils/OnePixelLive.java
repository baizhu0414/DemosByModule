package com.example.modulekeepalive.utils;

import android.app.Activity;

import com.example.library.LogUtil;

import java.lang.ref.WeakReference;

public class OnePixelLive {
    private static final String TAG = OnePixelLive.class.getSimpleName();
    private static WeakReference<Activity> mPixelActivityRef;

    public static void setActivity(Activity activity) {
        mPixelActivityRef = new WeakReference<>(activity);
        LogUtil.d(TAG, "保存OnPixelActivity");
    }

    public static Activity getActivity() {
        if (mPixelActivityRef != null && mPixelActivityRef.get() != null) {
            LogUtil.d(TAG, "获取OnPixelActivity");
            return mPixelActivityRef.get();
        }
        LogUtil.d(TAG, "获取OnPixelActivity失败，未保存?");
        return null;
    }
}
