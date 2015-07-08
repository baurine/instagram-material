package com.baurine.instamaterial.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewTreeObserver;

import com.baurine.instamaterial.R;
import com.baurine.instamaterial.ui.view.RevealBackgroundView;

import butterknife.InjectView;

public class TakePhotoActivity extends BaseActivity
        implements RevealBackgroundView.OnStateChangeListener {
    private static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    @InjectView(R.id.rbv_background)
    RevealBackgroundView mRbvBackground;

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
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        mRbvBackground.setFillPaintColor(0xFF16181a);
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

    @Override
    public void onStateChange(int state) {
        if (state == RevealBackgroundView.STATE_FINISHED) {
        } else {
        }
    }

    @Override
    protected boolean needDrawer() {
        return false;
    }
}
