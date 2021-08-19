package com.example.modulekeepalive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.library.LogUtil;
import com.example.modulekeepalive.utils.OnePixelLive;

public class ScreenOnReceiver extends BroadcastReceiver {
    private static final String TAG = ScreenOnReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            LogUtil.d(TAG, "onReceive锁屏");
            Intent intentNew = new Intent(context, OnePixelActivity.class);
            intentNew.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intentNew);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            LogUtil.d(TAG, "onReceive解锁");
            OnePixelActivity protectActivity = (OnePixelActivity) OnePixelLive.getActivity();
            if (protectActivity != null) {
                protectActivity.finish();
            }
        }
    }
}
