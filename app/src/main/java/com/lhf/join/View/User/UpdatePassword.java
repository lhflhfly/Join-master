package com.lhf.join.View.User;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lhf.join.Bean.User;
import com.lhf.join.R;
import com.lhf.join.View.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.lhf.join.Constant.Constant.URL_LOGIN;
import static com.lhf.join.Constant.Constant.URL_UPDATEPASSWORD;

public class UpdatePassword extends AppCompatActivity {
    private EditText et_password_old;
    private EditText et_password_new;
    private EditText et_confire_password_new;
    private Button btn_submit;
    private ImageView icon_back;
    private User user;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        initView();
        initData();

    }

    private void initView() {
        et_password_old = findViewById(R.id.et_password_old);
        et_password_new = findViewById(R.id.et_password_new);
        et_confire_password_new = findViewById(R.id.et_confire_password_new);
        btn_submit = findViewById(R.id.btn_submit);
        icon_back = findViewById(R.id.icon_back);

    }

    private void initData() {
        user = (User) getIntent().getSerializableExtra("user");
        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(et_password_old.getText().toString().equals(""))
                        && !(et_password_new.getText().toString().equals(""))
                        && !(et_confire_password_new.getText().toString().equals(""))) {
                    if (user.getPassword().equals(et_password_old.getText().toString())) {
                        if (et_password_new.getText().toString().equals(et_confire_password_new.getText().toString())) {
                            updatePassword(user.getUserId(), et_password_new.getText().toString());
                        } else {
                            et_password_new.setText("");
                            et_confire_password_new.setText("");
                            Toast.makeText(UpdatePassword.this, "两次输入密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        et_password_old.setText("");
                        Toast.makeText(UpdatePassword.this, "旧密码输入错误.请重新输入", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(UpdatePassword.this, "每项不能为空", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void updatePassword(int userId, String password) {
        String loginUrl = URL_UPDATEPASSWORD;
        new UpdatePasswordAsyncTask().execute(loginUrl, String.valueOf(userId), password);
    }

    private class UpdatePasswordAsyncTask extends AsyncTask<String, Integer, String> {
        public UpdatePasswordAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json = new JSONObject();
            try {
                json.put("userId", params[1]);
                json.put("password", params[2]);
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
                        Toast.makeText(UpdatePassword.this, "修改成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(UpdatePassword.this, "修改失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("结果为空");
                Toast.makeText(UpdatePassword.this, "网络未连接", Toast.LENGTH_LONG).show();

            }
        }
    }
}
