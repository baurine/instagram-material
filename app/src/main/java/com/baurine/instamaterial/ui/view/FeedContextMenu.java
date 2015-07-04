package com.baurine.instamaterial.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ViewAnimator;

import com.baurine.instamaterial.R;
import com.baurine.instamaterial.ui.adapter.FeedAdapter;
import com.baurine.instamaterial.utils.CommonUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedContextMenu extends LinearLayout {

    private static final int CONTEXT_MENU_WIDTH = CommonUtils.dpToPx(240);

    private int mFeedItem = -1;

    private OnFeedContextMenuItemClickListener mListener;

    public FeedContextMenu(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_context_menu, this, true);
        setBackgroundResource(R.mipmap.bg_container_shadow);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(CONTEXT_MENU_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void bindToItem(int feedItem) {
        mFeedItem = feedItem;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    public void dismiss() {
        ((ViewGroup) getParent()).removeView(FeedContextMenu.this);
    }

    @OnClick(R.id.btn_report)
    public void onReportClick() {
        if (mListener != null) {
            mListener.onReportClick(mFeedItem);
        }
    }

    @OnClick(R.id.btn_share_photo)
    public void onSharePhotoClick() {
        if (mListener != null) {
            mListener.onSharePhototClick(mFeedItem);
        }
    }

    @OnClick(R.id.btn_copy_share_url)
    public void onCopyShareUrlClick() {
        if (mListener != null)
            mListener.onCopyShareUrlClick(mFeedItem);
    }

    @OnClick(R.id.btn_cancel)
    public void onCancelClick() {
        if (mListener != null) {
            mListener.onCancelClick(mFeedItem);
        }
    }

    public void setOnFeedContextMentItemListener(OnFeedContextMenuItemClickListener listener) {
        mListener = listener;
    }

    public interface OnFeedContextMenuItemClickListener {
        public void onReportClick(int feedItem);

        public void onSharePhototClick(int feedItem);

        public void onCopyShareUrlClick(int feedItem);

        public void onCancelClick(int feedItem);
    }
}
