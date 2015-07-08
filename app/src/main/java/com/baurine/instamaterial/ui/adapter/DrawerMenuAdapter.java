package com.baurine.instamaterial.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baurine.instamaterial.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DrawerMenuAdapter extends BaseAdapter {

    private static final int TYPE_ITEM_MENU = 0;
    private static final int TYPE_ITEM_DIVIDER = 1;

    private LayoutInflater mInflater;
    private List<DrawerMenuItem> mMenuItems = new ArrayList<>();

    public DrawerMenuAdapter(Context context) {
        mInflater = LayoutInflater.from(context);

        setupMenuItems();
    }

    private void setupMenuItems() {
        mMenuItems.add(new DrawerMenuItem(R.mipmap.ic_global_menu_feed, "My Feed"));
        mMenuItems.add(new DrawerMenuItem(R.mipmap.ic_global_menu_direct, "Instagram Direct"));
        mMenuItems.add(new DrawerMenuItem(R.mipmap.ic_global_menu_news, "News"));
        mMenuItems.add(new DrawerMenuItem(R.mipmap.ic_global_menu_popular, "Popular"));
        mMenuItems.add(new DrawerMenuItem(R.mipmap.ic_global_menu_nearby, "Photos Nearby"));
        mMenuItems.add(new DrawerMenuItem(R.mipmap.ic_global_menu_likes, "Photos You've Liked"));
        mMenuItems.add(DrawerMenuItem.dividerMenuItem());
        mMenuItems.add(new DrawerMenuItem(0, "Settings"));
        mMenuItems.add(new DrawerMenuItem(0, "About"));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMenuItems.size();
    }

    @Override
    public DrawerMenuItem getItem(int position) {
        return mMenuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).mIsDivider ? TYPE_ITEM_DIVIDER : TYPE_ITEM_MENU;
    }

    @Override
    public boolean isEnabled(int position) {
        return !getItem(position).mIsDivider;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == TYPE_ITEM_MENU) {
            MenuItemViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_drawer_menu, parent, false);
                holder = new MenuItemViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (MenuItemViewHolder) convertView.getTag();
            }
            bindViewHolder(holder, position);
            return convertView;
        } else {
            return mInflater.inflate(R.layout.item_drawer_divider, parent, false);
        }
    }

    private void bindViewHolder(MenuItemViewHolder viewHolder, int position) {
        DrawerMenuItem item = getItem(position);
        viewHolder.mIvIcon.setImageResource(item.mIconResId);
        viewHolder.mIvIcon.setVisibility(item.mIsDivider ? View.GONE : View.VISIBLE);
        viewHolder.mTvLabel.setText(item.mLabel);
    }

    public static class MenuItemViewHolder {
        @InjectView(R.id.iv_icon)
        ImageView mIvIcon;
        @InjectView(R.id.tv_label)
        TextView mTvLabel;

        public MenuItemViewHolder(View v) {
            ButterKnife.inject(this, v);
        }
    }

    public static class DrawerMenuItem {
        public int mIconResId;
        public String mLabel;
        public boolean mIsDivider;

        private DrawerMenuItem() {

        }

        public DrawerMenuItem(int iconResId, String label) {
            mIconResId = iconResId;
            mLabel = label;
            mIsDivider = false;
        }

        public static DrawerMenuItem dividerMenuItem() {
            DrawerMenuItem item = new DrawerMenuItem();
            item.mIsDivider = true;
            return item;
        }
    }
}




