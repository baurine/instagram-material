package com.baurine.instamaterial.ui.activity;

import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baurine.instamaterial.R;
import com.baurine.instamaterial.data.UserSnsProfile;
import com.baurine.instamaterial.manager.UserManager;
import com.baurine.instamaterial.ui.manager.DrawerLayoutInstaller;
import com.baurine.instamaterial.ui.view.CircleTransformation;
import com.baurine.instamaterial.ui.view.DrawerMenuView;
import com.baurine.instamaterial.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

public class BaseDrawerActivity extends BaseActivity {

    @InjectView(R.id.drawerlayout)
    DrawerLayout mDrawerLayout;

    @Override
    public void setContentView(int resLayoutId) {
        super.setContentViewWithoutInject(R.layout.activity_drawer);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.fl_content_root);
        LayoutInflater.from(this).inflate(resLayoutId, viewGroup, true);
        injectViews();

        setupHeader();
    }

    protected void setupToolbar() {
        super.setupToolbar();
        if (getToolbar() != null) {
            getToolbar().setNavigationOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDrawerLayout.openDrawer(Gravity.LEFT);
                        }
                    }
            );
        }
    }

    private void setupHeader() {
        int avatarSize = getResources().getDimensionPixelSize(R.dimen.drawer_avatar_size);

        String avatarUrl;
        UserSnsProfile profile = UserManager.getInstance().getUserSnsProfile();
        if (profile != null && profile.mAvatarUrlLarge != null) {
            avatarUrl = profile.mAvatarUrlLarge;
        } else {
            avatarUrl = getResources().getString(R.string.user_profile_photo);
        }

        ImageView avatarView = (ImageView) findViewById(R.id.iv_avatar);
        Picasso.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(avatarView);

        if (profile != null && profile.mName != null) {
            TextView tvName = (TextView) findViewById(R.id.tv_label);
            tvName.setText(profile.mName);
        }
    }

    @OnClick(R.id.fl_drawer_header)
    public void onDrawerHeaderClick(final View view) {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] startLocation = new int[2];
                view.getLocationOnScreen(startLocation);
                startLocation[0] += view.getWidth() / 2;
                UserProfileActivity.startUserProfileFromLocation(startLocation, BaseDrawerActivity.this);
                overridePendingTransition(0, 0);
            }
        }, 200);
    }
}
