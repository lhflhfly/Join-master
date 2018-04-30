package com.lhf.join.View.User;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lhf.join.Adapter.BookAdapter;
import com.lhf.join.Adapter.StadiumAdapter;
import com.lhf.join.Bean.App;
import com.lhf.join.Bean.Book;
import com.lhf.join.Bean.Notice;
import com.lhf.join.Bean.Stadium;
import com.lhf.join.Bean.User;
import com.lhf.join.Fragment.OrderFragment;
import com.lhf.join.R;

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

import static com.lhf.join.Constant.Constant.URL_LOADINGORDER;
import static com.lhf.join.Constant.Constant.URL_NOTICE;
import static com.lhf.join.Constant.Constant.URL_ORDERINFORMATION;
import static com.lhf.join.Constant.Constant.URL_PICTURE;

public class OrderInformationActivity extends AppCompatActivity {
    private RecyclerView rv_orderinformation;
    private LinearLayoutManager layoutManager;
    private TextView tv_noorder;
    private ImageView icon_back;
    private List<Book> mData = null;
    private User user;
    Book book = new Book();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_information);
        initview();
        initdata();
    }

    private void initview() {
        user = (User) getIntent().getSerializableExtra("user");
        rv_orderinformation = findViewById(R.id.rv_orderinformation);
        tv_noorder = findViewById(R.id.tv_noorder);
        layoutManager = new LinearLayoutManager(this);
        icon_back = findViewById(R.id.icon_back);
        getWindow().setStatusBarColor(Color.parseColor("#FF029ACC"));


    }

    private void initdata() {
        orderInformation(user);
        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void orderInformation(User user) {
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
            JSONObject json = new JSONObject();
            try {
                json.put("userId", params[1]);
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
                    rv_orderinformation.setLayoutManager(layoutManager);
                    rv_orderinformation.addItemDecoration(new DividerItemDecoration(OrderInformationActivity.this, DividerItemDecoration.VERTICAL));
                    BookAdapter adapter = new BookAdapter(OrderInformationActivity.this, mData);
                    rv_orderinformation.setNestedScrollingEnabled(false);
                    rv_orderinformation.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("结果为空");
                List<Book> mData2 = new ArrayList<>();
                tv_noorder.setText("当前没有预定");
                rv_orderinformation.setLayoutManager(layoutManager);
                rv_orderinformation.addItemDecoration(new DividerItemDecoration(OrderInformationActivity.this, DividerItemDecoration.VERTICAL));
                BookAdapter adapter = new BookAdapter(OrderInformationActivity.this, mData2);
                rv_orderinformation.setNestedScrollingEnabled(false);
                rv_orderinformation.setAdapter(adapter);
                Toast.makeText(OrderInformationActivity.this, "您还没有预定", Toast.LENGTH_SHORT).show();

            }
        }
    }

}
