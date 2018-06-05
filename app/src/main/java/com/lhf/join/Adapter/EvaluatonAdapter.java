package com.lhf.join.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lhf.join.Bean.Evaluation;
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

public class EvaluatonAdapter extends FixedRecyclerView.Adapter<EvaluatonAdapter.ViewHolder> {
    private List<Evaluation> mEvaluation;
    private Context mContext;
    private boolean mi;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    static class ViewHolder extends RecyclerView.ViewHolder {
        View findView;
        TextView username;
        TextView content;
        TextView tv_grade;
        TextView tv_evaluatetime;
        ImageView user_proflie;

        public ViewHolder(View view) {
            super(view);
            findView = view;
            username = view.findViewById(R.id.tv_username);
            tv_evaluatetime = view.findViewById(R.id.tv_evaluatetime);
            user_proflie = view.findViewById(R.id.proflie);
            tv_grade = view.findViewById(R.id.tv_grade);
            content = view.findViewById(R.id.tv_content);

        }
    }

    public EvaluatonAdapter(Context context, List<Evaluation> evaluationlist) {
        mContext = context;
        mEvaluation = evaluationlist;



    }

    @Override
    public EvaluatonAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_evaluation, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(EvaluatonAdapter.ViewHolder holder, final int position) {
        final Evaluation evaluation = mEvaluation.get(position);
        holder.username.setText(evaluation.getUsername());
        holder.content.setText(evaluation.getContent());
        holder.tv_evaluatetime.setText(evaluation.getEvaluatetime());
        holder.tv_grade.setText("评分："+evaluation.getGrade());
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(mContext);
        ImageLoader.getInstance().init(configuration);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.error) // 设置图片加载或解码过程中发生错误显示的图片
                .showImageOnLoading(R.drawable.loading)
                .resetViewBeforeLoading(false)  // default 设置图片在加载前是否重置、复位
                .delayBeforeLoading(0)  // 下载前的延迟时间
                .build();
        ImageLoader.getInstance().displayImage(evaluation.getIcon(), holder.user_proflie,options);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mEvaluation.size();
    }


}
