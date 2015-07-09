package com.baurine.instamaterial.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.baurine.instamaterial.R;
import com.baurine.instamaterial.ui.view.RevealBackgroundView;
import com.baurine.instamaterial.ui.view.SquareFrameLayout;
import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraHostProvider;
import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;

import butterknife.InjectView;
import butterknife.OnClick;

public class TakePhotoActivity extends BaseActivity
        implements RevealBackgroundView.OnStateChangeListener, CameraHostProvider {
    private static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR =
            new AccelerateInterpolator();
    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR =
            new DecelerateInterpolator();
    private static final int STATE_TAEK_PHOTO = 0;
    private static final int STATE_SETUP_PHOTO = 1;

    private int mCurState;

    @InjectView(R.id.rbv_background)
    RevealBackgroundView mRbvBackground;

    @InjectView(R.id.vs_top_panel)
    ViewSwitcher mVsTopPanel;
    @InjectView(R.id.vs_bottom_pannel)
    ViewSwitcher mVsBottomPanel;
    @InjectView(R.id.btn_take_photo)
    Button mBtnTakePhoto;

    @InjectView(R.id.sfl_camera_root)
    SquareFrameLayout mSflCameraRoot;
    @InjectView(R.id.camera_view)
    CameraView mCameraView;
    @InjectView(R.id.iv_taken_photo)
    ImageView mIvTakenPhoto;
    @InjectView(R.id.v_shutter)
    View mVShutter;

    public static void startCameraFromLocation(int[] startLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, TakePhotoActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startLocation);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        updateState(STATE_TAEK_PHOTO);
        setupRevealBackground(savedInstanceState);
        setupPanel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraView.onPause();
    }

    @Override
    public void onBackPressed() {
        if (mCurState == STATE_SETUP_PHOTO) {
            mBtnTakePhoto.setEnabled(true);
            mVsTopPanel.showNext();
            mVsBottomPanel.showNext();
            updateState(STATE_TAEK_PHOTO);
        } else {
            super.onBackPressed();
        }
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
            mSflCameraRoot.setVisibility(View.VISIBLE);
            startIntroAnimation();
        } else {
            mSflCameraRoot.setVisibility(View.INVISIBLE);
        }
    }

    private void startIntroAnimation() {
        mVsTopPanel.animate().translationY(0).setDuration(400)
                .setInterpolator(DECELERATE_INTERPOLATOR);
        mVsBottomPanel.animate().translationY(0).setDuration(400)
                .setInterpolator(DECELERATE_INTERPOLATOR).start();
    }

    @Override
    protected boolean needDrawer() {
        return false;
    }

    private void showTakenPicture(Bitmap bitmap) {
        mVsTopPanel.showNext();
        mVsBottomPanel.showNext();
        mIvTakenPhoto.setImageBitmap(bitmap);
        updateState(STATE_SETUP_PHOTO);
    }

    private void updateState(int state) {
        mCurState = state;
        if (mCurState == STATE_TAEK_PHOTO) {
            mVsTopPanel.setInAnimation(this, R.anim.slide_in_from_right);
            mVsBottomPanel.setInAnimation(this, R.anim.slide_in_from_right);
            mVsTopPanel.setOutAnimation(this, R.anim.slide_out_to_left);
            mVsBottomPanel.setOutAnimation(this, R.anim.slide_out_to_left);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIvTakenPhoto.setVisibility(View.GONE);
                }
            }, 400);
        } else {
            mVsTopPanel.setInAnimation(this, R.anim.slide_in_from_left);
            mVsBottomPanel.setInAnimation(this, R.anim.slide_in_from_left);
            mVsTopPanel.setOutAnimation(this, R.anim.slide_out_to_right);
            mVsBottomPanel.setOutAnimation(this, R.anim.slide_out_to_right);
            mIvTakenPhoto.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.ib_close_camera)
    public void exitTakePhoto() {
        finish();
    }

    @OnClick(R.id.btn_take_photo)
    public void takePhoto() {
        mBtnTakePhoto.setEnabled(false);
        mCameraView.takePicture(true, false);
        animateShutter();
    }

    @OnClick(R.id.ib_back)
    public void backToTakePhoto() {
        onBackPressed();
    }

    private void animateShutter() {
        mVShutter.setVisibility(View.VISIBLE);
        mVShutter.setAlpha(0.0f);

        ObjectAnimator alphaInAnim = ObjectAnimator.ofFloat(mVShutter, "alpha", 0.0f, 0.8f);
        alphaInAnim.setDuration(200);
        alphaInAnim.setStartDelay(100);
        alphaInAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator alphaOutAnim = ObjectAnimator.ofFloat(mVShutter, "alpha", 0.8f, 0.0f);
        alphaOutAnim.setDuration(200);
        alphaOutAnim.setInterpolator(DECELERATE_INTERPOLATOR);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(alphaInAnim, alphaOutAnim);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mVShutter.setVisibility(View.GONE);
            }
        });
        animatorSet.start();
    }

    @Override
    public CameraHost getCameraHost() {
        return new MyCameraHost(this);
    }

    class MyCameraHost extends SimpleCameraHost {

        private Camera.Size previewSize;

        public MyCameraHost(Context ctxt) {
            super(ctxt);
        }

        @Override
        public boolean useFullBleedPreview() {
            return true;
        }

        @Override
        public Camera.Size getPictureSize(PictureTransaction xact, Camera.Parameters parameters) {
            return previewSize;
        }

        @Override
        public Camera.Parameters adjustPreviewParameters(Camera.Parameters parameters) {
            Camera.Parameters parameters1 = super.adjustPreviewParameters(parameters);
            previewSize = parameters1.getPreviewSize();
            return parameters1;
        }

        @Override
        public void saveImage(PictureTransaction xact, final Bitmap bitmap) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showTakenPicture(bitmap);
                }
            });
        }
    }

}
