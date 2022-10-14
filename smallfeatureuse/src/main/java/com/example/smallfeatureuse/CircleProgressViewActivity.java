package com.example.smallfeatureuse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.smallfeatureuse.ui.CircleProgress;

public class CircleProgressViewActivity extends AppCompatActivity {

    CircleProgress circleProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_progress_view);
        circleProgress = findViewById(R.id.circle_progress);
    }

    @Override
    protected void onResume() {
        super.onResume();
        circleProgress.start();
    }
}