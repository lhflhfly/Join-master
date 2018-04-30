package com.lhf.join.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lhf.join.Bean.Book;
import com.lhf.join.Bean.Stadium;
import com.lhf.join.Bean.User;
import com.lhf.join.R;
import com.lhf.join.View.Stadium.StadiumActivity;
import com.lhf.join.View.User.LoginActivity;
import com.lhf.join.View.User.OrderInformationActivity;
import com.lhf.join.View.User.UpdateUserActivity;

import org.json.JSONArray;
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

import static com.lhf.join.Constant.Constant.URL_DELETEORDERINFORMATION;
import static com.lhf.join.Constant.Constant.URL_ORDERINFORMATION;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private List<Book> mBooklist;
    private Context mContext;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    static class ViewHolder extends RecyclerView.ViewHolder {
        View bookView;
        TextView stadiumname;
        TextView place;
        TextView time;
        TextView time_order;
        Button btn_delete;

        public ViewHolder(View view) {
            super(view);
            bookView = view;
            stadiumname = view.findViewById(R.id.tv_stadiumname);
            place = view.findViewById(R.id.tv_place);
            time = view.findViewById(R.id.tv_time);
            time_order = view.findViewById(R.id.tv_time_order);
            btn_delete = view.findViewById(R.id.btn_delete);

        }
    }

    public BookAdapter(Context context, List<Book> bookList) {
        mContext = context;
        mBooklist = bookList;

    }

    @Override
    public BookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_book, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(BookAdapter.ViewHolder holder, final int position) {
        final Book book = mBooklist.get(position);
        holder.stadiumname.setText("场馆名：" + book.getStadiumname());
        holder.place.setText(book.getPlaceName());
        holder.time.setText(book.getTime());
        holder.time_order.setText(book.getTime_order());
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitdelete(position, book);
            }
        });
    }

    private void submitdelete(final int position, final Book book) {
        AlertDialog.Builder submit = new AlertDialog.Builder(mContext);
        submit.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBooklist.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(0, mBooklist.size());
                deleteorderInformation(book);
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

    private void deleteorderInformation(Book book) {
        String loadingUrl = URL_DELETEORDERINFORMATION;
        new deleteorderInformationAsyncTask().execute(loadingUrl, String.valueOf(book.getBookingId()));
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
                json.put("bookingId", params[1]);
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
        return mBooklist.size();
    }


}
