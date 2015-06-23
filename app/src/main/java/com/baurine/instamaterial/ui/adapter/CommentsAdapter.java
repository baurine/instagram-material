package com.baurine.instamaterial.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baurine.instamaterial.R;
import com.baurine.instamaterial.ui.view.RoundedTransformation;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private int mAvatarSize;
    private int mItemsCount = 0;
    private int mLastAnimatedPostion = -1;
    private boolean mAnimationLocked = false;
    private boolean mDelayEnterAnimation = true;

    public CommentsAdapter(Context context) {
        mContext = context;
        mAvatarSize = context.getResources().getDimensionPixelSize(R.dimen.comment_avatar_size);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        runEnterAnimation(holder.itemView, position);
        CommentViewHolder viewHolder = (CommentViewHolder) holder;
        switch (position % 3) {
            case 0:
                viewHolder.mTvComment.setText("Lorem ipsum dolor sit amet, consectetur adipisicing elit.");
                break;
            case 1:
                viewHolder.mTvComment.setText("Cupcake ipsum dolor sit amet bear claw.");
                break;
            case 2:
                viewHolder.mTvComment.setText("Cupcake ipsum dolor sit. Amet gingerbread cupcake. Gummies ice cream dessert icing marzipan apple pie dessert sugar plum.");
                break;
        }
        Picasso.with(mContext)
                .load(R.mipmap.ic_launcher)
                .centerCrop()
                .resize(mAvatarSize, mAvatarSize)
                .transform(new RoundedTransformation())
                .into(viewHolder.mIvUserAvatar);
    }

    @Override
    public int getItemCount() {
        return mItemsCount;
    }

    public void updateItems() {
        updateItems(10);
    }

    public void updateItems(int count) {
        mItemsCount = count;
        notifyDataSetChanged();
    }

    public void addItem() {
        mItemsCount++;
        notifyItemInserted(mItemsCount - 1);
    }

    private void runEnterAnimation(View itemView, int position) {
        if (mAnimationLocked) {
            return;
        }

        if (position > mLastAnimatedPostion) {
            mLastAnimatedPostion = position;
            itemView.setTranslationY(100);
            itemView.setAlpha(0.0f);
            itemView.animate()
                    .translationY(0).alpha(1.0f)
                    .setStartDelay(mDelayEnterAnimation ? 20 * position : 0)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mAnimationLocked = true;
                        }
                    })
                    .start();
        }
    }

    public void setAnimationLocked(boolean animationLocked) {
        mAnimationLocked = animationLocked;
    }

    public void setDelayEnterAnimation(boolean delayEnterAnimation) {
        mDelayEnterAnimation = delayEnterAnimation;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.iv_user_avatar)
        ImageView mIvUserAvatar;
        @InjectView(R.id.tv_user_comment)
        TextView mTvComment;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}




