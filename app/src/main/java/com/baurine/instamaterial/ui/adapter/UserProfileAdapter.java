package com.baurine.instamaterial.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.baurine.instamaterial.R;
import com.baurine.instamaterial.ui.view.CircleTransformation;
import com.baurine.instamaterial.utils.CommonUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_PROFILE_HEADER = 0;
    public static final int TYPE_PROFILE_OPTIONS = 1;
    public static final int TYPE_PHOTO = 2;

    private static final int MIN_ITEMS_COUNT = 2;

    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    private final Context mContext;
    private final int mCellSize;
    private final int mAvatarSize;
    private final String mProfileAvatar;
    private final List<String> mUserPhotos;
    private boolean mLockedAnimation;

    public UserProfileAdapter(Context context) {
        mContext = context;
        mCellSize = CommonUtils.getScreenWidth(context) / 3;
        mAvatarSize = context.getResources().getDimensionPixelSize(
                R.dimen.user_profile_avatar_size);
        mProfileAvatar = mContext.getString(R.string.user_profile_photo);
        mUserPhotos = Arrays.asList(mContext.getResources().getStringArray(R.array.user_photos));
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_PROFILE_HEADER;
        } else if (position == 1) {
            return TYPE_PROFILE_OPTIONS;
        } else {
            return TYPE_PHOTO;
        }
    }

    @Override
    public int getItemCount() {
        return mUserPhotos.size() + MIN_ITEMS_COUNT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_PROFILE_HEADER) {
            final View view = LayoutInflater.from(mContext).inflate(
                    R.layout.view_user_profile_header, parent, false);
            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            layoutParams.setFullSpan(true);
            view.setLayoutParams(layoutParams);
            return new ProfileHeaderViewHolder(view);
        } else if (viewType == TYPE_PROFILE_OPTIONS) {
            final View view = LayoutInflater.from(mContext).inflate(
                    R.layout.view_user_profile_options, parent, false);
            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            layoutParams.setFullSpan(true);
            view.setLayoutParams(layoutParams);
            return new ProfileOptionsViewHolder(view);
        } else if (viewType == TYPE_PHOTO) {
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
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_PROFILE_HEADER) {
            bindProfileHeader((ProfileHeaderViewHolder) holder);
        } else if (viewType == TYPE_PROFILE_OPTIONS) {
            bindProfileOptions((ProfileOptionsViewHolder) holder);
        } else if (viewType == TYPE_PHOTO) {
            bindPhoto((PhotoViewHolder) holder, position);
        }
    }

    private void bindProfileHeader(final ProfileHeaderViewHolder holder) {
        Picasso.with(mContext)
                .load(mProfileAvatar)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(mAvatarSize, mAvatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(holder.mIvUserProfileAvatar);
        holder.mLlUserProfileRoot.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        holder.mLlUserProfileRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                        animateProfileHeader(holder);
                        return false;
                    }
                });
    }

    private void bindProfileOptions(final ProfileOptionsViewHolder holder) {
        holder.mLlButtons.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        holder.mLlButtons.getViewTreeObserver().removeOnPreDrawListener(this);
                        holder.mVUnderline.getLayoutParams().width = holder.mIbGrid.getWidth();
                        holder.mVUnderline.requestLayout();
                        animateProfileOptions(holder);
                        return false;
                    }
                });
    }

    private void bindPhoto(final PhotoViewHolder holder, int position) {
        Picasso.with(mContext)
                .load(mUserPhotos.get(position - MIN_ITEMS_COUNT))
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
    }

    private void animateProfileHeader(ProfileHeaderViewHolder holder) {
        if (!mLockedAnimation) {
            holder.mLlUserProfileRoot.setTranslationY(-holder.mLlUserProfileRoot.getHeight());
            holder.mIvUserProfileAvatar.setTranslationY(-holder.mIvUserProfileAvatar.getHeight());
            holder.mLlUserProfileDetail.setTranslationY(-holder.mLlUserProfileDetail.getHeight());
            holder.mLlUserProfileStats.setAlpha(0f);

            holder.mLlUserProfileRoot.animate().translationY(0).setDuration(300)
                    .setInterpolator(INTERPOLATOR);
            holder.mIvUserProfileAvatar.animate().translationY(0).setDuration(300)
                    .setStartDelay(100).setInterpolator(INTERPOLATOR);
            holder.mLlUserProfileDetail.animate().translationY(0).setDuration(300)
                    .setStartDelay(200).setInterpolator(INTERPOLATOR);
            holder.mLlUserProfileStats.animate().alpha(1f).setDuration(200)
                    .setStartDelay(400).setInterpolator(INTERPOLATOR).start();
        }
    }

    private void animateProfileOptions(ProfileOptionsViewHolder holder) {

    }

    private void animatePhoto(PhotoViewHolder holder) {

    }

    static class ProfileHeaderViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.ll_user_profile_root)
        View mLlUserProfileRoot;
        @InjectView(R.id.iv_user_profile_avatar)
        ImageView mIvUserProfileAvatar;
        @InjectView(R.id.ll_user_profile_detail)
        View mLlUserProfileDetail;
        @InjectView(R.id.ll_user_profile_stats)
        View mLlUserProfileStats;

        public ProfileHeaderViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    static class ProfileOptionsViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.ib_grid)
        ImageButton mIbGrid;
        @InjectView(R.id.ib_list)
        ImageButton mIbList;
        @InjectView(R.id.ib_place)
        ImageButton mIbPlace;
        @InjectView(R.id.ib_label)
        ImageButton mIbLabel;
        @InjectView(R.id.v_underline)
        View mVUnderline;
        @InjectView(R.id.ll_buttons)
        View mLlButtons;

        public ProfileOptionsViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
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




