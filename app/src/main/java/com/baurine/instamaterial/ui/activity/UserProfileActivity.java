package com.baurine.instamaterial.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewTreeObserver;

import com.baurine.instamaterial.R;
import com.baurine.instamaterial.ui.adapter.UserProfileAdapter;
import com.baurine.instamaterial.ui.view.RevealBackgroundView;

import butterknife.InjectView;

public class UserProfileActivity extends BaseDrawerActivity
        implements RevealBackgroundView.OnStateChangeListener {
    private static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    @InjectView(R.id.rbv_background)
    RevealBackgroundView mRbvBackground;
    @InjectView(R.id.rv_user_profile)
    RecyclerView mRvUserProfile;

    private UserProfileAdapter mUserProfileAdapter;

    public static void startUserProfileFromLocation(int[] startLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startLocation);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);
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
            mRvUserProfile.setVisibility(View.VISIBLE);
            mUserProfileAdapter = new UserProfileAdapter(this);
            mRvUserProfile.setAdapter(mUserProfileAdapter);
        } else {
            mRvUserProfile.setVisibility(View.INVISIBLE);
        }
    }
}
