package com.baurine.instamaterial.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.baurine.instamaterial.R;

public class SendingProgressView extends View {

    private static final int STATE_NOT_STARTED = 0;
    private static final int STATE_PROGRESS_STARTED = 1;
    private static final int STATE_DONE_STARTED = 2;
    private static final int STATE_FINISHED = 3;

    private static final int PROGRESS_STROKE_WIDTH = 10;
    private static final int INNER_CIRCLE_PADDING = 30;
    private static final int MAX_DONE_BG_OFFSET = 800;
    private static final int MAX_DONE_CHECKMARK_OFFSET = 400;

    private int mCurState = STATE_NOT_STARTED;
    private float mCurProgress = 0;
    private float mCurDoneBgOffset = MAX_DONE_BG_OFFSET;
    private float mCurCheckmarkOffset = MAX_DONE_CHECKMARK_OFFSET;

    private int mCheckmarkXPosition = 0;
    private int mCheckmarkYPosition = 0;

    private Paint mProgressPaint;
    private Paint mDoneBgPaint;
    private Paint mDoneMaskPaint;
    private Paint mCheckmarkPaint;

    private Bitmap mInnerCircleMaskBitmap;
    private Bitmap mCheckmarkBitmap;

    private RectF mProgressBounds;

    private Canvas mTempCanvas;
    private Bitmap mTempBitmap;

    private ObjectAnimator mProgressAnimator;
    private ObjectAnimator mDoneAnimator;
    private ObjectAnimator mCheckmarkAnimator;

    private OnLoadingFinishedListener mListener;

    public SendingProgressView(Context context) {
        super(context);
        init();
    }

    public SendingProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SendingProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SendingProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setupProgressPaint();
        setupSimulateProgressAnimator();

