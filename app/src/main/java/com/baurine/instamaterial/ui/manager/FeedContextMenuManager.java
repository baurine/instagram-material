package com.baurine.instamaterial.ui.manager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.baurine.instamaterial.ui.view.FeedContextMenu;
import com.baurine.instamaterial.utils.CommonUtils;

public class FeedContextMenuManager extends RecyclerView.OnScrollListener
        implements View.OnAttachStateChangeListener {

    private static FeedContextMenuManager mInstance;

    private FeedContextMenu mContextMenuView;

    private boolean mIsContextMenuShowing = false;
    private boolean mIsContextMenuDismissing = false;

    public static FeedContextMenuManager getInstance() {
        if (mInstance == null) {
            mInstance = new FeedContextMenuManager();
        }
        return mInstance;
    }

    private FeedContextMenuManager() {
    }

    @Override
    public void onViewAttachedToWindow(View v) {

    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        mContextMenuView = null;
    }

    public void toogleContextMenuFromView(View openingView, int feedItem,
                                          FeedContextMenu.OnFeedContextMenuItemClickListener listener) {
        if (mContextMenuView == null) {
            showContextMenuFromView(openingView, feedItem, listener);
        } else {
            hideContextMenu();
        }
    }

    private void showContextMenuFromView(final View openingView, int feedItem,
                                         FeedContextMenu.OnFeedContextMenuItemClickListener listener) {
        if (!mIsContextMenuShowing) {
            mIsContextMenuShowing = true;
            mContextMenuView = new FeedContextMenu(openingView.getContext());
            mContextMenuView.bindToItem(feedItem);
            mContextMenuView.addOnAttachStateChangeListener(this);
            mContextMenuView.setOnFeedContextMentItemListener(listener);

            ((ViewGroup) openingView.getRootView().findViewById(android.R.id.content))
                    .addView(mContextMenuView);

            mContextMenuView.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            mContextMenuView.getViewTreeObserver().removeOnPreDrawListener(this);
                            setupContextMenuInitPostion(openingView);
                            performShowAnimation();
                            return false;
                        }
                    }
            );
        }
    }

    private void setupContextMenuInitPostion(View openingView) {
        final int[] openingViewLocation = new int[2];
        openingView.getLocationOnScreen(openingViewLocation);
        int additionalBottomMargin = CommonUtils.dpToPx(16);
        mContextMenuView.setTranslationX(openingViewLocation[0] - mContextMenuView.getWidth() / 3);
        mContextMenuView.setTranslationY(openingViewLocation[1] - mContextMenuView.getHeight()
                - additionalBottomMargin);
    }

    private void performShowAnimation() {
        mContextMenuView.setPivotX(mContextMenuView.getWidth() / 2);
        mContextMenuView.setPivotY(mContextMenuView.getHeight());
        mContextMenuView.setScaleX(0.1f);
        mContextMenuView.setScaleY(0.1f);
        mContextMenuView.animate()
                .scaleX(1f).scaleY(1f)
                .setDuration(150)
                .setInterpolator(new OvershootInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mIsContextMenuShowing = false;
                    }
                });
    }

    public void hideContextMenu() {
        if (!mIsContextMenuDismissing) {
            mIsContextMenuDismissing = true;
            performDismissAnimation();
        }
    }

    private void performDismissAnimation() {
        mContextMenuView.setPivotX(mContextMenuView.getWidth() / 2);
        mContextMenuView.setPivotY(mContextMenuView.getHeight());
        mContextMenuView.animate()
                .scaleX(0.1f).scaleY(0.1f)
                .setDuration(150)
                .setInterpolator(new AccelerateInterpolator())
                .setStartDelay(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (mContextMenuView != null) {
                            mContextMenuView.dismiss();
                        }
                        mIsContextMenuDismissing = false;
                    }
                });
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (mContextMenuView != null) {
            hideContextMenu();
            mContextMenuView.setTranslationY(mContextMenuView.getTranslationY() - dy);
        }
    }
}
