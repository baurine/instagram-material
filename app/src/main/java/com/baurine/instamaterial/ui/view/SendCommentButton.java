package com.baurine.instamaterial.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ViewAnimator;

import com.baurine.instamaterial.R;

public class SendCommentButton extends ViewAnimator implements View.OnClickListener {

    public static final int STATE_SEND = 0;
    public static final int STATE_DONE = 1;

    private static final long RESET_STATE_DELAY_MILLIS = 2000;

    private int mCurState;

    private OnSendClickListener mOnSendClickListener;

    private Runnable mRevertStateRunnable = new Runnable() {
        @Override
        public void run() {
            setCurrentState(STATE_SEND);
        }
    };

    public interface OnSendClickListener {
        public void onSendClick(View v);
    }

    public SendCommentButton(Context context) {
        super(context);
        init();
    }

    public SendCommentButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_send_comment_button, this, true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mCurState = STATE_SEND;
        super.setOnClickListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(mRevertStateRunnable);
        super.onDetachedFromWindow();
    }

    public void setCurrentState(int state) {
        if (state == mCurState) {
            return;
        }

        mCurState = state;
        if (mCurState == STATE_DONE) {
            setEnabled(false);
            postDelayed(mRevertStateRunnable, RESET_STATE_DELAY_MILLIS);
            setInAnimation(getContext(), R.anim.slide_in_done);
            setOutAnimation(getContext(), R.anim.slide_out_done);
        } else {
            setEnabled(true);
            setInAnimation(getContext(), R.anim.slide_in_send);
            setOutAnimation(getContext(), R.anim.slide_out_send);
        }
        showNext();
    }

    @Override
    public void onClick(View v) {
        if (mOnSendClickListener != null) {
            mOnSendClickListener.onSendClick(this);
        }
    }

    public void setOnSendClickListener(OnSendClickListener listener) {
        mOnSendClickListener = listener;
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        // do nothing
        // just protect the mOnSendClickListener
    }
}
