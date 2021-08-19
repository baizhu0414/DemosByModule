package com.example.modulekeepalive;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.example.library.LogUtil;
import com.example.library.ScreenOnUtil;
import com.example.modulekeepalive.utils.OnePixelLive;
import com.example.modulestepcounter.constant.Constant;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OnePixelActivity extends AppCompatActivity {

    private static final String TAG = OnePixelActivity.class.getSimpleName();
    TimerTask timerTask;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "OnePixelActivity保活onCreate");
        Window window = getWindow();
        //放在左上角
        window.setGravity(Gravity.START | Gravity.TOP);
        WindowManager.LayoutParams attributes = window.getAttributes();
        //宽高设计为1个像素
        attributes.width = 1;
        attributes.height = 1;
        //起始坐标
        attributes.x = 0;
        attributes.y = 0;
        window.setAttributes(attributes);
        boolean screenon = ScreenOnUtil.isScreenOn(this);
        if (screenon) {
            stopTimer();
            finish();
        } else {
            initTimeTask();
            OnePixelLive.setActivity(this);
        }
    }

    public void initTimeTask() {
        timerTask = new TimerTask() {
            public void run() {
                if (isServiceWork(getApplicationContext(),
                        "com.example.modulekeepalive.StepService_Alive")) {
                    LogUtil.w(TAG, "计步服务Alive...");
                } else {
                    LogUtil.w(TAG, "计步服务挂了，重新启动...");
                    setupService();
                }
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, Constant.MSG_ALIVE_SEND_DURATION);
    }

    public void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    /**
     * 开启计步服务
     */
    private void setupService() {
        Intent intent = new Intent(this, StepService_Alive.class);
        startService(intent);
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    public static void startMe(Context context) {
        Intent intentNew = new Intent(context, OnePixelActivity.class);
        intentNew.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentNew);
    }

    public static void stopMe() {
        OnePixelActivity protectActivity = (OnePixelActivity) OnePixelLive.getActivity();
        if (protectActivity != null) {
            protectActivity.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "OnePixelActivity保活onDestroy");
    }
}