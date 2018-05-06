package com.lhf.join.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lhf.join.Adapter.BookAdapter;
import com.lhf.join.Base.BaseFragment;
import com.lhf.join.Bean.Book;
import com.lhf.join.Bean.User;
import com.lhf.join.R;
import com.lhf.join.View.Collection.MyCollection;
import com.lhf.join.View.Find.MyJoinNeedActivity;
import com.lhf.join.View.User.LoginActivity;
import com.lhf.join.View.User.NeedInformationActivity;
import com.lhf.join.View.User.OrderInformationActivity;
import com.lhf.join.View.User.UserInformationActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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

import static com.lhf.join.Constant.Constant.URL_ORDERINFORMATION;
import static com.lhf.join.Constant.Constant.URL_PROFLIE;
import static com.lhf.join.Constant.Constant.URL_SELECTUSERBYUSERID;

public class NoUseOrderFragment extends BaseFragment{
    private User user;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.nouse, null);
        recyclerView = view.findViewById(R.id.rv_nouse);
        layoutManager = new LinearLayoutManager(mContext);
        return view;


    }

    @Override
    public void onResume() {
        super.onResume();
        orderInformation_nouse(user);

    }

    @Override
    protected void initData() {
        user = (User) getActivity().getIntent().getSerializableExtra("user");
        orderInformation_nouse(user);


    }

    private void orderInformation_nouse(User user) {
        String loadingUrl = URL_ORDERINFORMATION;
        new orderInformationAsyncTask().execute(loadingUrl, String.valueOf(user.getUserId()));
    }

    private class orderInformationAsyncTask extends AsyncTask<String, Integer, String> {
        public orderInformationAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            int method = 1;
            JSONObject json = new JSONObject();
            try {
                json.put("userId", params[1]);
                json.put("method", method);
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
                    JSONArray results = new JSONArray(s);
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject js = results.getJSONObject(i);
                        Book book = new Book();
                        book.setBookingId(js.getInt("bookingId"));
                        book.setStadiumname(js.getString("stadiumname"));
                        book.setPlaceName(js.getString("placename"));
                        book.setTime(js.getString("time"));
                        book.setTime_order(js.getString("time_order"));
                        mData.add(book);
                    }
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                    BookAdapter adapter = new BookAdapter(mContext, mData,1);
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("结果为空");
                List<Book> mData2 = new ArrayList<>();
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                BookAdapter adapter = new BookAdapter(mContext, mData2,1);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setAdapter(adapter);
                Toast.makeText(mContext, "您还没有预定", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
