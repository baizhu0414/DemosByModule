package com.example.testalgorithm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.example.library.LogUtil;

import java.util.Arrays;

public class ResultActivity extends AppCompatActivity {

    int[] arr = {23, 45, 17, 11, 13, 89, 72, 26, 3, 17, 11, 13};
    TextView tvResult;
    private static final String TAG = ResultActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        tvResult = findViewById(R.id.tvResult);


        ThreadPoolManager.getInstance().addTask(() -> {
            AlgorithmUtil.quickSort(arr, 0, arr.length - 1);
            LogUtil.e(TAG, "排序完成：" + Arrays.toString(arr));
            new Handler(Looper.getMainLooper()).post(() -> {
                tvResult.setText("结果：" + Arrays.toString(arr));
            });
        });
    }
}