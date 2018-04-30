package com.lhf.join.View.User;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
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

import static com.lhf.join.Constant.Constant.URL_UpdateUser;

public class UpdateUserActivity extends AppCompatActivity {
    private EditText et_username;
    private EditText et_realname;
    private EditText et_tel;
    private Button btn_submit;
    private RadioGroup rg_sex;
    private RadioButton rb_male;
    private RadioButton rb_female;
    private String sex;
    private String lastname;
    private User user;
    private ImageView icon_back;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateuser);
        initView();
        initData();

    }

    private void initData() {
        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        user = (User) getIntent().getSerializableExtra("user");
        et_username.setText(user.getUsername());
        lastname = user.getUsername();
        et_realname.setText(user.getRealname());
        et_tel.setText(user.getTel());
        if ("男".equals(user.getSex())) {
            rg_sex.check(R.id.rb_male);
            sex = rb_male.getText().toString();
        } else {
            rg_sex.check(R.id.rb_female);
            sex = rb_female.getText().toString();
        }
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
                String userId = String.valueOf(user.getUserId());
                if (!(et_username.getText().toString().equals(""))
                        && !(et_realname.getText().toString().equals(""))
                        && !(et_tel.getText().toString().equals(""))
                        && !"".equals(sex)) {
                    UpdateUser(
                            userId,
                            et_username.getText().toString(),
                            lastname,
                            et_realname.getText().toString(),
                            sex,
                            et_tel.getText().toString());
                } else {
                    Toast.makeText(v.getContext(), "每项必填，不能有空项", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    private void initView() {
        et_username = findViewById(R.id.et_username_register);
        et_realname = findViewById(R.id.et_realname_register);
        et_tel = findViewById(R.id.et_tel);
        btn_submit = findViewById(R.id.submit);
        rg_sex = findViewById(R.id.rg_sex);
        rb_male = findViewById(R.id.rb_male);
        rb_female = findViewById(R.id.rb_female);
        icon_back = findViewById(R.id.icon_back);
        getWindow().setStatusBarColor(Color.parseColor("#FF029ACC"));
    }


    private void UpdateUser(String userId, String username, String lastname, String realname, String sex, String tel) {
        String updateUserUrl = URL_UpdateUser;
        new UpdateUserAsyncTask().execute(updateUserUrl, userId, username, lastname, realname, sex, tel);
    }

    private class UpdateUserAsyncTask extends AsyncTask<String, Integer, String> {
        public UpdateUserAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json = new JSONObject();
            try {
                json.put("userId", params[1]);
                json.put("username", params[2]);
                json.put("lastname", params[3]);
                json.put("realname", params[4]);
                json.put("sex", params[5]);
                json.put("tel", params[6]);
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
                    User user = new User();
                    if (loginresult.equals("1")) {
                        user.setUserId(results.getInt("userId"));
                        user.setUsername(results.getString("username"));
                        user.setPassword(results.getString("password"));
                        user.setRealname(results.getString("realname"));
                        user.setSex(results.getString("sex"));
                        user.setTel(results.getString("tel"));
                        user.setMyright(results.getString("myRight"));
                        Toast.makeText(UpdateUserActivity.this, "修改成功", Toast.LENGTH_LONG).show();

//                        Intent intent=new Intent(UpdateUserActivity.this,MainActivity.class);
//                        Bundle mBundle = new Bundle();
//                        mBundle.putSerializable("user",user);
//                        intent.putExtras(mBundle);
//                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(UpdateUserActivity.this, "该用户名已注册", Toast.LENGTH_LONG).show();
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