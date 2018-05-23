package com.lhf.join.View.Find;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lhf.join.Adapter.FindAdapter;
import com.lhf.join.Adapter.JoinedUserAdapter;
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


import static com.lhf.join.Constant.Constant.URL_JOINEDUSERINFORMATION;
import static com.lhf.join.Constant.Constant.URL_PROFLIE;


public class MyFindInformationActivity extends AppCompatActivity {
    private ImageView icon_back;
    private TextView tv_nojoin;
    private RecyclerView rv_joineduser;
    private LinearLayoutManager layoutManager;
    private Need need;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_find_information);
        initView();
        initData();
    }

    private void initView() {
        icon_back = findViewById(R.id.icon_back);
        tv_nojoin = findViewById(R.id.tv_nojoin);
        rv_joineduser = findViewById(R.id.rv_joineduser);
        layoutManager = new LinearLayoutManager(this);
    }

    private void initData() {
        need = (Need)getIntent().getSerializableExtra("need");
        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        joineduser(need.getNeedId());
    }

    private void joineduser(int needId) {
        String SearchUrl = URL_JOINEDUSERINFORMATION;
        new joineduserAsyncTask().execute(SearchUrl, String.valueOf(needId));
    }

    private class joineduserAsyncTask extends AsyncTask<String, Integer, String> {
        public joineduserAsyncTask() {
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
            System.out.println("返回的数据："+s);
            List<User> mData = new ArrayList<>();
            if (!"null".equals(s)){
                try {
                    JSONArray results = new JSONArray(s);
                    for(int i=results.length()-1;i>=0;i--){
                        JSONObject js= results.getJSONObject(i);
                        User user = new User();
                        user.setUsername(js.getString("username"));
                        user.setSex(js.getString("sex"));
                        user.setTel(js.getString("tel"));
                        user.setProflie(URL_PROFLIE+js.optString("userproflie"));
                        mData.add(user);
                    }
                    rv_joineduser.setLayoutManager(layoutManager);
                    rv_joineduser.addItemDecoration(new DividerItemDecoration(MyFindInformationActivity.this,DividerItemDecoration.VERTICAL));
                    JoinedUserAdapter adapter = new JoinedUserAdapter(MyFindInformationActivity.this,mData);
                    rv_joineduser.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                System.out.println("结果为空");
                List<Need> mData2 = new ArrayList<>();
                tv_nojoin.setVisibility(View.VISIBLE);
                rv_joineduser.setLayoutManager(layoutManager);
                rv_joineduser.addItemDecoration(new DividerItemDecoration(MyFindInformationActivity.this,DividerItemDecoration.VERTICAL));
                NeedAdapter adapter = new NeedAdapter(MyFindInformationActivity.this,mData2);
                rv_joineduser.setNestedScrollingEnabled(false);
                rv_joineduser.setAdapter(adapter);
            }
        }
    }
}
