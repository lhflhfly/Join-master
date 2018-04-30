package com.lhf.join.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lhf.join.Bean.Book;
import com.lhf.join.Bean.Need;
import com.lhf.join.Bean.User;
import com.lhf.join.R;
import com.lhf.join.View.Find.FindActivity;
import com.lhf.join.View.Find.FindActivity_Me;
import com.lhf.join.View.Find.JoinedNeedActivity;
import com.lhf.join.View.FixedRecyclerView;
import com.lhf.join.View.Stadium.StadiumActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.lhf.join.Constant.Constant.URL_DELETENEEDINFORMATION;

public class FindAdapter extends FixedRecyclerView.Adapter<FindAdapter.ViewHolder> {
    private List<Need> mNeedlist;
    private Context mContext;
    private User mUser;
    private boolean mi;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    static class ViewHolder extends RecyclerView.ViewHolder {
        View findView;
        TextView username;
        TextView stadiumname;
        TextView num;
        TextView num_join;
        TextView time;
        TextView remark;

        public ViewHolder(View view) {
            super(view);
            findView = view;
            username = view.findViewById(R.id.tv_username);
            stadiumname = view.findViewById(R.id.tv_stadiumname);
            num = view.findViewById(R.id.tv_num);
            time = view.findViewById(R.id.tv_time);
            num_join = view.findViewById(R.id.tv_num_join);
            remark = view.findViewById(R.id.tv_remark);

        }
    }

    public FindAdapter(Context context, List<Need> needList, User user, boolean i) {
        mContext = context;
        mNeedlist = needList;
        mUser = user;
        mi = i;

    }

    @Override
    public FindAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_find, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.findView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Need need = mNeedlist.get(position);
                if (mi) {
                    if (mUser.getUserId() == need.getUserId()) {
                        Intent intent = new Intent(mContext, FindActivity_Me.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable("need", need);
                        intent.putExtras(mBundle);
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, FindActivity.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable("user", mUser);
                        mBundle.putSerializable("need", need);
                        intent.putExtras(mBundle);
                        mContext.startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(mContext, JoinedNeedActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("user", mUser);
                    mBundle.putSerializable("need", need);
                    intent.putExtras(mBundle);
                    mContext.startActivity(intent);
                }


            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(FindAdapter.ViewHolder holder, final int position) {
        final Need need = mNeedlist.get(position);
        holder.username.setText(need.getUsername());
        holder.stadiumname.setText("场馆名：" + need.getStadiumname());
        holder.time.setText("时间:" + need.getTime());
        holder.num.setText("召集人数:" + need.getNum());
        holder.num_join.setText("加入人数:" + need.getNum_join());
        holder.remark.setText("备注:" + need.getRemark());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mNeedlist.size();
    }


}
