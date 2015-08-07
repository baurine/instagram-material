package com.baurine.instamaterial.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.baurine.instamaterial.R;
import com.baurine.instamaterial.ui.adapter.UserProfileSingleAdapter;
import com.baurine.instamaterial.ui.view.CircleTransformation;
import com.baurine.instamaterial.ui.view.RevealBackgroundView;
import com.squareup.picasso.Picasso;

import butterknife.InjectView;

public class UserProfileCoordinatorActivity extends BaseDrawerActivity
        implements RevealBackgroundView.OnStateChangeListener {
    private static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";
    private static final int PROFILE_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    @InjectView(R.id.rbv_background)
    RevealBackgroundView mRbvBackground;

    @InjectView(R.id.ll_user_profile_root)
    View mLlUserProfileRoot;
    @InjectView(R.id.iv_user_profile_avatar)
    ImageView mIvUserProfileAvatar;
    @InjectView(R.id.ll_user_profile_detail)
    View mLlUserProfileDetail;
    @InjectView(R.id.ll_user_profile_stats)
    View mLlUserProfileStats;

    @InjectView(R.id.tl_options)
    TabLayout mTlOptions;

    @InjectView(R.id.rv_user_profile)
    RecyclerView mRvUserProfile;

    private UserProfileSingleAdapter mUserProfileAdapter;

    public static void startUserProfileFromLocation(int[] startLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, UserProfileCoordinatorActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startLocation);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_coordinator);

        setupUserProfileHeader();
        setupUserProfileOptions();
        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);
    }

    private void setupUserProfileHeader() {
        int avatarSize = getResources().getDimensionPixelSize(
                R.dimen.user_profile_avatar_size);
        String profileAvatar = getString(R.string.user_profile_photo);

        Picasso.with(this)
                .load(profileAvatar)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(mIvUserProfileAvatar);
    }

    private void setupUserProfileOptions() {
        mTlOptions.addTab(mTlOptions.newTab().setIcon(R.mipmap.ic_grid_on_white));
        mTlOptions.addTab(mTlOptions.newTab().setIcon(R.mipmap.ic_list_white));
        mTlOptions.addTab(mTlOptions.newTab().setIcon(R.mipmap.ic_label_white));
        mTlOptions.addTab(mTlOptions.newTab().setIcon(R.mipmap.ic_place_white));
    }

    private void setupUserProfileGrid() {
        final StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRvUserProfile.setLayoutManager(layoutManager);
        mRvUserProfile.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mUserProfileAdapter.setLockedAnimation(true);
            }
        });
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
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
            mUserProfileAdapter.setLockedAnimation(true);
            mRbvBackground.setToFinishedFrame();
        }
    }

    @Override
    public void onStateChange(int state) {
        if (state == RevealBackgroundView.STATE_FINISHED) {
            mLlUserProfileRoot.setVisibility(View.VISIBLE);
            mTlOptions.setVisibility(View.VISIBLE);
            mRvUserProfile.setVisibility(View.VISIBLE);

            mUserProfileAdapter = new UserProfileSingleAdapter(this);
            mRvUserProfile.setAdapter(mUserProfileAdapter);

            animateProfileHeader();
            animateProfileOptions();
        } else {
            mLlUserProfileRoot.setVisibility(View.INVISIBLE);
            mTlOptions.setVisibility(View.INVISIBLE);
            mRvUserProfile.setVisibility(View.INVISIBLE);
        }
    }

    private void animateProfileHeader() {
        mLlUserProfileRoot.setTranslationY(-mLlUserProfileRoot.getHeight());
        mIvUserProfileAvatar.setTranslationY(-mIvUserProfileAvatar.getHeight());
        mLlUserProfileDetail.setTranslationY(-mLlUserProfileDetail.getHeight());
        mLlUserProfileStats.setAlpha(0f);

        mLlUserProfileRoot.animate().translationY(0).setDuration(300)
                .setInterpolator(INTERPOLATOR);
        mIvUserProfileAvatar.animate().translationY(0).setDuration(300)
                .setStartDelay(100).setInterpolator(INTERPOLATOR);
        mLlUserProfileDetail.animate().translationY(0).setDuration(300)
                .setStartDelay(200).setInterpolator(INTERPOLATOR);
        mLlUserProfileStats.animate().alpha(1f).setDuration(200)
                .setStartDelay(400).setInterpolator(INTERPOLATOR).start();

    }

    private void animateProfileOptions() {
        mTlOptions.setTranslationY(-mTlOptions.getHeight());
        mTlOptions.animate().translationY(0).setDuration(300)
                .setStartDelay(PROFILE_OPTIONS_ANIMATION_DELAY)
                .setInterpolator(INTERPOLATOR);
    }
}
