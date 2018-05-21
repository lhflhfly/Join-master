package com.lhf.join.View.Stadium;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.lhf.join.Adapter.GridAdapter;
import com.lhf.join.Adapter.GridIconAdapter;
import com.lhf.join.Adapter.MyViewPagerAdapter;
import com.lhf.join.Bean.App;
import com.lhf.join.Bean.Stadium;
import com.lhf.join.Bean.User;
import com.lhf.join.Fragment.OrderFragment;
import com.lhf.join.R;
import com.lhf.join.View.User.ProflieDialog;

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

import static com.lhf.join.Constant.Constant.URL_LOADINGICON;
import static com.lhf.join.Constant.Constant.URL_PICTURE;
import static com.lhf.join.Constant.Constant.URL_SPORTS;
import static com.lhf.join.Constant.Constant.URL_SPORTSTYPE;

public class StadiumIconActivity extends AppCompatActivity {
    private GridView gridView;
    private ImageView icon_back;
    private List<String> mData;
    private Stadium stadium;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stadium_icon);
        initView();
        initData();
    }

    private void initView() {
        gridView = findViewById(R.id.gv);
        icon_back = findViewById(R.id.icon_back);
        getWindow().setStatusBarColor(Color.parseColor("#FF029ACC"));
    }

    private void initData() {
        stadium = (Stadium) getIntent().getSerializableExtra("stadium");
        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LoadingIcon(stadium.getStadiumId());

    }

    private void LoadingIcon(int stadiumId) {
        String url = URL_LOADINGICON;
        new IconAsyncTask().execute(url,String.valueOf(stadiumId));

    }

    @SuppressLint("StaticFieldLeak")
    private class IconAsyncTask extends AsyncTask<String, Integer, String> {
        public IconAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json = new JSONObject();
            try {
                json.put("stadiumId", params[1]);
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
            mData = new ArrayList<>();
            if (!"null".equals(s)) {
                try {
                    JSONArray results = new JSONArray(s);
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject js = results.getJSONObject(i);
                        String icon = URL_PICTURE+js.getString("icon");
                        mData.add(icon);
                    }
                    GridIconAdapter gridIconAdapter = new GridIconAdapter(mData,StadiumIconActivity.this);
                    gridView.setAdapter(gridIconAdapter);
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ProflieDialog std = new ProflieDialog(mData.get(position));
                            std.show(getSupportFragmentManager(), "adaPicker");
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("结果为空");
                Toast.makeText(StadiumIconActivity.this, "获取失败", Toast.LENGTH_LONG).show();

            }
        }
    }
}
