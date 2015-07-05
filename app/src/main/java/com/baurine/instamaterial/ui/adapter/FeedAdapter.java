package com.baurine.instamaterial.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;

import com.baurine.instamaterial.R;
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

    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR =
            new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR =
            new OvershootInterpolator(4);
    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR =
            new DecelerateInterpolator();

    private static final int ANIMATED_ITEMS_COUNT = 2;

    private int[] mFeedCenterImgs = {R.mipmap.img_feed_center_1, R.mipmap.img_feed_center_2};
    private int[] mFeedBottomImgs = {R.mipmap.img_feed_bottom_1, R.mipmap.img_feed_bottom_2};

    private Context mContext;
    private int mLastAnimatedPosition = -1;
    private int mItemsCount = 0;

    private OnFeedItemClickListener mListener;

    private final Map<Integer, Integer> mLikesCount = new HashMap<>();
    private final Set<Integer> mLikedPosition = new HashSet<>();
    private final Map<CellFeedViewHolder, AnimatorSet> mLikedAnimation = new HashMap<>();

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

        CellFeedViewHolder holder = new CellFeedViewHolder(view);
        holder.mIvUserProfile.setOnClickListener(this);
        holder.mIvFeedCenter.setOnClickListener(this);
        holder.mIbLike.setOnClickListener(this);
        holder.mIbComment.setOnClickListener(this);
        holder.mIbMore.setOnClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(CellFeedViewHolder holder, int position) {
        runEnterAnimation(holder.itemView, position);
        holder.mIvFeedCenter.setImageResource(mFeedCenterImgs[position % 2]);
        holder.mIvFeedBottom.setImageResource(mFeedBottomImgs[position % 2]);

        holder.mIvUserProfile.setTag(position);
        holder.mIvFeedCenter.setTag(holder);
        holder.mIbLike.setTag(holder);
        holder.mIbComment.setTag(position);
        holder.mIbMore.setTag(position);

        updateLikesCounter(holder, 0, false);
        updateLikeButton(holder, false);

        if (mLikedAnimation.containsKey(holder)) {
            mLikedAnimation.get(holder).cancel();
        }
        resetLikedAnimation(holder);
    }

    @Override
    public int getItemCount() {
        return mItemsCount;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.iv_user_profile) {
            if (mListener != null) {
                mListener.onUserProfileClick(v, (int) (v.getTag()));
            }
        } else if (id == R.id.ib_comment) {
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
        } else if (id == R.id.iv_feed_center) {
            CellFeedViewHolder holder = (CellFeedViewHolder) v.getTag();
            int position = holder.getAdapterPosition();
            if (!mLikedPosition.contains(position)) {
                mLikedPosition.add(position);
                animatePhotoLike(holder);
                updateLikeButton(holder, false);
                updateLikesCounter(holder, 1, true);
            }
        }
    }

    private void updateLikesCounter(CellFeedViewHolder holder, int deltaValue, boolean animate) {
        int curLikesCount = mLikesCount.get(holder.getAdapterPosition()) + deltaValue;
        String likesCountText = mContext.getResources().getQuantityString(
                R.plurals.likes_count, curLikesCount, curLikesCount);
        if (animate) {
            if (deltaValue > 0) {
                holder.mTsLikesCounter.setInAnimation(holder.mTsLikesCounter.getContext(),
                        R.anim.slide_in_likes_counter_add);
                holder.mTsLikesCounter.setOutAnimation(holder.mTsLikesCounter.getContext(),
                        R.anim.slide_out_likes_counter_add);
            } else if (deltaValue < 0) {
                holder.mTsLikesCounter.setInAnimation(holder.mTsLikesCounter.getContext(),
                        R.anim.slide_in_likes_counter_sub);
                holder.mTsLikesCounter.setOutAnimation(holder.mTsLikesCounter.getContext(),
                        R.anim.slide_out_likes_counter_sub);
            }
            holder.mTsLikesCounter.setText(likesCountText);
        } else {
            holder.mTsLikesCounter.setCurrentText(likesCountText);
        }

        mLikesCount.put(holder.getAdapterPosition(), curLikesCount);
    }

    private void updateLikeButton(final CellFeedViewHolder holder, boolean animate) {
        if (mLikedPosition.contains(holder.getAdapterPosition())) {
            if (animate) {
                if (mLikedAnimation.containsKey(holder)) {
                    return;
                }
                AnimatorSet animatorSet = new AnimatorSet();
                mLikedAnimation.put(holder, animatorSet);
                holder.mIbLike.setEnabled(false);

                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.mIbLike,
                        "rotation", 0f, 360f);
                rotationAnim.setDuration(300);
                rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.mIbLike,
                        "scaleX", 0.2f, 1.0f);
                bounceAnimX.setDuration(300);
                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.mIbLike,
                        "scaleY", 0.2f, 1.0f);
                bounceAnimY.setDuration(300);
                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                bounceAnimY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        holder.mIbLike.setImageResource(R.mipmap.heart_red);
                    }
                });

                animatorSet.play(rotationAnim);
                animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        resetLikedAnimation(holder);
                    }
                });
                animatorSet.start();
            } else {
                holder.mIbLike.setImageResource(R.mipmap.heart_red);
            }
        } else {
            holder.mIbLike.setImageResource(R.mipmap.ic_heart_outline_grey);
        }
    }

    private void animatePhotoLike(final CellFeedViewHolder holder) {
        if (!mLikedAnimation.containsKey(holder)) {
            holder.mVBgLike.setVisibility(View.VISIBLE);
            holder.mIvLike.setVisibility(View.VISIBLE);

            holder.mVBgLike.setScaleX(0.1f);
            holder.mVBgLike.setScaleY(0.1f);
            holder.mVBgLike.setAlpha(1f);
            holder.mIvLike.setScaleX(0.1f);
            holder.mIvLike.setScaleY(0.1f);

            AnimatorSet animatorSet = new AnimatorSet();
            mLikedAnimation.put(holder, animatorSet);
            holder.mIbLike.setEnabled(false);

            ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(holder.mVBgLike,
                    "scaleX", 0.1f, 1f);
            bgScaleXAnim.setDuration(200);
            bgScaleXAnim.setInterpolator(DECELERATE_INTERPOLATOR);
            ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(holder.mVBgLike,
                    "scaleY", 0.1f, 1f);
            bgScaleYAnim.setDuration(200);
            bgScaleYAnim.setInterpolator(DECELERATE_INTERPOLATOR);
            ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(holder.mVBgLike,
                    "alpha", 1f, 0f);
            bgAlphaAnim.setDuration(200);
            bgAlphaAnim.setStartDelay(150);
            bgAlphaAnim.setInterpolator(DECELERATE_INTERPOLATOR);

            ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(holder.mIvLike,
                    "scaleX", 0.1f, 1f);
            imgScaleUpXAnim.setDuration(300);
            imgScaleUpXAnim.setInterpolator(DECELERATE_INTERPOLATOR);
            ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(holder.mIvLike,
                    "scaleY", 0.1f, 1f);
            imgScaleUpYAnim.setDuration(300);
            imgScaleUpYAnim.setInterpolator(DECELERATE_INTERPOLATOR);

            ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(holder.mIvLike,
                    "scaleX", 1f, 0f);
            imgScaleDownXAnim.setDuration(300);
            imgScaleDownXAnim.setInterpolator(DECELERATE_INTERPOLATOR);
            ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(holder.mIvLike,
                    "scaleY", 1f, 0f);
            imgScaleDownYAnim.setDuration(300);
            imgScaleDownYAnim.setInterpolator(DECELERATE_INTERPOLATOR);

            animatorSet.playTogether(bgScaleXAnim, bgScaleYAnim, bgAlphaAnim,
                    imgScaleUpXAnim, imgScaleUpYAnim);
            animatorSet.play(imgScaleDownXAnim).with(imgScaleDownYAnim).after(imgScaleUpXAnim);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    resetLikedAnimation(holder);
                }
            });
            animatorSet.start();
        }
    }

    private void resetLikedAnimation(CellFeedViewHolder holder) {
        mLikedAnimation.remove(holder);
        holder.mIbLike.setEnabled(true);

        holder.mVBgLike.setVisibility(View.GONE);
        holder.mIvLike.setVisibility(View.GONE);
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.iv_user_profile)
        ImageView mIvUserProfile;
        @InjectView(R.id.iv_feed_center)
        ImageView mIvFeedCenter;
        @InjectView(R.id.v_bg_like)
        View mVBgLike;
        @InjectView(R.id.iv_like)
        ImageView mIvLike;
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
        void onUserProfileClick(View v, int position);

        void onCommentsClick(View v, int position);

        void onMoreClick(View v, int position);
    }
}




