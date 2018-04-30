package com.lhf.join.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lhf.join.Bean.App;
import com.lhf.join.R;

import java.util.LinkedList;


public class AppAdapter extends BaseAdapter {
    private LinkedList<App> mData;
    private Context mContext;

    public AppAdapter(LinkedList<App> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.list_app, parent, false);
        ImageView imageView = convertView.findViewById(R.id.img_icon);
        TextView name = convertView.findViewById(R.id.name);
        imageView.setBackgroundResource(mData.get(position).getIcon());
        name.setText(mData.get(position).getName());
        return convertView;
    }
}
