package com.baurine.instamaterial.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.baurine.instamaterial.R;
import com.baurine.instamaterial.ui.adapter.FeedAdapter;
import com.baurine.instamaterial.ui.manager.FeedContextMenuManager;
import com.baurine.instamaterial.ui.view.FeedContextMenu;
import com.baurine.instamaterial.utils.CommonUtils;

import butterknife.InjectView;

public class MainActivity extends BaseActivity
        implements FeedAdapter.OnFeedItemClickListener,
        FeedContextMenu.OnFeedContextMenuItemClickListener {

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    @InjectView(R.id.rv_feed)
    RecyclerView mRvFeed;
    @InjectView(R.id.fab_create)
    FloatingActionButton mFabCreate;

    private FeedAdapter mFeedAdapter;

    private boolean mPendingIntroAnimation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFeed();

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

    @Override
    public void onUserProfileClick(View v, int position) {

    }

    @Override
    public void onCommentsClick(View v, int position) {
        Intent intent = new Intent(this, CommentsActivity.class);

        int[] startLocation = new int[2];
        v.getLocationOnScreen(startLocation);
        intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, startLocation[1]);

        startActivity(intent);
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
