package com.baurine.instamaterial.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.baurine.instamaterial.R;
import com.baurine.instamaterial.ui.view.SquareImageView;
import com.baurine.instamaterial.utils.CommonUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.CellFeedViewHolder>
        implements View.OnClickListener {

    private static final int ANIMATED_ITEMS_COUNT = 2;

    private int[] mFeedCenterImgs = {R.mipmap.img_feed_center_1, R.mipmap.img_feed_center_2};
    private int[] mFeedBottomImgs = {R.mipmap.img_feed_bottom_1, R.mipmap.img_feed_bottom_2};

    private Context mContext;
    private int mLastAnimatedPosition = -1;
    private int mItemsCount = 0;

    private OnFeedItemClickListener mListener;

    public FeedAdapter(Context context) {
        mContext = context;
    }

    private void runEnterAnimation(View view, int pos) {
        if (pos >= ANIMATED_ITEMS_COUNT - 1) {
            return;
        }

        if (pos > mLastAnimatedPosition) {
            mLastAnimatedPosition = pos;
            view.setTranslationY(CommonUtils.getScreenHeight(mContext));
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .start();
        }
    }

    public void updateItems() {
        updateItems(10);
    }

    public void updateItems(int count) {
        mItemsCount = count;
        notifyDataSetChanged();
    }

    @Override
    public CellFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(
                R.layout.item_feed, parent, false);
        CellFeedViewHolder viewHolder = new CellFeedViewHolder(view);
//        viewHolder.mIvFeedBottom.setOnClickListener(this);
        viewHolder.mIbComment.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CellFeedViewHolder holder, int position) {
        runEnterAnimation(holder.itemView, position);
        holder.mSivFeedCenter.setImageResource(mFeedCenterImgs[position % 2]);
        holder.mIvFeedBottom.setImageResource(mFeedBottomImgs[position % 2]);
//        holder.mIvFeedBottom.setTag(position);
        holder.mIbComment.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mItemsCount;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_comment) {
            if (mListener != null) {
                mListener.onCommentsClick(v, (int) (v.getTag()));
            }
        }
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.siv_feed_center)
        SquareImageView mSivFeedCenter;
        @InjectView(R.id.iv_feed_bottom)
        ImageView mIvFeedBottom;
        @InjectView(R.id.ib_like)
        ImageButton mIbLike;
        @InjectView(R.id.ib_comment)
        ImageButton mIbComment;

        public CellFeedViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener lister) {
        mListener = lister;
    }

    public interface OnFeedItemClickListener {
        void onCommentsClick(View v, int position);
    }
}




