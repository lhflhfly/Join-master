package com.lhf.join.View.User;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lhf.join.Bean.User;
import com.lhf.join.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.lhf.join.Constant.Constant.URL_SELECTUSERBYUSERID;

public class UserInformationActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_username;
    private TextView tv_realname;
    private TextView tv_sex;
    private TextView tv_tel;
    private User user;
    private ImageView icon_back;
    private Button btn_update;
    private Button btn_password;
    private String userId;


    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initView();
        initData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        initData();
    }

    private void initView() {
        tv_username = findViewById(R.id.tv_username);
        tv_realname = findViewById(R.id.tv_realname);
        tv_sex = findViewById(R.id.tv_sex);
        tv_tel = findViewById(R.id.tv_tel);
        btn_update = findViewById(R.id.btn_update);
        btn_password = findViewById(R.id.btn_password);
        icon_back = findViewById(R.id.icon_back);
        getWindow().setStatusBarColor(Color.parseColor("#FF029ACC"));


    }

    private void initData() {
        user = (User) getIntent().getSerializableExtra("user");
        userId = String.valueOf(user.getUserId());
        RefrshUser(userId);
        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        tv_username.setText(user.getUsername());
//        tv_realname.setText(user.getRealname());
//        tv_sex.setText(user.getSex());
//        tv_tel.setText(user.getTel());
//        tv_myright.setText(user.getMyright());
        btn_update.setOnClickListener(this);
        btn_password.setOnClickListener(this);


    }

    private void RefrshUser(String userId) {
        String loginUrl = URL_SELECTUSERBYUSERID;
        new RefrshUserAsyncTask().execute(loginUrl, userId);
    }

    private class RefrshUserAsyncTask extends AsyncTask<String, Integer, String> {
        public RefrshUserAsyncTask() {
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
            System.out.println(s);
            if (s != null) {
                try {
                    JSONObject results = new JSONObject(s);
                    String loginresult = results.getString("result");
                    System.out.println("22");
                    System.out.println(loginresult);
                    if (!"0".equals(loginresult)) {
                        user.setUserId(results.getInt("userId"));
                        user.setUsername(results.getString("username"));
                        user.setPassword(results.getString("password"));
                        user.setRealname(results.getString("realname"));
                        user.setSex(results.getString("sex"));
                        user.setTel(results.getString("tel"));
                        user.setMyright(results.getString("myRight"));
                        tv_username.setText(user.getUsername());
                        tv_realname.setText(user.getRealname());
                        tv_sex.setText(user.getSex());
                        tv_tel.setText(user.getTel());
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("结果为空");
                Toast.makeText(UserInformationActivity.this, "网络未连接", Toast.LENGTH_LONG).show();

            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                Intent intent = new Intent(UserInformationActivity.this, UpdateUserActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("user", user);
                intent.putExtras(mBundle);
                startActivity(intent);
                break;
            case R.id.btn_password:
                Intent intent1 = new Intent(UserInformationActivity.this, UpdatePassword.class);
                Bundle mBundle1 = new Bundle();
                mBundle1.putSerializable("user", user);
                intent1.putExtras(mBundle1);
                startActivity(intent1);
                break;


        }
    }
}