package com.example.moduleexpandablelist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.library.LogUtil;
import com.example.moduleexpandablelist.model.MyDiffCallback;
import com.example.moduleexpandablelist.model.User;
import com.example.moduleexpandablelist.model.UserListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * AsyncListDiffer管理RecyclerView的更新。
 */
public class MainActivity3 extends AppCompatActivity {

    RecyclerView mRv;
    int mIdMax = 0;
    boolean clicked = false;
    List<User> mOldList;
    List<User> mNewList;
    UserListAdapter adapter;
    // DiffUtil使用
    DiffUtil.DiffResult diffResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        initViewData();
    }

    protected void initViewData() {
        // View
        mRv = findViewById(R.id.rv_syn);

        // Data Old
        mOldList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mIdMax++;
            mOldList.add(new User(i, i + 20, "USER-" + i));
        }
        adapter = new UserListAdapter(mOldList);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(adapter);

    }

    public void clickRefresh(View view) {
        // Data New
        mNewList = new ArrayList<>();
        for (User user: mOldList) {
            mNewList.add(new User(user.getId(), user.getAge(), user.getName()));
        }
        if (!clicked) {
            mNewList.get(0).setAge(10);
            mNewList.get(0).setName("Hello~USER-0");
            mNewList.add(2, new User(mIdMax + 1, 20, "USER-insert"));
            LogUtil.e("clickRefresh:",
                    mOldList.get(0).toString() + "   \n" + mNewList.get(0).toString());
        } else {
            mNewList.get(0).setAge(100);
            mNewList.get(0).setName("USER-0");
            mNewList.remove(2);
            LogUtil.e("clickRefresh:",
                    mOldList.get(0).toString() + "   \n" + mNewList.get(0).toString());
        }
        clicked = !clicked;
        // 设置数据更新
        diffResult = DiffUtil.calculateDiff(new MyDiffCallback(mOldList, mNewList));
        mOldList = mNewList;
        adapter.setListUser(mNewList);
        diffResult.dispatchUpdatesTo(adapter);
    }
}