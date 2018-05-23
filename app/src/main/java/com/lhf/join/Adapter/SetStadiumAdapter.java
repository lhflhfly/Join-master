package com.lhf.join.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lhf.join.Bean.Stadium;
import com.lhf.join.R;

import java.util.List;

public class SetStadiumAdapter extends RecyclerView.Adapter<SetStadiumAdapter.ViewHolder> {
    private List<Stadium> mStadiumlist;
    private Context mContext;
    private Stadium stadium;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View stadiumView;
        ImageView stadiumpicture;
        TextView stadiumname;
        TextView stadiumtype;
        TextView stadiumadress;
        RatingBar ratingBar;

        public ViewHolder(View view) {
            super(view);
            stadiumView = view;
            stadiumpicture = view.findViewById(R.id.icon_changguan);
            stadiumname = view.findViewById(R.id.tv_changguan);
            stadiumadress = view.findViewById(R.id.tv_changguanadress);
            stadiumtype = view.findViewById(R.id.tv_changguan_type);
            ratingBar = view.findViewById(R.id.ratbar);
        }
    }

    public SetStadiumAdapter(Context context, List<Stadium> stadiumList) {
        mContext = context;
        mStadiumlist = stadiumList;

    }

    @Override
    public SetStadiumAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stadium, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.stadiumView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = holder.getAdapterPosition();
                    Stadium stadium = mStadiumlist.get(position);
                    mOnItemClickListener.onItemClick(stadium);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(SetStadiumAdapter.ViewHolder holder, int position) {
        stadium = mStadiumlist.get(position);
        Glide.with(mContext)
                .load(stadium.getMainpicture())
                .placeholder(R.drawable.loading)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.error)
                .into(holder.stadiumpicture);
        holder.stadiumname.setText(stadium.getStadiumname());
        holder.stadiumadress.setText(stadium.getAdress());
        holder.stadiumtype.setText("[" + stadium.getStadiumtype() + "]");
        holder.ratingBar.setRating(stadium.getGrade());
        holder.ratingBar.setIsIndicator(true);

    }

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(Stadium stadium);
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
