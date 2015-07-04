package com.baurine.instamaterial.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;

import com.baurine.instamaterial.R;
import com.baurine.instamaterial.ui.view.SquareImageView;
import com.baurine.instamaterial.utils.CommonUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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

    private final Map<Integer, Integer> mLikesCount = new HashMap<>();
    private final Set<Integer> mLikedPosition = new HashSet<>();

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
        fillLikesWithRandomValues();
        notifyDataSetChanged();
    }

    private void fillLikesWithRandomValues() {
        for (int i = 0; i < mItemsCount; i++) {
            mLikesCount.put(i, new Random().nextInt(100));
        }
    }

    @Override
    public CellFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(
                R.layout.item_feed, parent, false);
        return new CellFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CellFeedViewHolder holder, int position) {
        runEnterAnimation(holder.itemView, position);
        holder.mSivFeedCenter.setImageResource(mFeedCenterImgs[position % 2]);
        holder.mIvFeedBottom.setImageResource(mFeedBottomImgs[position % 2]);
        updateLikesCounter(holder, 0, false);
        updateLikeButton(holder, false);

        holder.mIbLike.setOnClickListener(this);
        holder.mIbLike.setTag(holder);

        holder.mIbComment.setOnClickListener(this);
        holder.mIbComment.setTag(position);

        holder.mIbMore.setOnClickListener(this);
        holder.mIbMore.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mItemsCount;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.ib_comment) {
            if (mListener != null) {
                mListener.onCommentsClick(v, (int) (v.getTag()));
            }
        } else if (id == R.id.ib_more) {
            if (mListener != null) {
                mListener.onMoreClick(v, (int) (v.getTag()));
            }
        } else if (id == R.id.ib_like) {
            CellFeedViewHolder holder = (CellFeedViewHolder) v.getTag();
            int position = holder.getAdapterPosition();
            int delta;
            if (!mLikedPosition.contains(position)) {
                mLikedPosition.add(position);
                delta = 1;
            } else {
                mLikedPosition.remove(position);
                delta = -1;
            }
            updateLikeButton(holder, true);
            updateLikesCounter(holder, delta, true);
        }
    }

    private void updateLikesCounter(CellFeedViewHolder holder, int deltaValue, boolean animate) {
        int curLikesCount = mLikesCount.get(holder.getAdapterPosition()) + deltaValue;
        String likesCountText = mContext.getResources().getQuantityString(
                R.plurals.likes_count, curLikesCount, curLikesCount);
        if (animate) {
            holder.mTsLikesCounter.setText(likesCountText);
        } else {
            holder.mTsLikesCounter.setCurrentText(likesCountText);
        }

        mLikesCount.put(holder.getAdapterPosition(), curLikesCount);
    }

    private void updateLikeButton(CellFeedViewHolder holder, boolean animate) {
        if (mLikedPosition.contains(holder.getAdapterPosition())) {
            holder.mIbLike.setImageResource(R.mipmap.heart_red);
        } else {
            holder.mIbLike.setImageResource(R.mipmap.ic_heart_outline_grey);
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
        @InjectView(R.id.ib_more)
        ImageButton mIbMore;
        @InjectView(R.id.ts_likes_counter)
        TextSwitcher mTsLikesCounter;

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

        void onMoreClick(View v, int position);
    }
}




