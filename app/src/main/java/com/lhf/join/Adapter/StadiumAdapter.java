package com.lhf.join.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lhf.join.Bean.Stadium;
import com.lhf.join.Bean.User;
import com.lhf.join.R;
import com.lhf.join.View.Stadium.StadiumActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.Serializable;
import java.util.List;

public class StadiumAdapter extends RecyclerView.Adapter<StadiumAdapter.ViewHolder> {
    private List<Stadium> mStadiumlist;
    private Context mContext;
    private User mUser;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View stadiumView;
        ImageView stadiumpicture;
        TextView stadiumname;
        TextView stadiumtype;
        TextView stadiumadress;

        public ViewHolder(View view) {
            super(view);
            stadiumView = view;
            stadiumpicture = view.findViewById(R.id.icon_changguan);
            stadiumname = view.findViewById(R.id.tv_changguan);
            stadiumadress = view.findViewById(R.id.tv_changguanadress);
            stadiumtype = view.findViewById(R.id.tv_changguan_type);
        }
    }

    public StadiumAdapter(Context context, List<Stadium> stadiumList, User user) {
        mContext = context;
        mStadiumlist = stadiumList;
        mUser = user;
    }

    @Override
    public StadiumAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_changguan, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.stadiumView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Stadium stadium = mStadiumlist.get(position);
                Intent intent = new Intent(mContext, StadiumActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("user", mUser);
                mBundle.putSerializable("stadium", stadium);
                intent.putExtras(mBundle);
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(StadiumAdapter.ViewHolder holder, int position) {
        Stadium stadium = mStadiumlist.get(position);
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(mContext);
        ImageLoader imageLoader = ImageLoader.getInstance();
        ImageLoader.getInstance().init(configuration);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.error) // 设置图片加载或解码过程中发生错误显示的图片
                .showImageOnLoading(R.drawable.loading)
                .resetViewBeforeLoading(false)  // default 设置图片在加载前是否重置、复位
                .delayBeforeLoading(1000)  // 下载前的延迟时间
                .build();
        ImageLoader.getInstance().displayImage(stadium.getMainpicture(), holder.stadiumpicture,options);

//        Glide.with(mContext)
//                .load(stadium.getMainpicture())
//                .placeholder(R.drawable.loading)
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .error(R.drawable.error)
//                .into(holder.stadiumpicture);
        holder.stadiumname.setText(stadium.getStadiumname());
        holder.stadiumadress.setText(stadium.getAdress());
        holder.stadiumtype.setText("[" + stadium.getStadiumtype() + "]");

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mStadiumlist.size();
    }


}
