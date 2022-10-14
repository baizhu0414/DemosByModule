package com.example.smallfeatureuse.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.smallfeatureuse.R;

public class CircleProgress extends View {

    private static final String TAG = CircleProgress.class.getSimpleName();
    private static final int START_ANGLE = -90;
    private Paint mPaint;
    private Rect mRect;
    private RectF mRectF;

    private String mText = "000000";
    // 控件宽度
    private float mWidth = 200f;
    // 圆环边的宽度
    private float mArcWidth = 20;
    private float mArcWidthProportion = 0.1f;
    // 颜色控制
    private final int chosenColor;
    private final int unChosenColor;
    // 进度控制
    public float mCurrentProgress = 15f;
    private float mMaxProgress = 60f;

    public CircleProgress(Context context) {
        this(context, null);
    }

    public CircleProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.CircleProgress);
        mMaxProgress = arr.getFloat(R.styleable.CircleProgress_maxProgress, 100);
        mCurrentProgress = arr.getFloat(R.styleable.CircleProgress_startProgress, START_ANGLE);
        chosenColor = arr.getColor(R.styleable.CircleProgress_chosenColor
                , Color.parseColor("#FFC3C1C1"));
        unChosenColor = arr.getColor(R.styleable.CircleProgress_unChosenColor
                , Color.parseColor("#FF0691FF"));
        mText = arr.getString(R.styleable.CircleProgress_startText);
        mArcWidthProportion = arr.getFloat(R.styleable.CircleProgress_arcWidthProportion, 0.1f);
        arr.recycle();

        this.mPaint = new Paint();
        mPaint.setAntiAlias(true);
        this.mRect = new Rect();
        this.mRectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mArcWidth = mWidth * mArcWidthProportion;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float bigCircleR = mWidth / 2;
        float smallCircleR = bigCircleR - mArcWidth;
        // 绘制小圆
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(unChosenColor);
        mPaint.setStrokeWidth(mArcWidth);
        canvas.drawCircle(bigCircleR, bigCircleR, smallCircleR, mPaint);
        // 绘制圆弧
        mPaint.setColor(chosenColor);
        mRectF.set(mArcWidth, mArcWidth, mWidth - mArcWidth, mWidth - mArcWidth);
        canvas.drawArc(mRectF, START_ANGLE, mCurrentProgress / mMaxProgress * 360, false, mPaint);
        Log.w(TAG, "sweepAngle:" + mCurrentProgress / mMaxProgress * 360);
        // 绘制文字
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(mArcWidth * 0.1f);
        mPaint.setTextSize(mWidth / 5.5f);
        mPaint.getTextBounds(mText, 0, mText.length(), mRect);
        mPaint.setColor(Color.BLACK);
        canvas.drawText(mText, bigCircleR - mRect.width() / 2, bigCircleR + mRect.height() / 2, mPaint);
    }

    public void start() {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setFloatValues(0, 60);
        valueAnimator.setDuration(60 * 1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentProgress = (float) animation.getAnimatedValue();
                Log.w(TAG, "0623" + "progress:" + mCurrentProgress
                        + " currentTime:" + System.currentTimeMillis() / 1000);
                invalidate();
            }
        });
        valueAnimator.start();
    }

}
