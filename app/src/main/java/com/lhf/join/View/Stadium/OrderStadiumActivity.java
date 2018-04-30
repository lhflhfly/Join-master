package com.lhf.join.View.Stadium;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.lhf.join.Bean.Stadium;
import com.lhf.join.Bean.User;
import com.lhf.join.Fragment.OrderFragment;
import com.lhf.join.R;
import com.lhf.join.View.User.LoginActivity;
import com.lhf.join.View.User.RegisterActivity;
import com.lhf.join.View.User.UpdateUserActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.lhf.join.Constant.Constant.URL_ORDERSTADIUM;

public class OrderStadiumActivity extends AppCompatActivity implements View.OnClickListener, SetPlaceDialog.SetPlaceListener {
    private Button btn_date;
    private Button btn_time;
    private Button btn_place;
    private TextView tv_date;
    private TextView tv_time;
    private TextView tv_place;
    private TextView tv_userId;
    private Button btn_submit;
    private User user;
    private Stadium stadium;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_stadium);
        initView();
        initData();

    }

    private void initView() {
        btn_date = findViewById(R.id.btn_date);
        btn_time = findViewById(R.id.btn_time);
        btn_place = findViewById(R.id.btn_place);
        tv_date = findViewById(R.id.tv_date);
        tv_userId = findViewById(R.id.tv_userId);
        tv_time = findViewById(R.id.tv_time);
        tv_place = findViewById(R.id.tv_place);
        btn_submit = findViewById(R.id.btn_submit);
        getWindow().setStatusBarColor(Color.parseColor("#FF029ACC"));


    }

    private void initData() {
        user = (User) getIntent().getSerializableExtra("user");
        tv_userId.setText("userID:" + user.getUserId());
        stadium = (Stadium) getIntent().getSerializableExtra("stadium");
        btn_date.setOnClickListener(this);
        btn_time.setOnClickListener(this);
        btn_place.setOnClickListener(this);
        btn_submit.setOnClickListener(this);


    }

    public void setDateClick(View v) {
        SetDateDialog sdt = new SetDateDialog();
        sdt.setTV(tv_date);
        sdt.show(getSupportFragmentManager(), "datePicker");
    }

    public void setTimeClick(View v) {
        SetTimeDialog std = new SetTimeDialog();
        std.setTV(tv_time);
        std.show(getSupportFragmentManager(), "timePicker");
    }

    public void setPlaceClick(View v) {
        SetPlaceDialog std = new SetPlaceDialog(stadium);
        std.show(getSupportFragmentManager(), "adaPicker");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_date:
                setDateClick(v);
                break;
            case R.id.btn_time:
                setTimeClick(v);
                break;
            case R.id.btn_place:
                setPlaceClick(v);
                break;
            case R.id.btn_submit:
                if (!"日期".equals(tv_date.getText().toString()) && !"时间".equals(tv_time.getText().toString()) && !"场地".equals(tv_place.getText().toString())) {
                    Calendar c = Calendar.getInstance();
                    String time = c.get(Calendar.YEAR) + "年" + (c.get(Calendar.MONTH) + 1) + "月" + c.get(Calendar.DAY_OF_MONTH) + "日";
                    String time_order = tv_date.getText().toString() + tv_time.getText().toString();

                    OrderStadium(user.getUserId(), stadium.getStadiumId(), time, time_order, tv_place.getText().toString(), user.getTel());


                } else {
                    Toast.makeText(OrderStadiumActivity.this, "您有未输入的内容", Toast.LENGTH_LONG).show();
                }
        }
    }

    private void OrderStadium(int userId, int stadiumId, String time, String time_order, String s, String tel) {
        String orderURL = URL_ORDERSTADIUM;
        new OrderStadiumAsyncTask().execute(orderURL, String.valueOf(userId), String.valueOf(stadiumId), time, time_order, s, tel);
    }

    private class OrderStadiumAsyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json = new JSONObject();
            try {
                json.put("userId", params[1]);
                json.put("stadiumId", params[2]);
                json.put("time", params[3]);
                json.put("time_order", params[4]);
                json.put("placename", params[5]);
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
                    if (loginresult.equals("1")) {
                        Toast.makeText(OrderStadiumActivity.this, "预约成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OrderStadiumActivity.this, "预约失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("结果为空");
            }
        }
    }

    @Override
    public void onSetPlaceComplete(String place1) {
        tv_place.setText(place1);
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

    public static class SetTimeDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        private TextView tv;

        public void setTV(TextView tv) {
            this.tv = tv;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            TimePickerDialog dpd = new TimePickerDialog(getActivity(), this, hour, minute, true);
            return dpd;
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            tv.setText(hourOfDay + ":" + minute);

        }

    }


}
