package com.baurine.instamaterial.utils;

import android.support.v7.widget.RecyclerView;

public abstract class MyRecyclerScroll extends RecyclerView.OnScrollListener {

    private static final float MIN_DIST = 25;

    private boolean mIsVisible = true;
    private int mScrollDist = 0;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if ((mIsVisible && dy > 0) || (!mIsVisible && dy < 0)) {
            mScrollDist += dy;
        }
        if (mIsVisible && mScrollDist > MIN_DIST) {
            hide();
            mScrollDist = 0;
            mIsVisible = false;
        } else if (!mIsVisible && mScrollDist < -MIN_DIST) {
            show();
            mScrollDist = 0;
            mIsVisible = true;
        }
    }

    public void reset() {
        mIsVisible = true;
        mScrollDist = 0;
    }

    protected abstract void hide();

    protected abstract void show();
}
