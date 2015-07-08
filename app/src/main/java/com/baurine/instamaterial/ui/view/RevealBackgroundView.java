package com.baurine.instamaterial.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

public class RevealBackgroundView extends View {

    public static final int STATE_NOT_STARTED = 0;
    public static final int STATE_FILL_STARTED = 1;
    public static final int STATE_FINISHED = 2;

    private static final Interpolator INTERPOLATOR = new AccelerateInterpolator();
    private static final int FILL_TIME = 400;

    private int mState = STATE_NOT_STARTED;

    private Paint mFillPaint;
    private int mCurRadius;
    private ObjectAnimator mRevealAnimator;

    private int mStartLocationX, mStartLocationY;

    private OnStateChangeListener mListener;

    public RevealBackgroundView(Context context) {
        super(context);
        init();
    }

    public RevealBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RevealBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RevealBackgroundView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mFillPaint = new Paint();
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setColor(Color.WHITE);
    }

    // 这个方法很重要，没有这个方法，
    // ObjectAnimator.ofInt(this, "mCurRadius", 0, getWidth() + getHeight()) 不能正常工作
    public void setMCurRadius(int radius) {
        mCurRadius = radius;
        invalidate();
    }

    public void setFillPaintColor(int color) {
        mFillPaint.setColor(color);
    }

    public void startFromLocation(int[] tapLocationOnScreen) {
        changeState(STATE_FILL_STARTED);
        mStartLocationX = tapLocationOnScreen[0];
        mStartLocationY = tapLocationOnScreen[1];
        mRevealAnimator = ObjectAnimator.ofInt(this, "mCurRadius", 0, getWidth() + getHeight())
                .setDuration(FILL_TIME);
        mRevealAnimator.setInterpolator(INTERPOLATOR);
        mRevealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                changeState(STATE_FINISHED);
            }
        });
        mRevealAnimator.start();
    }

    public void setToFinishedFrame() {
        changeState(STATE_FINISHED);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mState == STATE_FINISHED) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), mFillPaint);
        } else {
            canvas.drawCircle(mStartLocationX, mStartLocationY, mCurRadius, mFillPaint);
        }
    }

    private void changeState(int state) {
        if (mState == state) {
            return;
        }

        mState = state;
        if (mListener != null) {
            mListener.onStateChange(state);
        }
    }

    public void setOnStateChangeListener(OnStateChangeListener listener) {
        mListener = listener;
    }

    public interface OnStateChangeListener {
        void onStateChange(int state);
    }

}
