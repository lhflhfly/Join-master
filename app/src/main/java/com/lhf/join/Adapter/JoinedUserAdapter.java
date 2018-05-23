package com.lhf.join.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lhf.join.Bean.Need;
import com.lhf.join.Bean.User;
import com.lhf.join.R;
import com.lhf.join.View.Find.FindActivity;
import com.lhf.join.View.Find.FindActivity_Me;
import com.lhf.join.View.Find.JoinedNeedActivity;
import com.lhf.join.View.FixedRecyclerView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

import okhttp3.MediaType;

public class JoinedUserAdapter extends FixedRecyclerView.Adapter<JoinedUserAdapter.ViewHolder> {
    private List<User> mUserlist;
    private Context mContext;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    static class ViewHolder extends RecyclerView.ViewHolder {
        View findView;
        TextView username;
        TextView usersex;
        TextView usertel;
        Button btn_call;
        ImageView user_proflie;

        public ViewHolder(View view) {
            super(view);
            findView = view;
            username = view.findViewById(R.id.tv_username);
            usersex = view.findViewById(R.id.tv_usersex);
            usertel = view.findViewById(R.id.tv_usertel);
            btn_call  = view.findViewById(R.id.btn_call);
            user_proflie = view.findViewById(R.id.user_proflie);

        }
    }

    public JoinedUserAdapter(Context context, List<User> needList) {
        mContext = context;
        mUserlist = needList;
    }

    @Override
    public JoinedUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_joineduser, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(JoinedUserAdapter.ViewHolder holder, final int position) {
        final User user = mUserlist.get(position);
        holder.username.setText(user.getUsername());
        holder.usersex.setText("性别:" + user.getSex());
        holder.usertel.setText("联系方式:" + user.getTel());
        if("女".equals(user.getSex())){
            holder.btn_call.setText("联系她");
        }else{
            holder.btn_call.setText("联系他");
        }
        holder.btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+user.getTel()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(mContext);
        ImageLoader.getInstance().init(configuration);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.error) // 设置图片加载或解码过程中发生错误显示的图片
                .showImageOnLoading(R.drawable.loading)
                .resetViewBeforeLoading(false)  // default 设置图片在加载前是否重置、复位
                .delayBeforeLoading(100)  // 下载前的延迟时间
                .build();
        ImageLoader.getInstance().displayImage(user.getProflie(), holder.user_proflie,options);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mUserlist.size();
    }


}
