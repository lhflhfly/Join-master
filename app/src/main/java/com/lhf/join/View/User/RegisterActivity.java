package com.lhf.join.View.User;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import static com.lhf.join.Constant.Constant.URL_Register;

public class RegisterActivity extends AppCompatActivity {
    private EditText et_username;
    private EditText et_password;
    private EditText et_realname;
    private EditText et_tel;
    private Button btn_submit;
    private RadioGroup rg_sex;
    private RadioButton rb_male;
    private RadioButton rb_female;
    private RadioGroup rg_right;
    private RadioButton rb_user;
    private RadioButton rb_stadiumuser;
    private String sex;
    private ImageView icon_back;
    private String myright;


    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initData();

    }

    private void initView() {
        et_username = findViewById(R.id.et_username_register);
        et_password = findViewById(R.id.et_password_register);
        et_realname = findViewById(R.id.et_realname_register);
        et_tel = findViewById(R.id.et_tel);
        btn_submit = findViewById(R.id.submit);
        rg_sex = findViewById(R.id.rg_sex);
        rb_male = findViewById(R.id.rb_male);
        rb_female = findViewById(R.id.rb_female);
        icon_back = findViewById(R.id.icon_back);
        getWindow().setStatusBarColor(Color.parseColor("#FF029ACC"));

    }

    private void initData() {
        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rg_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_male:
                        sex = rb_male.getText().toString();
                        break;
                    case R.id.rb_female:
                        sex = rb_female.getText().toString();
                }
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(et_username.getText().toString().equals(""))
                        && !(et_password.getText().toString().equals(""))
                        && !(et_realname.getText().toString().equals(""))
                        && !(et_tel.getText().toString().equals(""))
                        && !"".equals(sex)
                        && !"".equals(myright)) {
                    Register(et_username.getText().toString(),
                            et_password.getText().toString(),
                            et_realname.getText().toString(),
                            sex,
                            et_tel.getText().toString(),
                            myright);
                } else {
                    Toast.makeText(v.getContext(), "每项必填，不能有空项", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    private void Register(String username, String password, String realname, String sex, String tel, String myright) {
        String registerUrl = URL_Register;
        new RegisterAsyncTask().execute(registerUrl, username, password, realname, sex, tel, myright);
    }

    private class RegisterAsyncTask extends AsyncTask<String, Integer, String> {
        public RegisterAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            User user = new User();
            JSONObject json = new JSONObject();
            try {
                json.put("username", params[1]);
                json.put("password", params[2]);
                json.put("realname", params[3]);
                json.put("sex", params[4]);
                json.put("tel", params[5]);
                json.put("myright", params[6]);
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
            if (!"".equals(s)) {
                try {
                    JSONObject results = new JSONObject(s);
                    String loginresult = results.getString("result");
                    if (loginresult.equals("1")) {
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                        new Handler(new Handler.Callback() {
                            //处理接收到的消息的方法
                            @Override
                            public boolean handleMessage(Message arg0) {
                                //实现页面跳转
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();
                                return false;
                            }
                        }).sendEmptyMessageDelayed(0, 2000);
                    } else {
                        Toast.makeText(RegisterActivity.this, "该用户名已注册", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("结果为空");
            }
        }
    }
}