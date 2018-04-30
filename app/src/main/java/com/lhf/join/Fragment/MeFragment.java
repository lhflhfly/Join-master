package com.lhf.join.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lhf.join.Base.BaseFragment;
import com.lhf.join.Bean.User;
import com.lhf.join.R;
import com.lhf.join.View.Collection.MyCollection;
import com.lhf.join.View.Find.MyJoinNeedActivity;
import com.lhf.join.View.User.NeedInformationActivity;
import com.lhf.join.View.User.LoginActivity;
import com.lhf.join.View.User.OrderInformationActivity;
import com.lhf.join.View.User.UserInformationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.lhf.join.Constant.Constant.URL_SELECTUSERBYUSERID;

public class MeFragment extends BaseFragment implements View.OnClickListener {
    private TextView tv_username;
    private User user;
    private LinearLayout btn_logout;
    private LinearLayout btn_user;
    private LinearLayout btn_order;
    private LinearLayout btn_find;
    private LinearLayout btn_collect;
    private LinearLayout btn_joinedneed;
    private String userId;
    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");




    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.first_me,null);
        tv_username = view.findViewById(R.id.tv_username);
        btn_logout = view.findViewById(R.id.btn_logout);
        btn_user = view.findViewById(R.id.btn_user);
        btn_order = view.findViewById(R.id.btn_order);
        btn_find = view.findViewById(R.id.btn_find);
        btn_collect = view.findViewById(R.id.btn_collect);
        btn_joinedneed = view.findViewById(R.id.btn_joinedneed);



        return view;


    }

    @Override
    public void onResume() {
        super.onResume();
        RefrshUser(userId);

    }

    @Override
    protected void initData() {
        user = (User) getActivity().getIntent().getSerializableExtra("user");
//        tv_username.setText("用户名："+user.getUsername());
        btn_logout.setOnClickListener(this);
        btn_user.setOnClickListener(this);
        btn_order.setOnClickListener(this);
        btn_find.setOnClickListener(this);
        btn_collect.setOnClickListener(this);
        btn_joinedneed.setOnClickListener(this);
        userId = String.valueOf(user.getUserId());
        RefrshUser(userId);



    }

    private void RefrshUser(String userId) {
        String loginUrl = URL_SELECTUSERBYUSERID;
        new RefrshUserAsyncTask().execute(loginUrl,userId);
    }

    private class RefrshUserAsyncTask extends AsyncTask<String, Integer, String> {
        public RefrshUserAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json=new JSONObject();
            try {
                json.put("userId",params[1]);
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
                    user = new User();
                    if(!"0".equals(loginresult)){
                        user.setUserId(results.getInt("userId"));
                        user.setUsername(results.getString("username"));
                        user.setPassword(results.getString("password"));
                        user.setRealname(results.getString("realname"));
                        user.setSex(results.getString("sex"));
                        user.setTel(results.getString("tel"));
                        user.setMyright(results.getString("myRight"));
                        tv_username.setText(user.getUsername());

                    }else{
                        Toast.makeText(mContext,"更新错误",Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                System.out.println("结果为空");
                Toast.makeText(mContext,"网络未连接",Toast.LENGTH_LONG).show();

            }
        }
    }

    @Override
    public void onClick(View v) {

        Bundle mBundle = new Bundle();
        switch (v.getId()){
            case R.id.btn_logout:
                submitlogout();
                break;
            case R.id.btn_user:
                Intent intent=new Intent(mContext,UserInformationActivity.class);
                mBundle.putSerializable("user",user);
                intent.putExtras(mBundle);
                startActivity(intent);
                break;
            case R.id.btn_order:
                Intent intent1=new Intent(mContext,OrderInformationActivity.class);
                mBundle.putSerializable("user",user);
                intent1.putExtras(mBundle);
                startActivity(intent1);
                break;
            case R.id.btn_find:
                Intent intent2=new Intent(mContext,NeedInformationActivity.class);
                mBundle.putSerializable("user",user);
                intent2.putExtras(mBundle);
                startActivity(intent2);
                break;
            case R.id.btn_collect:
                Intent intent3=new Intent(mContext,MyCollection.class);
                mBundle.putSerializable("user",user);
                intent3.putExtras(mBundle);
                startActivity(intent3);
                break;
            case R.id.btn_joinedneed:
                Intent intent4=new Intent(mContext,MyJoinNeedActivity.class);
                mBundle.putSerializable("user",user);
                intent4.putExtras(mBundle);
                startActivity(intent4);
                break;



        }

    }

    private void submitlogout(){
        AlertDialog.Builder submit = new AlertDialog.Builder(mContext);
        submit.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        submit.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        submit.setMessage("确认退出当前用户？");
        submit.setTitle("提示");
        submit.show();
    }
}
