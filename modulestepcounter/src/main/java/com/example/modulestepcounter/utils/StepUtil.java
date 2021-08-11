package com.example.modulestepcounter.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

/**
 * 用于获取步数信息：需要持续获取步数才可以。
 */
public class StepUtil {

    private static final String TAG = "StepUtil";
    private static StepUtil mStepUtilInstance;
    private final SensorManager mSensorManager;
    private final SensorEventListener mStepListener;
    public static int mStepDetector = 0;
    public static int mStepCounter = 0;
    private OnStepSensorChangeListener mListenerCallback;

    /**
     * 1.创建sensor
     *
     * @param context 获取系统服务
     * @return StepUtil单例对象
     */
    public static StepUtil getInstance(Context context) {
        if (mStepUtilInstance != null) {
            return mStepUtilInstance;
        } else {
            mStepUtilInstance = new StepUtil(context);
            return mStepUtilInstance;
        }
    }

    private StepUtil(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mStepListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Log.w(TAG, "onSensorChanged:" + event.sensor.getType() + "--" + Sensor.TYPE_STEP_DETECTOR + "--" + Sensor.TYPE_STEP_COUNTER);
                if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                    if (event.values[0] == 1.0f) {
                        mStepDetector++;
                        Log.w(TAG, "step:" + mStepDetector);

                    }
                } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                    mStepCounter = (int) event.values[0];
                    Log.w(TAG, "stepTotal:" + mStepCounter);
                }
                mListenerCallback.onSensorStepChanged(mStepDetector, mStepCounter);
//            String desc = String.format("设备检测到您当前走了%d步，自开机以来总数为%d步", mStepDetector, mStepCounter);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Log.w(TAG, "@@@:" + "onAccuracyChanged...");
            }
        };
    }

    /**
     * 2.start
     */
    public void startStepCount(OnStepSensorChangeListener mListenerCallback) {
        // 顺序不要改动，此处用到listenerCallback回调。
        this.mListenerCallback = mListenerCallback;
        mSensorManager.registerListener(mStepListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mStepListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * 3.stop
     */
    public void stopStepCount() {
        mSensorManager.unregisterListener(mStepListener);
    }

    /**
     * 是否支持计步
     * @param context 上下文，获取PackageManager
     * @return true支持
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isSupportStepCountSensor(Context context) {
        return context.getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
    }

    /**
     * 想要获取步数回调，实现此接口
     */
    public interface OnStepSensorChangeListener {
        void onSensorStepChanged(int step, int stepTotal);

        void onSensorStepDesc(String desc);
    }
}
