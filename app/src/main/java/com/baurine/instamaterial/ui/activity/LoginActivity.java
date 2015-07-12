package com.baurine.instamaterial.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.sns.SNS;
import com.avos.sns.SNSBase;
import com.avos.sns.SNSCallback;
import com.avos.sns.SNSException;
import com.avos.sns.SNSType;
import com.baurine.instamaterial.R;
import com.baurine.instamaterial.manager.UserManager;
import com.baurine.instamaterial.ui.manager.ProgressDialogManager;
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

    @OnClick(R.id.btn_login_weibo)
    public void loginWithWeibo() {
        try {
            SNS.setupPlatform(SNSType.AVOSCloudSNSSinaWeibo,
                    "https://leancloud.cn/1.1/sns/goto/9lo2zxpkz1knk29m");
        } catch (AVException e) {
            showMessage(e.getMessage());
            return;
        }
        ProgressDialogManager.getInstance().showProgressDialog(this, "Login...");
        SNS.loginWithCallback(this, SNSType.AVOSCloudSNSSinaWeibo, new SNSCallback() {
            @Override
            public void done(SNSBase base, SNSException e) {
                if (e == null) {
                    UserManager.getInstance().saveUserSnsProfile(base.authorizedData());
                    SNS.loginWithAuthData(base.userInfo(), new LogInCallback<AVUser>() {
                        @Override
                        public void done(final AVUser user, AVException e) {
                            if (e == null) {
                                UserManager.getInstance().saveAVUser(user);
                                showMessage("Login success!");
                                MainActivity.enterFromLoginActivity(LoginActivity.this);
                            } else {
                                showMessage(e.getMessage());
                            }
                        }
                    });
                } else {
                    showMessage(e.getMessage());
                }
            }
        });
    }

    @OnClick(R.id.btn_login_guest)
    public void loginAsGuest() {
        MainActivity.enterFromLoginActivity(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SNS.onActivityResult(requestCode, resultCode, data, SNSType.AVOSCloudSNSSinaWeibo);
    }

    private void showMessage(String message) {
        ProgressDialogManager.getInstance().hideProgressDialog();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
