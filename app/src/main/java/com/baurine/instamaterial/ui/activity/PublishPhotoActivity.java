package com.baurine.instamaterial.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

import com.baurine.instamaterial.R;

import butterknife.InjectView;
import butterknife.OnClick;

public class PublishPhotoActivity extends BaseActivity {

    @InjectView(R.id.tb_publish_follower)
    ToggleButton mTbPublishFollower;
    @InjectView(R.id.tb_publish_direct)
    ToggleButton mTbPublishDirect;

    public static void openWithOpenUri(Uri uri, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, PublishPhotoActivity.class);
        intent.setData(uri);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_photo);

        updateToolbar();
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
