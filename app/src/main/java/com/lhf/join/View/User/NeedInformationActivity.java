package com.lhf.join.View.User;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lhf.join.Adapter.NeedAdapter;
import com.lhf.join.Bean.Need;
import com.lhf.join.Bean.User;
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

import static com.lhf.join.Constant.Constant.URL_NEEDINFORMATION;

public class NeedInformationActivity extends AppCompatActivity {
    private RecyclerView rv_orderinformation;
    private LinearLayoutManager layoutManager;
    private List<Need> mData = null;
    private TextView tv_noneed;
    private ImageView icon_back;
    private User user;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_information);
        initview();
        initdata();
    }

    private void initview() {
        user = (User) getIntent().getSerializableExtra("user");
        rv_orderinformation = findViewById(R.id.rv_orderinformation);
        tv_noneed = findViewById(R.id.tv_noneed);
        layoutManager = new LinearLayoutManager(this);
        icon_back = findViewById(R.id.icon_back);
        getWindow().setStatusBarColor(Color.parseColor("#FF029ACC"));


    }

    private void initdata() {
        needInformation(user);
        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void needInformation(User user) {
        String loadingUrl = URL_NEEDINFORMATION;
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
            List<Need> mData = new ArrayList<>();
            if (!"null".equals(s)) {
                try {
                    JSONArray results = new JSONArray(s);
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject js = results.getJSONObject(i);
                        Need need = new Need();
                        need.setNeedId(js.getInt("needId"));
                        need.setStadiumname(js.getString("stadiumname"));
                        need.setTime(js.getString("time"));
                        need.setNum(js.getInt("num"));
                        need.setNum_join(js.getInt("num_join"));
                        need.setRemark(js.getString("remark"));
                        mData.add(need);
                    }
                    rv_orderinformation.setLayoutManager(layoutManager);
                    rv_orderinformation.addItemDecoration(new DividerItemDecoration(NeedInformationActivity.this, DividerItemDecoration.VERTICAL));
                    NeedAdapter adapter = new NeedAdapter(NeedInformationActivity.this, mData);
                    rv_orderinformation.setNestedScrollingEnabled(false);
                    rv_orderinformation.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("结果为空");
                tv_noneed.setText("当前没有发布信息");
                List<Need> mData2 = new ArrayList<>();
                rv_orderinformation.setLayoutManager(layoutManager);
                rv_orderinformation.addItemDecoration(new DividerItemDecoration(NeedInformationActivity.this, DividerItemDecoration.VERTICAL));
                NeedAdapter adapter = new NeedAdapter(NeedInformationActivity.this, mData2);
                rv_orderinformation.setNestedScrollingEnabled(false);
                rv_orderinformation.setAdapter(adapter);
                Toast.makeText(NeedInformationActivity.this, "您还没有发布信息", Toast.LENGTH_SHORT).show();

            }
        }
    }

}
