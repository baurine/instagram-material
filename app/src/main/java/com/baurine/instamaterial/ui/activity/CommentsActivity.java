package com.baurine.instamaterial.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.baurine.instamaterial.R;
import com.baurine.instamaterial.ui.adapter.CommentsAdapter;
import com.baurine.instamaterial.ui.view.SendCommentButton;
import com.baurine.instamaterial.utils.CommonUtils;

import butterknife.InjectView;

public class CommentsActivity extends BaseActivity
        implements SendCommentButton.OnSendClickListener {

    public static final String ARG_DRAWING_START_LOCATION = "draw_start_location";

    @InjectView(R.id.ll_content)
    LinearLayout mLlContent;
    @InjectView(R.id.rv_comments)
    RecyclerView mRvComments;
    @InjectView(R.id.ll_add_comment)
    LinearLayout mLlAddComment;
    @InjectView(R.id.et_comment)
    EditText mEtComment;
    @InjectView(R.id.btn_send_comment)
    SendCommentButton mBtnSendComment;

    private CommentsAdapter mCommentsAdapter;

    private int drawingStartLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        setupComments();
        setupSendCommentButton();

        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        mLlContent.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mLlContent.getViewTreeObserver().removeOnPreDrawListener(this);
                startIntroAnimation();
                return true;
            }
        });
    }

    private void setupComments() {
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRvComments.setLayoutManager(llm);
        mCommentsAdapter = new CommentsAdapter(this);
        mRvComments.setAdapter(mCommentsAdapter);

        mRvComments.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    mCommentsAdapter.setAnimationLocked(true);
                }
            }
        });
    }

    private void setupSendCommentButton() {
        mBtnSendComment.setOnSendClickListener(this);
    }

    private void startIntroAnimation() {
        ViewCompat.setElevation(getToolbar(), 0);
        mLlContent.setScaleY(0.1f);
        mLlContent.setPivotY(drawingStartLocation);
        mLlAddComment.setTranslationY(200);

        mLlContent.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewCompat.setElevation(getToolbar(), CommonUtils.dpToPx(8));
                        animateContent();
                    }
                })
                .start();
    }

    private void animateContent() {
        mCommentsAdapter.updateItems();
        mLlAddComment.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_inbox) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        ViewCompat.setElevation(getToolbar(), 0);
        mLlContent.animate()
                .translationY(CommonUtils.getScreenHeight(this))
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        CommentsActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }

    @Override
    public void onSendClick(View v) {
        if (validateComment()) {
            mCommentsAdapter.addItem();
            mCommentsAdapter.setAnimationLocked(false);
            mCommentsAdapter.setDelayEnterAnimation(false);
            mRvComments.smoothScrollBy(0,
                    mRvComments.getChildAt(0).getHeight() * mCommentsAdapter.getItemCount());

            mEtComment.setText(null);
            mBtnSendComment.setCurrentState(SendCommentButton.STATE_DONE);
        }
    }

    private boolean validateComment() {
        if (TextUtils.isEmpty(mEtComment.getText())) {
//            mBtnSendComment.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
//            让编辑框晃动的效果也不错
            mEtComment.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }
        return true;
    }
}
