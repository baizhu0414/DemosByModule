package com.example.modulekeepalive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.library.DateUtil;
import com.example.modulestepcounter.constant.Constant;
import com.example.modulestepcounter.utils.StepDataIterator;
import com.example.modulestepcounter.utils.StepEntity;
import com.example.modulestepcounter.utils.StepUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StepShowActivity extends AppCompatActivity implements Handler.Callback{

    // 第二版
    StepDataIterator stepDataIterator;
    private String mCurSelDate = "";
    TextView mMovementTotalStepsTv;
    TextView mMovementTotalKmTv;
    TextView mMovementTotalKmTimeTv;
    TextView mMovementTotalStepsTimeTv;
    private List<StepEntity> stepEntityList;
    TextView mIsSupportTv;

    private boolean isBind = false;
    private Messenger mClientMessenger;
    private Messenger serverMessenger;
    private TimerTask timerTask;
    private Timer timer;

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            //这里用来获取到Service发来的数据
            case Constant.MSG_FROM_SERVER:
                //如果是今天则更新数据
                if (mCurSelDate.equals(DateUtil.getCurrentDateFormatTwo())) {
                    //记录运动步数
                    int steps = msg.getData().getInt("steps");
                    //设置的步数
                    mMovementTotalStepsTv.setText(String.valueOf(steps));
                    //计算总公里数
                    mMovementTotalKmTv.setText(countTotalKM(steps));
                }
            default:break;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.modulestepcounter.R.layout.activity_counter);

        initView();
        initParam(this);
        requestPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startStepService();
    }

    private void startStepService() {
        if (StepUtil.isSupportStepCountSensor(this)) {
            getRecordList();
            mIsSupportTv.setVisibility(View.GONE);
            setDatas();
            setupService();
        } else {
            mMovementTotalStepsTv.setText("0");
            mIsSupportTv.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置记录数据
     */
    private void setDatas() {
        StepEntity stepEntity = stepDataIterator.getCurDataByDate(mCurSelDate);

        if (stepEntity != null) {
            Integer steps = Integer.parseInt(stepEntity.getSteps());
            //获取全局的步数
            mMovementTotalStepsTv.setText(steps.toString());
            //计算总公里数
            mMovementTotalKmTv.setText(countTotalKM(steps));
        } else {
            //获取全局的步数
            mMovementTotalStepsTv.setText("0");
            //计算总公里数
            mMovementTotalKmTv.setText("0");
        }

        //设置时间
        String time = DateUtil.getWeekStr(mCurSelDate);
        mMovementTotalKmTimeTv.setText(time);
        mMovementTotalStepsTimeTv.setText(time);
    }

    /**
     * 简易计算公里数，假设一步大约有0.7米
     *
     * @param steps 用户当前步数
     * @return
     */
    private String countTotalKM(int steps) {
        float totalMeters = steps * 0.7f;
        DecimalFormat df = new DecimalFormat("#.##");
        //保留两位有效数字
        return df.format(totalMeters / 1000);
    }

    /**
     * 开启计步服务
     */
    private void setupService() {
        Intent intent = new Intent(this, StepService_Alive.class);
        isBind = bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    /**
     * 用于查询应用服务（application Service）的状态的一种interface，
     * 更详细的信息可以参考Service 和 context.bindService()中的描述，
     * 和许多来自系统的回调方式一样，ServiceConnection的方法都是进程的主线程中调用的。
     */
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            timerTask = new TimerTask() {
                public void run() {
                    try {
                        serverMessenger = new Messenger(service);
                        Message msg = Message.obtain(null, Constant.MSG_FROM_CLIENT);
                        msg.replyTo = mClientMessenger;
                        serverMessenger.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            };
            timer = new Timer();
            timer.schedule(timerTask, 0, Constant.MSG_SEND_DURATION);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 1.加载布局
     */
    protected void initView() {
        mIsSupportTv = findViewById(com.example.modulestepcounter.R.id.is_support_tv);
        mMovementTotalStepsTv = findViewById(com.example.modulestepcounter.R.id.movement_total_steps_tv);
        mMovementTotalKmTv = findViewById(com.example.modulestepcounter.R.id.movement_total_km_tv);
        mMovementTotalKmTimeTv = findViewById(com.example.modulestepcounter.R.id.movement_total_km_time_tv);
        mMovementTotalStepsTimeTv = findViewById(com.example.modulestepcounter.R.id.movement_total_steps_time_tv);
    }

    /**
     * 2.初始化成员变量
     *
     * @param context Context
     */
    protected void initParam(Context context) {
        // 设置
        stepEntityList = new ArrayList();
        mCurSelDate = DateUtil.getCurrentDateFormatTwo();
        mClientMessenger = new Messenger(new Handler(this));
    }

    protected void requestPermission() {
        // 获取步数
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
            } else {
                Toast toast = Toast.makeText(this, "已授权", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    /**
     * 获取全部运动历史纪录
     */
    private void getRecordList() {
        //获取数据库
        stepDataIterator = new StepDataIterator(this);
        stepEntityList.clear();
        stepEntityList.addAll(stepDataIterator.getAllDatas());
        if (stepEntityList.size() > 7) {
            //在这里获取历史记录条数，当条数达到7条以上时，就开始删除第七天之前的数据
            for (StepEntity entity : stepEntityList) {
                if (DateUtil.isDateOutDate(entity.getCurDate())) {
                    stepDataIterator.deleteCurData(entity.getCurDate());
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //记得解绑Service，不然多次绑定Service会异常
        if (isBind) this.unbindService(conn);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PackageManager.PERMISSION_GRANTED) {
            if (isBind) this.unbindService(conn);
            startStepService();
        }
    }
}