package com.lhf.join.View.Find;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lhf.join.Bean.Need;
import com.lhf.join.Bean.User;
import com.lhf.join.R;
import com.lhf.join.View.User.UpdatePassword;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.lhf.join.Constant.Constant.URL_FINDINFORMATION;
import static com.lhf.join.Constant.Constant.URL_JOINFIND;
import static com.lhf.join.Constant.Constant.URL_UPDATEPASSWORD;

public class FindActivity extends AppCompatActivity {
    private TextView tv_username;
    private TextView tv_stadiumname;
    private TextView tv_time;
    private TextView tv_num;
    private TextView tv_num_join;
    private TextView tv_remark;
    private Button btn_join;
    private User user;
    private User user1;
    private Need need;
    private ImageView icon_back;
    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        initView();
        initData();
    }

    private void initView() {
        tv_username = findViewById(R.id.tv_username);
        tv_stadiumname = findViewById(R.id.tv_stadiumname);
        tv_time = findViewById(R.id.tv_time);
        tv_num = findViewById(R.id.tv_num);
        tv_num_join = findViewById(R.id.tv_num_join);
        tv_remark = findViewById(R.id.tv_remark);
        btn_join =findViewById(R.id.btn_join);
        icon_back= findViewById(R.id.icon_back);
    }

    private void initData() {
        user = (User) getIntent().getSerializableExtra("user");
        need = (Need) getIntent().getSerializableExtra("need");
        tv_username.setText(need.getUsername());
        tv_stadiumname.setText("地点："+need.getStadiumname());
        tv_time.setText("时间："+need.getTime());
        tv_num.setText("召集人数："+String.valueOf(need.getNum()));
        tv_num_join.setText("已加入人数："+String.valueOf(need.getNum_join()));
        tv_remark.setText("备注："+need.getRemark());
        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findJoin(user.getUserId(),need.getNeedId());
            }
        });


    }

    private void findJoin(int userId,int needId) {
        String loginUrl = URL_JOINFIND;
        new findJoinAsyncTask().execute(loginUrl,String.valueOf(userId),String.valueOf(needId));
    }

    private class findJoinAsyncTask extends AsyncTask<String, Integer, String> {
        public findJoinAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json=new JSONObject();
            try {
                json.put("userId",params[1]);
                json.put("needId",params[2]);
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
                Request request = new Request.Builder()
                        .url(params[0])
                        .post(requestBody)
                        .build();
                response=okHttpClient.newCall(request).execute();
                results=response.body().string();
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
            if (s != null){
                try {
                    JSONObject results = new JSONObject(s);
                    String loginresult = results.getString("result");
                    System.out.println("22");
                    System.out.println(loginresult);
                    if("1".equals(loginresult)){
                        Toast.makeText(FindActivity.this,"加入成功",Toast.LENGTH_SHORT).show();
                        finish();
                    }else if("2".equals(loginresult)){
                        Toast.makeText(FindActivity.this,"人数已满，加入失败",Toast.LENGTH_SHORT).show();
                    }else if("3".equals(loginresult)){
                        Toast.makeText(FindActivity.this,"已经加入，不能重复加入",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(FindActivity.this,"加入失败" ,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                System.out.println("结果为空");
                Toast.makeText(FindActivity.this,"网络未连接",Toast.LENGTH_LONG).show();

            }
        }
    }


}
