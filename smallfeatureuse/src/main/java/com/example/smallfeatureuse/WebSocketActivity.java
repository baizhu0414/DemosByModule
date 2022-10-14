package com.example.smallfeatureuse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.smallfeatureuse.utils.WsManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class WebSocketActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_connect;
    WsManager wsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_socket);
        tv_connect = findViewById(R.id.btn_connect_ws);
        requestPermissions();
        tv_connect.setOnClickListener(this);
        wsManager = new WsManager.Builder(getBaseContext()).client(
                new OkHttpClient().newBuilder()
                        .pingInterval(15, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                        .build())
                .needReconnect(true)
                .wsUrl("ws://ip_address:port/url/id")
                .build();
    }

    @Override
    public void onClick(View v) {
        wsManager.startConnect();
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET
                            , Manifest.permission.ACCESS_NETWORK_STATE},
                    1);
        }
    }
}