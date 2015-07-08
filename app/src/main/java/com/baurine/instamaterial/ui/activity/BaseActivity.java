package com.baurine.instamaterial.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.baurine.instamaterial.R;
import com.baurine.instamaterial.ui.manager.DrawerLayoutInstaller;
import com.baurine.instamaterial.ui.view.DrawerMenuView;
import com.baurine.instamaterial.utils.CommonUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BaseActivity extends AppCompatActivity
        implements DrawerMenuView.OnDrawerClickListener {

    @InjectView(R.id.tl_toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.iv_logo)
    ImageView mIvLogo;

    private MenuItem mMiInbox;

    @Override
    public void setContentView(int resLayoutId) {
        super.setContentView(resLayoutId);
        ButterKnife.inject(this);

        setupToolbar();
        setupDrawer();
    }

    protected void setupToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.mipmap.ic_menu_white);
        }
    }

    private void setupDrawer() {
        DrawerMenuView drawerMenuView = new DrawerMenuView(this);
        drawerMenuView.setOnDrawerClickListener(this);

        DrawerLayoutInstaller.from(this)
                .drawerRoot(R.layout.drawer_root)
                .drawerLeftView(drawerMenuView)
                .drawerLeftWidth(CommonUtils.dpToPx(300))
                .withNavigationIconToggler(mToolbar)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMiInbox = menu.findItem(R.id.action_inbox);
        mMiInbox.setActionView(R.layout.menu_item_view);
        return true;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public ImageView getIvLogo() {
        return mIvLogo;
    }

    public MenuItem getMenuItemInbox() {
        return mMiInbox;
    }

    @Override
    public void onHeaderClick(View view) {

    }
}
