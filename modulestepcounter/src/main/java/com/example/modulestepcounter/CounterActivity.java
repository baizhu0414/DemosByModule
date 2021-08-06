package com.example.modulestepcounter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.library.DateUtil;

public class CounterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        TextView tv_hello = findViewById(R.id.tv_hello);
        tv_hello.setText("当前日期为：" + DateUtil.getCurrentDateFormatOne());
    }
}