        setupDonePaint();
        setupDoneAnimators();
    }

    public void setMCurProgress(float progress) {
        mCurProgress = progress;
        postInvalidate();
    }

    public void setMCurDoneBgOffset(float offset) {
        mCurDoneBgOffset = offset;
        postInvalidate();
    }

    public void setMCurCheckmarkOffset(float offset) {
        mCurCheckmarkOffset = offset;
        postInvalidate();
    }

    private void setupProgressPaint() {
        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setColor(0xffffffff);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(PROGRESS_STROKE_WIDTH);
    }

    private void setupSimulateProgressAnimator() {
        mProgressAnimator = ObjectAnimator.ofFloat(this, "mCurProgress", 0f, 100f)
                .setDuration(2000);
        mProgressAnimator.setInterpolator(new AccelerateInterpolator());
        mProgressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                changeState(STATE_DONE_STARTED);
            }
        });
    }

    private void setupDonePaint() {
        mDoneBgPaint = new Paint();
        mDoneBgPaint.setAntiAlias(true);
        mDoneBgPaint.setStyle(Paint.Style.FILL);
        mDoneBgPaint.setColor(0xff39cb72);

        mDoneMaskPaint = new Paint();
        mDoneMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        mCheckmarkPaint = new Paint();
    }

    private void setupDoneAnimators() {
        mDoneAnimator = ObjectAnimator.ofFloat(this, "mCurDoneBgOffset", MAX_DONE_BG_OFFSET, 0)
                .setDuration(300);
        mDoneAnimator.setInterpolator(new AccelerateInterpolator());

        mCheckmarkAnimator = ObjectAnimator.ofFloat(this, "mCurCheckmarkOffset",
                MAX_DONE_CHECKMARK_OFFSET, 0).setDuration(300);
        mCheckmarkAnimator.setInterpolator(new OvershootInterpolator());
        mCheckmarkAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                changeState(STATE_FINISHED);
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateProgressBounds();
        setupCheckmarkBitmap();
        setupDoneMaskBitmap();
        resetTempCanvas();
    }

    private void updateProgressBounds() {
        mProgressBounds = new RectF(
                PROGRESS_STROKE_WIDTH, PROGRESS_STROKE_WIDTH,
                getWidth() - PROGRESS_STROKE_WIDTH, getWidth() - PROGRESS_STROKE_WIDTH
        );
    }

    private void setupCheckmarkBitmap() {
        mCheckmarkBitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_done_white_48dp);
        mCheckmarkXPosition = (getWidth() - mCheckmarkBitmap.getWidth()) / 2;
        mCheckmarkYPosition = (getWidth() - mCheckmarkBitmap.getHeight()) / 2;
    }

    private void setupDoneMaskBitmap() {
        mInnerCircleMaskBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas maskCanvas = new Canvas(mInnerCircleMaskBitmap);
        maskCanvas.drawCircle(getWidth() / 2, getWidth() / 2,
                getWidth() / 2 - INNER_CIRCLE_PADDING, new Paint());
    }

    private void resetTempCanvas() {
        mTempBitmap = Bitmap.createBitmap(getWidth(), getWidth(), Bitmap.Config.ARGB_8888);
        mTempCanvas = new Canvas(mTempBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCurState == STATE_PROGRESS_STARTED) {
            drawArcForCurProgress();
        } else if (mCurState == STATE_DONE_STARTED) {
            drawFrameForDoneAnimation();
            postInvalidate(); // ??
        } else if (mCurState == STATE_FINISHED) {
            drawFinishedState();
        }

        canvas.drawBitmap(mTempBitmap, 0, 0, null);
    }

    private void drawArcForCurProgress() {
        mTempCanvas.drawArc(mProgressBounds, -90f, 360 * mCurProgress / 100,
                false, mProgressPaint);
    }

    private void drawFrameForDoneAnimation() {
        // 绘制 DoneBg
        mTempCanvas.drawCircle(getWidth() / 2, getWidth() / 2 + mCurDoneBgOffset,
                getWidth() / 2 - INNER_CIRCLE_PADDING, mDoneBgPaint);
        // 绘制 Check Mark
        mTempCanvas.drawBitmap(mCheckmarkBitmap, mCheckmarkXPosition,
                mCheckmarkYPosition + mCurCheckmarkOffset, mCheckmarkPaint);
        // 绘制 Mask
        mTempCanvas.drawBitmap(mInnerCircleMaskBitmap, 0, 0, mDoneMaskPaint);
        // 绘制最外面的白框
        mTempCanvas.drawArc(mProgressBounds, 0f, 360f, false, mProgressPaint);
    }

    // 其实我觉得这个函数没必要
    private void drawFinishedState() {
        // 绘制 DoneBg
        mTempCanvas.drawCircle(getWidth() / 2, getWidth() / 2,
                getWidth() / 2 - INNER_CIRCLE_PADDING, mDoneBgPaint);
        // 绘制 Check Mark
        mTempCanvas.drawBitmap(mCheckmarkBitmap, mCheckmarkXPosition,
                mCheckmarkYPosition, mCheckmarkPaint);
        // 绘制最外面的白框
        mTempCanvas.drawArc(mProgressBounds, 0f, 360f, false, mProgressPaint);
    }

    private void changeState(int state) {
        if (mCurState == state) {
            return;
        }

        // 这行代码不能放到 resetTempCanvas() 里
        // 因为 mTempBitmap 初始时为 null
        mTempBitmap.recycle();
        resetTempCanvas();

        mCurState = state;
        if (state == STATE_PROGRESS_STARTED) {
            setMCurProgress(0);
            mProgressAnimator.start();
        } else if (state == STATE_DONE_STARTED) {
            setMCurDoneBgOffset(MAX_DONE_BG_OFFSET);
            setMCurCheckmarkOffset(MAX_DONE_CHECKMARK_OFFSET);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playSequentially(mDoneAnimator, mCheckmarkAnimator);
            animatorSet.start();
        } else if (state == STATE_FINISHED) {
            if (mListener != null) {
                mListener.onLoadingFinished();
            }
        }
    }

    public void simulateProgress() {
        changeState(STATE_PROGRESS_STARTED);
    }

    public void setOnLoadingFinishedListener(OnLoadingFinishedListener listener) {
        mListener = listener;
    }

    public interface OnLoadingFinishedListener {
        void onLoadingFinished();
    }
}
