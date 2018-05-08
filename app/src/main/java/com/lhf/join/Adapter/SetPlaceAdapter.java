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
import com.lhf.join.Bean.Place;
import com.lhf.join.Bean.Stadium;
import com.lhf.join.R;

import java.util.List;

public class SetPlaceAdapter extends RecyclerView.Adapter<SetPlaceAdapter.ViewHolder> {
    private List<Place> mPlace;
    private Context mContext;
    private Place place;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View placeView;
        TextView placename;
        TextView placeprice;

        public ViewHolder(View view) {
            super(view);
            placeView = view;
            placename = view.findViewById(R.id.tv_placename);
            placeprice = view.findViewById(R.id.tv_price);

        }
    }

    public SetPlaceAdapter(Context context, List<Place> placeList) {
        mContext = context;
        mPlace = placeList;

    }

    @Override
    public SetPlaceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.placeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = holder.getAdapterPosition();
                    Place place = mPlace.get(position);
                    mOnItemClickListener.onItemClick(place);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(SetPlaceAdapter.ViewHolder holder, int position) {
        place = mPlace.get(position);
        holder.placename.setText(place.getPlacename());
        holder.placeprice.setText(place.getMaterial());


    }

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(Place place);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mPlace.size();
    }


}
