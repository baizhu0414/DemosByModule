package com.example.modulestepcounter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.library.DateUtil;

public class CounterActivity extends AppCompatActivity {

    private static final String TAG = "CounterActivity";
    TextView mStepTV;
    private SensorManager mSensorManager;
    private MySensorEventListener mListener;
    private int mStepDetector = 0;  // 自应用运行以来STEP_DETECTOR检测到的步数
    private int mStepCounter = 0;   // 自系统开机以来STEP_COUNTER检测到的步数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        initView(this);
        initParam(this);
        requestPermission();
        initListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mListener, mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mListener, mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mListener);
    }

    class MySensorEventListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.w(TAG,"@@@:" + event.sensor.getType() + "--" + Sensor.TYPE_STEP_DETECTOR + "--" + Sensor.TYPE_STEP_COUNTER);
            if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                if (event.values[0] == 1.0f) {
                    mStepDetector++;
                }
            } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                mStepCounter = (int) event.values[0];
            }

            String desc = String.format("设备检测到您当前走了%d步，自开机以来总数为%d步", mStepDetector, mStepCounter);
            mStepTV.setText(desc);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.w(TAG, "@@@:" + "onAccuracyChanged...");
        }
    }

    /**
     * 1.加载布局
     * @param context context
     */
    protected void initView(Context context) {
        mStepTV = findViewById(R.id.tv_hello);
    }

    /**
     * 2.初始化成员变量
     * @param context Context
     */
    protected void initParam(Context context) {
        // 设置
        mStepTV.setText("当前日期为：" + DateUtil.getCurrentDateFormatOne());
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mListener = new MySensorEventListener();
    }

    protected void requestPermission() {
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
            } else {
                Toast toast = Toast.makeText(this, "已授权", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    /**
     * 初始化监听器
     * @param context context
     */
    protected void initListener(Context context) {
        //
    }

}