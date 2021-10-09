package com.example.moduleexpandablelist.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class LeftRightSlideList extends ListView {
    Context context;
    GestureDetector detector;
    ListSlideListener listSlideListener;

    // constructor
    public LeftRightSlideList(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        detector = new GestureDetector(context, listener);
    }

    // constructor
    public LeftRightSlideList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        detector = new GestureDetector(context, listener);
    }

    // constructor
    public LeftRightSlideList(Context context) {
        super(context);
        this.context = context;
        detector = new GestureDetector(context, listener);
    }

    public void setListSlideListener(ListSlideListener listSlideListener) {
        this.listSlideListener = listSlideListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (detector.onTouchEvent(ev)) {
            if (distance > 120) {
                listSlideListener.onSlideBack(SLIDE_LEFT);
            } else if (distance < -120) {
                listSlideListener.onSlideBack(SLIDE_RIGHT);
            }
            super.onTouchEvent(ev);
            return true;//当左右滑动时自己处理
        }
        return super.onTouchEvent(ev);
    }

    float distance = 0;// left -, right +
    GestureDetector.OnGestureListener listener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            distance = e1.getX() - e2.getX();
            super.onFling(e1, e2, velocityX, velocityY);
            return distance > 120 || distance < -120;
        }
    };

    public static final int SLIDE_LEFT = 1;
    public static final int SLIDE_RIGHT = 2;
    public interface ListSlideListener {
        void onSlideBack(int slideDir);
    }
}
