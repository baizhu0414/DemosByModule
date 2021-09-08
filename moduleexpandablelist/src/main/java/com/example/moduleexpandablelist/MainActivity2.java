package com.example.moduleexpandablelist;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 通过组合效果实现ListView的底部收缩
 * 效果还行，但是不好控制。因此使用{@link MainActivity}进行展示。
 */
public class MainActivity2 extends AppCompatActivity {

    ListView mLvConversation;
    ListView mLvConversation2;
    ConvListAdapter adapter;

    View mLlTopHint;
    ImageView mIvTopHintArrow;
    boolean isArrowOn = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        hideActionBar();
        initView();
        initData();
        initListner();
    }

    private void hideActionBar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }
    }

    private void initView() {
        mLvConversation = findViewById(R.id.lv_conv_list);
        mLvConversation2 = findViewById(R.id.lv_conv_list_bottom);
        mLlTopHint = findViewById(R.id.ll_top_hint);
        mIvTopHintArrow = findViewById(R.id.iv_top_hint_arrow);
    }

    private void initData() {
        List<String> data = new ArrayList<>();
        for (int i=0; i<30; i++) {
            data.add("" + i + i + i + i + i);
        }
        adapter = new ConvListAdapter(data);
        mLvConversation.setAdapter(adapter);
        mLvConversation2.setAdapter(adapter);
        setListViewHeight(mLvConversation);
        setListViewHeight(mLvConversation2);
    }

    private void initListner() {
        mLlTopHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isArrowOn) {
                    Toast.makeText(MainActivity2.this, "点击...DiDiDi~~", Toast.LENGTH_SHORT).show();
                    mIvTopHintArrow.setImageResource(R.drawable.arrow_off_icon);
                    startAnimOffList(mLvConversation);
                    isArrowOn = false;
                } else {
                    mIvTopHintArrow.setImageResource(R.drawable.arrow_on_icon);
                    mIvTopHintArrow.setImageResource(R.drawable.arrow_on_icon);
                    startAnimOnList(mLvConversation);
                    isArrowOn = true;
                }
            }
        });
    }

    public void startAnimOffList(ListView lv) {
        int height = lv.getResources().getDimensionPixelOffset(R.dimen.list_item_height)
                * lv.getAdapter().getCount();
        Animator anim = AnimatorInflater.loadAnimator(this, R.animator.anim_list_off);
        anim.setTarget(lv);
        anim.start();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(height, 0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lv.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                lv.requestLayout();
            }
        });
        valueAnimator.setDuration(500);
        valueAnimator.start();
    }

    public void startAnimOnList(ListView lv) {
        int height = lv.getResources().getDimensionPixelOffset(R.dimen.list_item_height)
                * lv.getAdapter().getCount();
        Animator anim = AnimatorInflater.loadAnimator(this, R.animator.anim_list_on);
        anim.setTarget(lv);
        anim.start();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, height);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lv.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                lv.requestLayout();
            }
        });
        valueAnimator.setDuration(500);
        valueAnimator.start();
    }

    public class ConvListAdapter extends BaseAdapter {

        List<String> convData;

        ConvListAdapter(List<String> data) {
            this.convData = data;
        }

        @Override
        public int getCount() {
            return convData.size();
        }

        @Override
        public Object getItem(int position) {
            return convData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(MainActivity2.this)
                    .inflate(R.layout.lv_conv_item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.tv = view.findViewById(R.id.tv_conv_item);
            viewHolder.tv.setText("position" + position + convData.get(position));
            return view;
        }

        public class ViewHolder {
            TextView tv;
        }
    }

    /**
     * listview
     * @param listView */
    public void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return; }
        int totalHeight = 0;
        for(int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView); // item listItem.measure(0, 0); // widthMeasureSpec heightMeasureSpec totalHeight += listItem.getMeasuredHeight(); //
            listItem.measure(0, 0); // widthMeasureSpec heightMeasureSpec
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)); // item listView.setLayoutParams(params);
    }
}