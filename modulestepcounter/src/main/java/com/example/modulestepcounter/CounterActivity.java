package com.example.modulestepcounter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.library.DateUtil;

public class CounterActivity extends AppCompatActivity {

    TextView tv_hello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        initView(this);
        initParam(this);
        initListener(this);
    }

    /**
     * 1.加载布局
     * @param context context
     */
    protected void initView(Context context) {
        tv_hello = findViewById(R.id.tv_hello);
    }

    /**
     * 2.初始化成员变量
     * @param context Context
     */
    protected void initParam(Context context) {
        // 设置
        tv_hello.setText("当前日期为：" + DateUtil.getCurrentDateFormatOne());
    }

    /**
     * 初始化监听器
     * @param context context
     */
    protected void initListener(Context context) {
        //
    }

}