package com.baurine.instamaterial.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.baurine.instamaterial.R;
import com.baurine.instamaterial.ui.adapter.FeedAdapter;
import com.baurine.instamaterial.ui.manager.FeedContextMenuManager;
import com.baurine.instamaterial.ui.view.FeedContextMenu;
import com.baurine.instamaterial.utils.CommonUtils;
import com.baurine.instamaterial.utils.MyRecyclerScroll;

import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends BaseDrawerActivity
        implements FeedAdapter.OnFeedItemClickListener,
        FeedContextMenu.OnFeedContextMenuItemClickListener {

    private static final String ACTION_SHOW_LOADING_ITEM = "action_show_loading_item";

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    @InjectView(R.id.srl_feed)
    SwipeRefreshLayout mSrlFeed;
    @InjectView(R.id.rv_feed)
    RecyclerView mRvFeed;
    @InjectView(R.id.fab_create)
    FloatingActionButton mFabCreate;
    @InjectView(R.id.cl_content)
    CoordinatorLayout mClContent;

    private FeedAdapter mFeedAdapter;

    private boolean mPendingIntroAnimation = false;

    private MyRecyclerScroll mMyRecyclerScroll;

    public static void bringToTopForPublishingPhoto(Activity startingActivity) {
        Intent intent = new Intent(startingActivity, MainActivity.class);
        // 等同于 launchMode = "singleTask"
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(ACTION_SHOW_LOADING_ITEM);
        startingActivity.startActivity(intent);
    }

    public static void enterFromLoginActivity(Activity startingActivity) {
        Intent intent = new Intent(startingActivity, MainActivity.class);
        startingActivity.startActivity(intent);
        startingActivity.finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (ACTION_SHOW_LOADING_ITEM.equals(intent.getAction())) {
            showFeedLoadingItemDelayed();
        }
    }

    private void showFeedLoadingItemDelayed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRvFeed.smoothScrollToPosition(0);
                mFeedAdapter.showLoadingView();
            }
        }, 500);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFeed();
        setupRefresh();

        if (savedInstanceState == null) {
            mPendingIntroAnimation = true;
        }
    }

    private void setupFeed() {
        LinearLayoutManager llm = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        mRvFeed.setLayoutManager(llm);
        mFeedAdapter = new FeedAdapter(this);
        mFeedAdapter.setOnFeedItemClickListener(this);
        mRvFeed.setAdapter(mFeedAdapter);

        mRvFeed.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
            }
        });

        mMyRecyclerScroll = new MyRecyclerScroll() {
            @Override
            protected void hide() {
                mFabCreate.animate()
                        .translationY(2 * mFabCreate.getHeight())
                        .setInterpolator(new AccelerateInterpolator(2f))
                        .start();
            }

            @Override
            protected void show() {
                mFabCreate.animate()
                        .translationY(0)
                        .setInterpolator(new DecelerateInterpolator(2f))
                        .start();
            }
        };
        mRvFeed.addOnScrollListener(mMyRecyclerScroll);
    }

    private void setupRefresh() {
        mSrlFeed.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSrlFeed.setRefreshing(false);
                        mFeedAdapter.addItem();
                    }
                }, 2000);
            }
        });
    }

    private void startIntroAnimation() {
        mFabCreate.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.fab_size));
        int actionbarSize = CommonUtils.dpToPx(56);
        getToolbar().setTranslationY(-actionbarSize);
        getIvLogo().setTranslationY(-actionbarSize);
        getMenuItemInbox().getActionView().setTranslationY(-actionbarSize);

        getToolbar().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        getIvLogo().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);
        getMenuItemInbox().getActionView().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }
                })
                .start();
    }

    private void startContentAnimation() {
        mFabCreate.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1f))
                .setStartDelay(300)
                .setDuration(ANIM_DURATION_FAB)
                .start();
        mFeedAdapter.updateItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (mPendingIntroAnimation) {
            mPendingIntroAnimation = false;
            startIntroAnimation();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_inbox) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab_create)
    public void onFabCreateClick() {
        int[] startLocation = new int[2];
        mFabCreate.getLocationOnScreen(startLocation);
        startLocation[0] += mFabCreate.getWidth() / 2;
        TakePhotoActivity.startCameraFromLocation(startLocation, this);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onUserProfileClick(View v, int position) {
        int[] startLocation = new int[2];
        v.getLocationOnScreen(startLocation);
        startLocation[0] += v.getWidth() / 2;
        UserProfileCoordinatorActivity.startUserProfileFromLocation(startLocation, this);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onLikeClick(View v, int position) {
        Snackbar.make(mClContent, "Liked!", Snackbar.LENGTH_SHORT).show();
        // Snackbar 显示时，会把之前隐藏的 fab 也重新显示，所以要 reset fab 的内部状态
        mMyRecyclerScroll.reset();
    }

    @Override
    public void onCommentsClick(View v, int position) {
        int[] startLocation = new int[2];
        v.getLocationOnScreen(startLocation);
        CommentsActivity.startCommentsFromLocation(startLocation[1], this);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onMoreClick(View v, int position) {
        FeedContextMenuManager.getInstance().toogleContextMenuFromView(v, position, this);
    }

    @Override
    public void onReportClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onSharePhototClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCopyShareUrlClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCancelClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }
}
