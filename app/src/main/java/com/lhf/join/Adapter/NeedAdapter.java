package com.lhf.join.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lhf.join.Bean.Book;
import com.lhf.join.Bean.Need;
import com.lhf.join.R;

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
import static com.lhf.join.Constant.Constant.URL_DELETEORDERINFORMATION;

public class NeedAdapter extends RecyclerView.Adapter<NeedAdapter.ViewHolder> {
    private List<Need> mNeedlist;
    private Context mContext;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    static class ViewHolder extends RecyclerView.ViewHolder {
        View needView;
        TextView stadiumname;
        TextView num;
        TextView num_join;
        TextView time;
        TextView remark;
        Button btn_delete;

        public ViewHolder(View view) {
            super(view);
            needView = view;
            stadiumname = view.findViewById(R.id.tv_stadiumname);
            num = view.findViewById(R.id.tv_num);
            time = view.findViewById(R.id.tv_time);
            num_join = view.findViewById(R.id.tv_num_join);
            remark = view.findViewById(R.id.tv_remark);
            btn_delete = view.findViewById(R.id.btn_delete);

        }
    }

    public NeedAdapter(Context context, List<Need> needList) {
        mContext = context;
        mNeedlist = needList;

    }

    @Override
    public NeedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_need, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(NeedAdapter.ViewHolder holder, final int position) {
        final Need need = mNeedlist.get(position);
        holder.stadiumname.setText("场馆名：" + need.getStadiumname());
        holder.time.setText("时间:" + need.getTime());
        holder.num.setText("召集人数:" + need.getNum());
        holder.num_join.setText("加入人数:" + need.getNum_join());
        holder.remark.setText("备注:" + need.getRemark());
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitdelete(position, need);
            }
        });
    }

    private void submitdelete(final int position, final Need need) {
        AlertDialog.Builder submit = new AlertDialog.Builder(mContext);
        submit.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mNeedlist.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(0, mNeedlist.size());
                deleteorderInformation(need);
            }
        });
        submit.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        submit.setMessage("确认删除此预约？");
        submit.setTitle("提示");
        submit.show();
    }

    private void deleteorderInformation(Need need) {
        String loadingUrl = URL_DELETENEEDINFORMATION;
        new deleteorderInformationAsyncTask().execute(loadingUrl, String.valueOf(need.getNeedId()));
    }

    private class deleteorderInformationAsyncTask extends AsyncTask<String, Integer, String> {
        public deleteorderInformationAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json = new JSONObject();
            try {
                json.put("needId", params[1]);
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
                Request request = new Request.Builder()
                        .url(params[0])
                        .post(requestBody)
                        .build();
                response = okHttpClient.newCall(request).execute();
                results = response.body().string();
                //判断请求是否成功
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.println("返回的数据：" + s);
            List<Book> mData = new ArrayList<>();
            if (!"null".equals(s)) {
                try {
                    JSONObject results = new JSONObject(s);
                    String result = results.getString("result");
                    if ("1".equals(result)) {
                        Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("结果为空");

            }
        }
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
