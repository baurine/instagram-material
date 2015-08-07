package com.baurine.instamaterial.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.baurine.instamaterial.R;
import com.baurine.instamaterial.utils.CommonUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserProfileSingleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();
    private static final int PHOTO_ANIMATION_DELAY = 600;

    private final Context mContext;
    private final int mCellSize;
    private final List<String> mUserPhotos;

    private boolean mLockedAnimation;
    private int mLastAnimationPos;

    public UserProfileSingleAdapter(Context context) {
        mContext = context;
        mCellSize = CommonUtils.getScreenWidth(context) / 3;
        mUserPhotos = Arrays.asList(mContext.getResources().getStringArray(R.array.user_photos));
    }

    @Override
    public int getItemCount() {
        return mUserPhotos.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(
                R.layout.item_photo, parent, false);
        StaggeredGridLayoutManager.LayoutParams layoutParams =
                (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        layoutParams.width = mCellSize;
        layoutParams.height = mCellSize;
        layoutParams.setFullSpan(false);
        view.setLayoutParams(layoutParams);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindPhoto((PhotoViewHolder) holder, position);
    }

    private void bindPhoto(final PhotoViewHolder holder, int position) {
        Picasso.with(mContext)
                .load(mUserPhotos.get(position))
                .resize(mCellSize, mCellSize)
                .centerCrop()
                .into(holder.mIvPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        animatePhoto(holder);
                    }

                    @Override
                    public void onError() {

                    }
                });
        if (position > mLastAnimationPos) {
            mLastAnimationPos = position;
        }
    }

    private void animatePhoto(PhotoViewHolder holder) {
        if (!mLockedAnimation) {
            // 只有第一屏的图片会有这种动画效果
            if (mLastAnimationPos == holder.getAdapterPosition()) {
                setLockedAnimation(true);
            }

            int delayAnimationTime = PHOTO_ANIMATION_DELAY + holder.getAdapterPosition() * 30;

            holder.mSflPhotoRoot.setScaleX(0f);
            holder.mSflPhotoRoot.setScaleY(0f);

            holder.mSflPhotoRoot.animate().scaleX(1f).scaleY(1f)
                    .setDuration(200).setStartDelay(delayAnimationTime)
                    .setInterpolator(INTERPOLATOR).start();
        }
    }

    public void setLockedAnimation(boolean lockedAnimation) {
        mLockedAnimation = lockedAnimation;
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.sfl_photo_root)
        FrameLayout mSflPhotoRoot;
        @InjectView(R.id.iv_photo)
        ImageView mIvPhoto;

        public PhotoViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}




