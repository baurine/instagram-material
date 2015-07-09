package com.baurine.instamaterial.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.baurine.instamaterial.R;

public class PublishPhotoActivity extends BaseActivity {

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

}
