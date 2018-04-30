package com.lhf.join.View.Find;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.lhf.join.Adapter.FindAdapter;
import com.lhf.join.Adapter.NeedAdapter;
import com.lhf.join.Adapter.StadiumAdapter;
import com.lhf.join.Bean.Need;
import com.lhf.join.Bean.Stadium;
import com.lhf.join.Bean.User;
import com.lhf.join.R;
import com.lhf.join.View.Collection.MyCollection;

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

import static com.lhf.join.Constant.Constant.URL_FINDINFORMATION;
import static com.lhf.join.Constant.Constant.URL_PICTURE;
import static com.lhf.join.Constant.Constant.URL_SEARCHCOLLECTSTADIUM;

public class MyJoinNeedActivity extends AppCompatActivity {
    private User user;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ImageView btn_back;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_join_need);
        initView();
        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    private void initView() {
        btn_back = findViewById(R.id.icon_back);
        recyclerView = findViewById(R.id.rv_joinedneed);
        layoutManager = new LinearLayoutManager(this);
        getWindow().setStatusBarColor(Color.parseColor("#FF029ACC"));

    }

    private void initData() {
        user = (User) getIntent().getSerializableExtra("user");
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        joinedneed(user.getUserId());
    }

    private void joinedneed(int userId) {
        String SearchUrl = URL_FINDINFORMATION;
        new joinedneedAsyncTask().execute(SearchUrl, String.valueOf(userId));
    }

    private class joinedneedAsyncTask extends AsyncTask<String, Integer, String> {
        public joinedneedAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            int method = 2;
            JSONObject json = new JSONObject();
            try {
                json.put("userId", params[1]);
                json.put("method",method);
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
            System.out.println("返回的数据："+s);
            List<Need> mData = new ArrayList<>();
            if (!"null".equals(s)){
                try {
                    JSONArray results = new JSONArray(s);
                    for(int i=results.length()-1;i>=0;i--){
                        JSONObject js= results.getJSONObject(i);
                        Need need = new Need();
                        need.setNeedId(js.getInt("needId"));
                        need.setUserId(js.getInt("userId"));
                        need.setUsername(js.getString("username"));
                        need.setStadiumname(js.getString("stadiumname"));
                        need.setTime(js.getString("time"));
                        need.setNum(js.getInt("num"));
                        need.setNum_join(js.getInt("num_join"));
                        need.setRemark(js.getString("remark"));
                        mData.add(need);
                    }
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.addItemDecoration(new DividerItemDecoration(MyJoinNeedActivity.this,DividerItemDecoration.VERTICAL));
                    FindAdapter adapter = new FindAdapter(MyJoinNeedActivity.this,mData,user,false);
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                System.out.println("结果为空");
                List<Need> mData2 = new ArrayList<>();
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.addItemDecoration(new DividerItemDecoration(MyJoinNeedActivity.this,DividerItemDecoration.VERTICAL));
                NeedAdapter adapter = new NeedAdapter(MyJoinNeedActivity.this,mData2);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setAdapter(adapter);
            }
        }
    }
}
