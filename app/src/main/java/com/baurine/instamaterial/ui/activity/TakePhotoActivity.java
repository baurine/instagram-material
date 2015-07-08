package com.baurine.instamaterial.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ViewSwitcher;

import com.baurine.instamaterial.R;
import com.baurine.instamaterial.ui.view.RevealBackgroundView;
import com.baurine.instamaterial.ui.view.SquareFrameLayout;

import butterknife.InjectView;
import butterknife.OnClick;

public class TakePhotoActivity extends BaseActivity
        implements RevealBackgroundView.OnStateChangeListener {
    private static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    @InjectView(R.id.rbv_background)
    RevealBackgroundView mRbvBackground;
    @InjectView(R.id.vs_top_panel)
    ViewSwitcher mVsTopPanel;
    @InjectView(R.id.vs_bottom_pannel)
    ViewSwitcher mVsBottomPanel;
    @InjectView(R.id.sfl_photo_root)
    SquareFrameLayout mSflTakePhotoRoot;

    public static void startCameraFromLocation(int[] startLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, TakePhotoActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startLocation);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        setupRevealBackground(savedInstanceState);
        setupPanel();
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        mRbvBackground.setFillPaintColor(0xFF16181A);
        mRbvBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            mRbvBackground.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            mRbvBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                            mRbvBackground.startFromLocation(startLocation);
                            return false;
                        }
                    });
        } else {
            mRbvBackground.setToFinishedFrame();
        }
    }

    private void setupPanel() {
        mVsTopPanel.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        mVsTopPanel.getViewTreeObserver().removeOnPreDrawListener(this);
                        mVsTopPanel.setTranslationY(-mVsTopPanel.getHeight());
                        mVsBottomPanel.setTranslationY(mVsBottomPanel.getHeight());
                        return false;
                    }
                });
    }

    @Override
    public void onStateChange(int state) {
        if (state == RevealBackgroundView.STATE_FINISHED) {
            mSflTakePhotoRoot.setVisibility(View.VISIBLE);
            startIntroAnimation();
        } else {
            mSflTakePhotoRoot.setVisibility(View.INVISIBLE);
        }
    }

    private void startIntroAnimation() {
        mVsTopPanel.animate().translationY(0).setDuration(400)
                .setInterpolator(INTERPOLATOR);
        mVsBottomPanel.animate().translationY(0).setDuration(400)
                .setInterpolator(INTERPOLATOR).start();
    }

    @Override
    protected boolean needDrawer() {
        return false;
    }

    @OnClick(R.id.ib_close_camera)
    public void exitTakePhoto() {
        finish();
    }
}
