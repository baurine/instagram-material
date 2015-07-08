package com.baurine.instamaterial.ui.manager;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class DrawerLayoutInstaller {

    public static final int DEFAULT_LEFT_DRAWER_WIDTH_DP = 240;

    public static DrawerBuilder from(Activity activity) {
        return new DrawerBuilder(activity);
    }

    public static class DrawerBuilder {
        private Activity mActivity;
        private int mDrawerRootResId;
        private Toolbar mToolbar;
        private DrawerLayout.DrawerListener mDrawerListener;
        private View mDrawerLeftView;
        private int mDrawerLeftWidth;

        public DrawerBuilder(Activity activity) {
            mActivity = activity;
        }

        @SuppressWarnings("UnusedDeclaration")
        private DrawerBuilder() {
            throw new RuntimeException("Not supported, Use DrawerBuilder(Activity activity) instead");
        }

        public DrawerBuilder drawerRoot(int drawerRootResId) {
            mDrawerRootResId = drawerRootResId;
            return this;
        }

        public DrawerBuilder withNavigationIconToggler(Toolbar toolbar) {
            mToolbar = toolbar;
            return this;
        }

        public DrawerBuilder drawerListener(DrawerLayout.DrawerListener listener) {
            mDrawerListener = listener;
            return this;
        }

        public DrawerBuilder drawerLeftView(View drawerLeftView) {
            mDrawerLeftView = drawerLeftView;
            return this;
        }

        public DrawerBuilder drawerLeftWidth(int width) {
            mDrawerLeftWidth = width;
            return this;
        }

        public DrawerLayout build() {
            DrawerLayout drawerLayout = createDrawerLayout();
            addDrawerToActivity(drawerLayout);
            setupToggler(drawerLayout);
            setupDrawerLeftView(drawerLayout);
            drawerLayout.setDrawerListener(mDrawerListener);
            return drawerLayout;
        }

        private DrawerLayout createDrawerLayout() {
            if (mDrawerRootResId != 0) {
                return (DrawerLayout) LayoutInflater.from(mActivity)
                        .inflate(mDrawerRootResId, null);
            } else {
                DrawerLayout drawerLayout = new DrawerLayout(mActivity);

                FrameLayout contentView = new FrameLayout(mActivity);
                drawerLayout.addView(contentView, new DrawerLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ));

                FrameLayout leftDrawer = new FrameLayout(mActivity);
                int drawerLeftWidth =
                        mDrawerLeftWidth != 0 ? mDrawerLeftWidth : DEFAULT_LEFT_DRAWER_WIDTH_DP;
                final ViewGroup.LayoutParams leftDrawerParams =
                        new DrawerLayout.LayoutParams(
                                (int) (drawerLeftWidth * Resources.getSystem().getDisplayMetrics().density),
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                Gravity.START
                        );
                drawerLayout.addView(leftDrawer, leftDrawerParams);

                return drawerLayout;
            }
        }

        private void addDrawerToActivity(DrawerLayout drawerLayout) {
            ViewGroup rootView = (ViewGroup) mActivity.findViewById(android.R.id.content);
            View contentView = rootView.getChildAt(0);
            ViewGroup drawerContentRoot = (ViewGroup) drawerLayout.getChildAt(0);

            rootView.removeView(contentView);
            drawerContentRoot.addView(contentView, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            rootView.addView(drawerLayout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
        }

        private void setupToggler(final DrawerLayout drawerLayout) {
            if (mToolbar != null) {
                mToolbar.setNavigationOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                    drawerLayout.closeDrawer(GravityCompat.START);
                                } else {
                                    drawerLayout.openDrawer(GravityCompat.START);
                                }
                            }
                        }
                );
            }
        }

        private void setupDrawerLeftView(DrawerLayout drawerLayout) {
            if (mDrawerLeftView != null) {
                ((ViewGroup) drawerLayout.getChildAt(1))
                        .addView(mDrawerLeftView, new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                        ));
            }
        }
    }
}
