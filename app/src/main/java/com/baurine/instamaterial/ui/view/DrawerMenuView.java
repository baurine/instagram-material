package com.baurine.instamaterial.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.baurine.instamaterial.R;
import com.baurine.instamaterial.ui.adapter.DrawerMenuAdapter;
import com.squareup.picasso.Picasso;

public class DrawerMenuView extends ListView implements View.OnClickListener {

    private OnDrawerClickListener mListener;

    public DrawerMenuView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setChoiceMode(CHOICE_MODE_SINGLE);
        setDivider(getResources().getDrawable(android.R.color.transparent));
        setDividerHeight(0);
        setBackgroundColor(Color.WHITE);

        setupHeader();
        setupAdapter();
    }

    private void setupHeader() {
        setHeaderDividersEnabled(true);
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.item_drawer_header, null);
        int avatarSize = getResources().getDimensionPixelSize(R.dimen.drawer_avatar_size);
        String avatarUrl = getResources().getString(R.string.user_profile_photo);
        ImageView avatarView = (ImageView) headerView.findViewById(R.id.iv_avatar);
        Picasso.with(getContext())
                .load(avatarUrl)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(avatarView);

        addHeaderView(headerView);
        headerView.setOnClickListener(this);
    }

    private void setupAdapter() {
        setAdapter(new DrawerMenuAdapter(getContext()));
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onHeaderClick(v);
        }
    }

    public void setOnDrawerClickListener(OnDrawerClickListener lister) {
        mListener = lister;
    }

    public interface OnDrawerClickListener {
        void onHeaderClick(View view);
    }
}
