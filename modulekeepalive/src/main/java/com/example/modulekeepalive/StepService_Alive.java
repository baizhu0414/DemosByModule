package com.example.modulekeepalive;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.library.DateUtil;
import com.example.library.LogUtil;
import com.example.modulekeepalive.utils.OnePixelLive;
import com.example.modulestepcounter.constant.Constant;
import com.example.modulestepcounter.utils.StepDataIterator;
import com.example.modulestepcounter.utils.StepEntity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StepService_Alive extends Service implements SensorEventListener {

    private static final String TAG = StepService_Alive.class.getSimpleName();
    //当前日期
    private static String mCurrentDate;
    //当前步数
    private int mCurrentStep;
    //3秒进行一次存储
    private static int saveDuration = 3000;
    //传感器
    private SensorManager sensorManager;
    //数据库
    private StepDataIterator stepDataIterator;
    //计步传感器类型 0-counter 1-detector
    private static int stepSensor = -1;
    //广播接收
    private BroadcastReceiver mInfoReceiver;
    //自定义简易计时器
    private TimeCount timeCount;
    //发送消息，用来和Service之间传递步数
    private Messenger messenger = new Messenger(new MessengerHandler());
    //是否有当天的记录
    private boolean hasRecord;
    //记录初始步数
    private int createTotalCount;

    /**
     * 自定义handler
     */
    private class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_FROM_CLIENT:
                    try {
                        //这里负责将当前的步数发送出去，可以在界面或者其他地方获取，我这里是在MainActivity中获取来更新界面
                        Messenger clientMessenger = msg.replyTo;
                        Message replyMsg = Message.obtain(null, Constant.MSG_FROM_SERVER);
                        Bundle bundle = new Bundle();
                        bundle.putInt("steps", mCurrentStep);
                        replyMsg.setData(bundle);
                        clientMessenger.send(replyMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initBroadcastReceiver();
        new Thread(new Runnable() {
            public void run() {
                setStepDetector();
            }
        }).start();
        startTimeCount();
        initTodayData();
    }

    /**
     * 初始化广播
     */
    private void initBroadcastReceiver() {
        final IntentFilter filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        // 屏幕亮屏广播
        filter.addAction(Intent.ACTION_SCREEN_ON);
        //关机广播
        filter.addAction(Intent.ACTION_SHUTDOWN);
        // 屏幕解锁广播
        filter.addAction(Intent.ACTION_USER_PRESENT);
        // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
        // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
        // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        //监听日期变化
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIME_TICK);
        mInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action) {
                    // 屏幕灭屏广播
                    case Intent.ACTION_SCREEN_OFF:
                        LogUtil.d(TAG, "onReceive锁屏,准备调起一像素");
                        OnePixelActivity.startMe(getApplicationContext());
                        //屏幕熄灭改为10秒一存储
                        saveDuration = 10000;
                        break;
                    case Intent.ACTION_SCREEN_ON:
                        LogUtil.d(TAG, "onReceive解锁，准备结束一像素");
                        OnePixelActivity.stopMe();
                        break;
                    //关机广播，保存好当前数据
                    case Intent.ACTION_SHUTDOWN:
                        saveStepData();
                        break;
                    // 屏幕解锁广播
                    case Intent.ACTION_USER_PRESENT:
                        saveDuration = 3000;
                        break;
                    // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
                    // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
                    // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
                    case Intent.ACTION_CLOSE_SYSTEM_DIALOGS:
                        saveStepData();
                        break;
                    //监听日期变化
                    case Intent.ACTION_DATE_CHANGED:
                    case Intent.ACTION_TIME_CHANGED:
                    case Intent.ACTION_TIME_TICK:
                        saveStepData();
                        updateNewDayData();
                        break;
                    default:
                        break;
                }
            }
        };
        //注册广播
        registerReceiver(mInfoReceiver, filter);
    }

    /**
     * 获取传感器实例
     */
    private void setStepDetector() {
        if (sensorManager != null) {
            sensorManager = null;
        }
        // 获取传感器管理器的实例
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //android4.4以后可以使用计步传感器
        int VERSION_CODES = Build.VERSION.SDK_INT;
        if (VERSION_CODES >= 19) {
            addCountStepListener();
        }
    }
    /**
     * 添加传感器监听
     */
    private void addCountStepListener() {
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor detectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (countSensor != null) {
            stepSensor = 0;
            sensorManager.registerListener(StepService_Alive.this,
                    countSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Log.w(TAG, "注册了TYPE_STEP_COUNTER传感器");
        } else if (detectorSensor != null) {
            stepSensor = 1;
            sensorManager.registerListener(StepService_Alive.this,
                    detectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Log.w(TAG, "注册了TYPE_STEP_DETECTOR传感器");
        }
    }

    /**
     * 开始循环倒计时，存储步数到数据库。
     */
    private void startTimeCount() {
        timeCount = new TimeCount(saveDuration, 1000);
        timeCount.start();
    }

    /**
     * 用来刷新通知栏并更新数据库。
     */
    private class TimeCount extends CountDownTimer {
        /**
         * @param millisInFuture {@link #start()}调用后几秒调用{@link #onFinish()}
         * @param countDownInterval 接受{@link #onTick(long)}回调的时间间隔.
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
        }
        @Override
        public void onFinish() {
            // 如果计时器正常结束，则每隔三秒存储步数到数据库
            timeCount.cancel();
            saveStepData();
            startTimeCount();
        }
    }

    /**
     * 初始化当天数据
     */
    private void initTodayData() {
        //获取当前时间
        mCurrentDate = DateUtil.getCurrentDateFormatTwo();
        //获取数据库
        stepDataIterator = new StepDataIterator(getApplicationContext());
        //获取当天的数据，用于展示
        StepEntity entity = stepDataIterator.getCurDataByDate(mCurrentDate);
        //为空则说明还没有该天的数据，有则说明已经开始当天的计步了
        if (entity == null) {
            mCurrentStep = 0;
        } else {
            mCurrentStep = Integer.parseInt(entity.getSteps());
        }
    }

    /**
     * 保存当天的数据到数据库中，并去刷新通知栏
     */
    private void saveStepData() {
        //查询数据库中的数据
        StepEntity entity = stepDataIterator.getCurDataByDate(mCurrentDate);
        //为空则说明还没有该天的数据，有则说明已经开始当天的计步了
        if (entity == null) {
            //没有则新建一条数据
            entity = new StepEntity(mCurrentDate, String.valueOf(mCurrentStep));
            stepDataIterator.insertNewData(entity);
        } else {
            //有则更新当前的数据
            entity.setSteps(String.valueOf(mCurrentStep));
            stepDataIterator.updateCurData(entity);
        }
    }

    /**
     * 监听晚上0点变化，初始化数据
     */
    private void updateNewDayData() {
        String time = "00:00";
        if (time.equals(new SimpleDateFormat("HH:mm").format(new Date()))
                || !mCurrentDate.equals(DateUtil.getCurrentDateFormatTwo())) {
            initTodayData();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getApplication().unregisterReceiver(mInfoReceiver);
    }

    /**
     * 由传感器记录当前用户运动步数，注意：该传感器只在4.4及以后才有，并且该传感器记录的数据是从设备开机以后不断累加，
     * 只有当用户关机以后，该数据才会清空，所以需要做数据保护
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        Toast.makeText(getApplicationContext(), "感应器："
                        + event.values[0] + "hasRecord:" + hasRecord
                , Toast.LENGTH_SHORT).show();
        LogUtil.w(TAG, "计步增加...");
        if (stepSensor == 0) {
            int totalStep = (int) event.values[0];
            if (!hasRecord) {
                hasRecord = true;
                createTotalCount = totalStep;
            } else {
                mCurrentStep = totalStep - createTotalCount;
            }
        } else if (stepSensor == 1) {
            if (event.values[0] == 1.0) {
                mCurrentStep++;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
