package com.baurine.instamaterial.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.baurine.instamaterial.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.InjectView;
import butterknife.OnClick;

public class PublishPhotoActivity extends BaseActivity {

    public static final String ARGU_PHOTO_URI = "argu_photo_uri";

    @InjectView(R.id.tb_publish_follower)
    ToggleButton mTbPublishFollower;
    @InjectView(R.id.tb_publish_direct)
    ToggleButton mTbPublishDirect;
    @InjectView(R.id.iv_thumbnail)
    ImageView mIvPhotoThumbnail;

    private Uri mPhotoUri;
    private int mPhotoSize;

    public static void openWithPhotoUri(Uri photoUri, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, PublishPhotoActivity.class);
        intent.putExtra(ARGU_PHOTO_URI, photoUri);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_photo);

        getData(savedInstanceState);
        updateToolbar();
        setupPhotoThumbnail();
    }

    private void getData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mPhotoUri = getIntent().getParcelableExtra(ARGU_PHOTO_URI);
        } else {
            mPhotoUri = savedInstanceState.getParcelable(ARGU_PHOTO_URI);
        }

        mPhotoSize = getResources().getDimensionPixelSize(R.dimen.publish_photo_thumbnail_size);
    }

    private void updateToolbar() {
        getToolbar().setNavigationIcon(R.mipmap.ic_arrow_back_grey600_24dp);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupPhotoThumbnail() {
        mIvPhotoThumbnail.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        mIvPhotoThumbnail.getViewTreeObserver().removeOnPreDrawListener(this);
                        loadPhotoThumbnail();
                        return true;
                    }
                });
    }

    private void loadPhotoThumbnail() {
        Picasso.with(this)
                .load(mPhotoUri)
                .centerCrop()
                .resize(mPhotoSize, mPhotoSize)
                .into(mIvPhotoThumbnail, new Callback() {
                    @Override
                    public void onSuccess() {
                        animatePhotoThumbnail();
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private void animatePhotoThumbnail() {
        mIvPhotoThumbnail.setScaleX(0.0f);
        mIvPhotoThumbnail.setScaleY(0.0f);
        mIvPhotoThumbnail.animate().scaleX(1.0f).scaleY(1.0f)
                .setDuration(400).setInterpolator(new OvershootInterpolator())
                .setStartDelay(200).start();
    }

    @Override
    protected boolean needDrawer() {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_publish_photo) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARGU_PHOTO_URI, mPhotoUri);
    }

    @OnClick(R.id.tb_publish_follower)
    public void onClickTbPublishFollower() {
        mTbPublishFollower.setChecked(true);
        mTbPublishDirect.setChecked(false);
    }

    @OnClick(R.id.tb_publish_direct)
    public void onClickTbPublishDirect() {
        mTbPublishDirect.setChecked(true);
        mTbPublishFollower.setChecked(false);
    }

}
