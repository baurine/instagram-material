package com.baurine.instamaterial.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    }

}
