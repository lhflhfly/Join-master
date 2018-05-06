package com.lhf.join.View.User;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.lhf.join.Constant.Constant.URL_LOGIN;
import static com.lhf.join.Constant.Constant.URL_PROFLIE;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_username;
    private EditText et_password;
    private TextView btn_register;
    private Button btn_login;
    private ImageView ivDelete;
    private ImageView ivDelete2;
    private CheckBox rememberPass;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_login);
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                MY_PERMISSION_REQUEST_CODE);
        initView();
        initData();

    }

    private void initView() {
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_register = findViewById(R.id.btn_register);
        ivDelete = (ImageView) findViewById(R.id.imb_search_clear);
        ivDelete2 = (ImageView) findViewById(R.id.imb_search_clear1);
        btn_login = findViewById(R.id.btn_login);
        rememberPass=findViewById(R.id.save_pass);

    }

    private void initData() {
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        ivDelete2.setOnClickListener(this);
        et_username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(!"".equals(et_username.getText().toString())){
                        ivDelete.setVisibility(VISIBLE);
                        et_username.addTextChangedListener(new EditChangedListener());
                    }else {
                        ivDelete.setVisibility(GONE);
                        et_username.addTextChangedListener(new EditChangedListener());
                    }

                }
                else {
                    ivDelete.setVisibility(GONE);
                }
            }
        });
        et_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(!"".equals(et_password.getText().toString())){
                        ivDelete2.setVisibility(VISIBLE);
                        et_password.addTextChangedListener(new EditChangedListener1());
                    }else {
                        ivDelete2.setVisibility(GONE);
                        et_password.addTextChangedListener(new EditChangedListener1());
                    }
                }
                else {
                    ivDelete2.setVisibility(GONE);
                }
            }
        });
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemenber=pref.getBoolean("remember_password",false);
        if(isRemenber){
            //将账号和密码都设置到文本中
            String account=pref.getString("account","");
            String password=pref.getString("password","");
            et_username.setText(account);
            et_password.setText(password);
            rememberPass.setChecked(true);

        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (!("".equals(et_username.getText().toString()))
                        && !(et_password.getText().toString().equals(""))) {
                    Login(et_username.getText().toString(), et_password.getText().toString());
                } else {
                    Toast.makeText(this, "账号、密码都不能为空！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_register:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.imb_search_clear:
                et_username.setText("");
                ivDelete.setVisibility(GONE);
                break;
            case R.id.imb_search_clear1:
                et_password.setText("");
                ivDelete.setVisibility(GONE);
                break;

        }
    }

    private class EditChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (!"".equals(charSequence.toString())) {
                ivDelete.setVisibility(VISIBLE);
                //更新autoComplete数据
            } else {
                ivDelete.setVisibility(GONE);
            }

        }
        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private class EditChangedListener1 implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (!"".equals(charSequence.toString())) {
                ivDelete2.setVisibility(VISIBLE);
                //更新autoComplete数据
            } else {
                ivDelete2.setVisibility(GONE);
            }

        }
        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private void Login(String username, String password) {
        String loginUrl = URL_LOGIN;
        new LoginAsyncTask().execute(loginUrl, username, password);
    }

    private class LoginAsyncTask extends AsyncTask<String, Integer, String> {
        public LoginAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json = new JSONObject();
            try {
                json.put("username", params[1]);
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
                    User user = new User();
                    if (!"0".equals(loginresult)) {
                        editor=pref.edit();
                        if(rememberPass.isChecked()){
                            editor.putBoolean("remember_password",true);
                            editor.putString("account",results.getString("username"));
                            editor.putString("password",results.getString("password"));
                        }else {
                            editor.clear();
                        }
                        editor.apply();
                        user.setUserId(results.getInt("userId"));
                        user.setUsername(results.getString("username"));
                        user.setPassword(results.getString("password"));
                        user.setRealname(results.getString("realname"));
                        user.setSex(results.getString("sex"));
                        user.setTel(results.getString("tel"));
                        user.setMyright(results.getString("myRight"));
                        user.setProflie(URL_PROFLIE+results.optString("proflie"));
                        Toast.makeText(LoginActivity.this, "正在登陆，请稍后", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable("user", user);
                        intent.putExtras(mBundle);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("结果为空");
                Toast.makeText(LoginActivity.this, "网络未连接", Toast.LENGTH_LONG).show();

            }
        }
    }
}