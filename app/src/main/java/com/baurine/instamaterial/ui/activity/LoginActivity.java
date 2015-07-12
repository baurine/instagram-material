package com.baurine.instamaterial.ui.activity;

import android.os.Bundle;

import com.baurine.instamaterial.R;

import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
