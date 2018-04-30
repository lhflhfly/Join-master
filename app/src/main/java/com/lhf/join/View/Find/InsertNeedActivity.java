package com.lhf.join.View.Find;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lhf.join.Bean.Stadium;
import com.lhf.join.Bean.User;
import com.lhf.join.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.lhf.join.Constant.Constant.URL_INSERTNEED;
import static com.lhf.join.Constant.Constant.URL_ORDERSTADIUM;

public class InsertNeedActivity extends AppCompatActivity implements View.OnClickListener,SetStadiumDialog.SetStadiumListener,SetNumDialog.SetNumListener,SetTimeDialog.SetTimeListener {
    private Button btn_stadium;
    private Button btn_date;
    private Button btn_time;
    private User user;
    private Button btn_num;
    private Button btn_sumbit;
    private TextView tv_date;
    private TextView tv_time;
    private TextView tv_num;
    private TextView tv_stadiumname;
    private Stadium stadium_set;
    private String time_all;
    private String num_set;
    private EditText et_remark;
    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_need);
        initView();
        initData();
    }

    private void initView() {
        btn_stadium = findViewById(R.id.btn_stadium);
        tv_stadiumname = findViewById(R.id.tv_stadiumname);
        btn_date = findViewById(R.id.btn_date);
        btn_time = findViewById(R.id.btn_time);
        btn_num = findViewById(R.id.btn_num);
        btn_sumbit = findViewById(R.id.btn_submit);
        tv_num = findViewById(R.id.tv_num);
        tv_date = findViewById(R.id.tv_date);
        tv_time = findViewById(R.id.tv_time);
        et_remark = findViewById(R.id.et_remark);
        getWindow().setStatusBarColor(Color.parseColor("#FF029ACC"));
    }

    private void initData() {
        user = (User) getIntent().getSerializableExtra("user");
        btn_stadium.setOnClickListener(this);
        btn_date.setOnClickListener(this);
        btn_time.setOnClickListener(this);
        btn_num.setOnClickListener(this);
        btn_sumbit.setOnClickListener(this);

    }

    public void setDateClick(View v) {
        SetDateDialog sdt = new SetDateDialog();
        sdt.setTV(tv_date);
        sdt.show(getSupportFragmentManager(), "datePicker");
    }

    public void setTimeClick(View v) {
        SetTimeDialog std = new SetTimeDialog();
        std.show(getSupportFragmentManager(), "timePicker");
    }

    public void setPlaceClick(View v) {
        SetStadiumDialog std = new SetStadiumDialog();
        std.show(getSupportFragmentManager(), "adaPicker");
    }

    public void setNumClick(View v) {
        SetNumDialog std = new SetNumDialog();
        std.show(getSupportFragmentManager(), "numPicker");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_stadium:
                setPlaceClick(v);
                break;
            case R.id.btn_date:
                setDateClick(v);
                break;
            case R.id.btn_time:
                setTimeClick(v);
                break;
            case R.id.btn_num:
                setNumClick(v);
                break;
            case R.id.btn_submit:
                Calendar c =Calendar.getInstance();
                String remark = et_remark.getText().toString();
                String time = c.get(Calendar.YEAR)+"年"+(c.get(Calendar.MONTH)+1)+"月"+c.get(Calendar.DAY_OF_MONTH)+"日";
                time_all = tv_date.getText().toString()+tv_time.getText().toString();
                if(!("".equals(tv_stadiumname.getText().toString()))
                        && !(tv_date.getText().toString().equals(""))&& !(tv_num.getText().toString().equals(""))
                        && !(tv_num.getText().toString().equals(""))&& !(tv_time.getText().toString().equals(""))){
                    InsertNeed(user.getUserId(),stadium_set.getStadiumId(),time_all,num_set,stadium_set.getStadiumtype(),remark);
                }else {
                    Toast.makeText(this, "有选项为空！", Toast.LENGTH_SHORT).show();
                }

        }

    }

    private void InsertNeed(int userId, int stadiumId, String time,String num, String stadiumtype, String remark) {
        String orderURL = URL_INSERTNEED;
        new InsertNeedAsyncTask().execute(orderURL,String.valueOf(userId),String.valueOf(stadiumId),time,num,stadiumtype,remark);
    }

    private class InsertNeedAsyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json=new JSONObject();
            try {
                json.put("userId",params[1]);
                json.put("stadiumId",params[2]);
                json.put("time",params[3]);
                json.put("num",params[4]);
                json.put("stadiumtype",params[5]);
                json.put("remark",params[6]);
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
            if (!"".equals(s)){
                try {
                    JSONObject results = new JSONObject(s);
                    String loginresult = results.getString("result");
                    if(loginresult.equals("1")){
                        Toast.makeText(InsertNeedActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(InsertNeedActivity.this,"发布失败，请重试",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                System.out.println("结果为空");
            }
        }
    }

    @Override
    public void onSetStadiumComplete(Stadium stadium) {
        if (stadium == null) {
            Toast.makeText(InsertNeedActivity.this, "没有选场馆", Toast.LENGTH_SHORT).show();
        } else {
            stadium_set = stadium;
            tv_stadiumname.setText(stadium.getStadiumname().toString());
        }
    }

    @Override
    public void onSetPlaceComplete(int num1) {
        num_set = String.valueOf(num1);
        tv_num.setText(String.valueOf(num1)+"位");
    }

    @Override
    public void onSetNumLComplete(String time) {
        tv_time.setText(time);
    }

    @SuppressLint("ValidFragment")
    public static class SetDateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        private TextView tv;

        public void setTV(TextView tv) {
            this.tv = tv;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, year, month, day);
            dpd.getDatePicker().setMinDate((new Date()).getTime());
            dpd.getDatePicker().setMaxDate(new Date().getTime() + 3 * 24 * 60 * 60 * 1000);
            return dpd;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            tv.setText(year + "年" + (month + 1) + "月" + day + "日");
        }
    }

}