package com.example.moduleexpandablelist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 可能是最简单的收缩列表展示方式了。
 */
public class MainActivity extends AppCompatActivity {

    ListView listView;
    List<String> level1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.list);

        prepareData();
        MyListAdapter adapter = new MyListAdapter(level1, this);
        listView.setAdapter(adapter);
    }

    void prepareData() {
        level1 = new ArrayList<>();
        level1.add("title1");
        level1.add("title2");

    }
}