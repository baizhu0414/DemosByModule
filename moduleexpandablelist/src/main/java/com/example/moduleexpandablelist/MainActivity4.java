package com.example.moduleexpandablelist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.example.moduleexpandablelist.model.User;
import com.example.moduleexpandablelist.model.UserListAdapter;
import com.example.moduleexpandablelist.view.LeftRightSlideList;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建左右滑动效果的ListView。
 * TODO：左右滑动有问题，上下滑动也会触发，可结合velocityX属性判读。
 * 上下滑动时，监听条目展示隐藏，做某些操作。
 */
public class MainActivity4 extends AppCompatActivity implements LeftRightSlideList.ListSlideListener {

    LeftRightSlideList mLvSchedule;
    List<User> userList;
    UserListAdapter adapter;
    View bottomItem;
    private static final String TAG = "MainActivity4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        mLvSchedule = findViewById(R.id.slideLv_leftRight);
        bottomItem = findViewById(R.id.bottom_item);

        initListener(mLvSchedule);
        initData(slideDir);
        mLvSchedule.setListSlideListener(this);

    }

    int slideDir = 1;

    public void initListener(ListView lv) {
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
//                LogUtil.e(TAG, "第一个可见元素位置：" + firstVisibleItem
//                        + "、可见元素数量：" + visibleItemCount + "、总元素数量：" + totalItemCount);
                show20thItem(firstVisibleItem, visibleItemCount);
            }
        });
    }

    /**
     * 滑动到第20个元素（position:19）不可见，则底部显示项目。
     */
    public void show20thItem(int firstVisibleItem, int visibleItemCount) {
        if (slideDir != 1) {
            bottomItem.setVisibility(View.GONE);
            return;
        }
        // 0~f+v-1可见
        if (firstVisibleItem + visibleItemCount + 1 < 20
                && bottomItem.getVisibility() != View.VISIBLE) {
            bottomItem.setVisibility(View.VISIBLE);
        } else if (firstVisibleItem + visibleItemCount + 1 > 20
                && bottomItem.getVisibility() != View.GONE) {
            bottomItem.setVisibility(View.GONE);
        }
    }

    /**
     * 刷新数据
     */
    public void initData(int type) {
        adapter = new UserListAdapter();
        userList = new ArrayList<>();
        if (type == 1) {
            for (int i = 1; i < 50; i++) {
                userList.add(new User(i, 20 + i, "User11111-" + i));
            }
        } else if (type == 2) {
            for (int i = 1; i < 53; i++) {
                userList.add(new User(i, 20 + i, "User22222-" + i));
            }
        }
        adapter.setList(userList);
        mLvSchedule.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * 监听左右滑动回调
     */
    @Override
    public void onSlideBack(int slideDir) {
        if (slideDir != this.slideDir) {
            this.slideDir = slideDir;
            initData(slideDir);
        }
    }
}