package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.model.NavItem;
import com.example.ui.activity.R;

import java.util.List;

public class DrawerListAdapter extends BaseAdapter {

    private Context mContext;
    private List<NavItem> navItems;

    public DrawerListAdapter(Context mContext, List<NavItem> navItems) {
        this.mContext = mContext;
        this.navItems = navItems;
    }

    @Override
    public int getCount() {
        return navItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if(convertView == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.drawer_item,null);
        }else{
            view = convertView;
        }
        TextView title = view.findViewById(R.id.title1);
        TextView subTitle = view.findViewById(R.id.subTitle);
        ImageView icon = view.findViewById(R.id.icon);

        NavItem item = navItems.get(position);
        title.setText(item.getmTitle());
        subTitle.setText(item.getmSubtitle());
        icon.setImageResource(item.getmIcon());

        return view;
    }
}
