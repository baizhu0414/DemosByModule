package com.example.moduleexpandablelist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ListView;

import com.example.moduleexpandablelist.model.User;
import com.example.moduleexpandablelist.model.UserListAdapter;
import com.example.moduleexpandablelist.view.LeftRightSlideList;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建左右滑动效果的ListView。
 */
public class MainActivity4 extends AppCompatActivity implements LeftRightSlideList.ListSlideListener {

    LeftRightSlideList mLvSchedule;
    List<User> userList;
    UserListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        mLvSchedule = findViewById(R.id.slideLv_leftRight);

        initData(slideDir);
        mLvSchedule.setListSlideListener(this);

    }

    int slideDir = 1;
    public void initData(int type) {
        adapter = new UserListAdapter();
        userList = new ArrayList<>();
        if (type == 1) {
            userList.add(new User(1, 20, "User1-1"));
            userList.add(new User(2, 21, "User1-2"));
            userList.add(new User(3, 23, "User1-3"));
        } else if (type == 2) {
            userList.add(new User(1, 20, "User2-1"));
            userList.add(new User(2, 21, "User2-2"));
            userList.add(new User(3, 23, "User2-3"));
        }
        adapter.setList(userList);
        mLvSchedule.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSlideBack(int slideDir) {
        if (slideDir != this.slideDir) {
            this.slideDir = slideDir;
            initData(slideDir);
        }
    }
}