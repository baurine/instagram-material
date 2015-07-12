package com.baurine.instamaterial.ui.activity;

import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.baurine.instamaterial.R;
import com.baurine.instamaterial.utils.CommonUtils;

import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @InjectView(R.id.iv_icon_logo)
    ImageView mIvIconLogo;
    @InjectView(R.id.iv_name_logo)
    ImageView mIvNameLogo;
    @InjectView(R.id.btn_login_weibo)
    Button mBtnLoginWeibo;
    @InjectView(R.id.btn_login_guest)
    Button mBtnLoginGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            startAnimation();
        }
    }

    private void startAnimation() {
        int halfScreenHeight = CommonUtils.getScreenHeight(this) / 2;
        mIvIconLogo.setTranslationY(-halfScreenHeight);
        mIvNameLogo.setTranslationY(-halfScreenHeight);
        mBtnLoginWeibo.setTranslationY(halfScreenHeight);
        mBtnLoginGuest.setTranslationY(halfScreenHeight);

        DecelerateInterpolator interpolator = new DecelerateInterpolator();
        mIvNameLogo.animate().translationY(0).setDuration(600).setStartDelay(300)
                .setInterpolator(new OvershootInterpolator());
        mIvIconLogo.animate().translationY(0).setDuration(300).setStartDelay(900)
                .setInterpolator(interpolator);
        mBtnLoginWeibo.animate().translationY(0).setDuration(300).setStartDelay(1200)
                .setInterpolator(interpolator);
        mBtnLoginGuest.animate().translationY(0).setDuration(300).setStartDelay(1500)
                .setInterpolator(interpolator)
                .start();
    }

    @Override
    protected boolean needDrawer() {
        return false;
    }

    @OnClick(R.id.btn_login_guest)
    public void loginAsGuest() {
        MainActivity.enterFromLoginActivity(this);
    }
}
