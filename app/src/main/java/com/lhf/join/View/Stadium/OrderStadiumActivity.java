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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lhf.join.Bean.Place;
import com.lhf.join.Bean.Stadium;
import com.lhf.join.Bean.User;
import com.lhf.join.Fragment.OrderFragment;
import com.lhf.join.R;
import com.lhf.join.View.Find.InsertNeedActivity;
import com.lhf.join.View.Find.SetTimeDialog;
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

public class OrderStadiumActivity extends AppCompatActivity implements View.OnClickListener, SetPlaceDialog.SetPlaceListener, SetTimeDialog.SetTimeListener {
    private Button btn_date;
    private Button btn_time;
    private Button btn_place;
    private TextView tv_date;
    private TextView tv_time;
    private TextView tv_place;
    private ImageView icon_back;
    private Button btn_submit;
    private User user;
    private Stadium stadium;
    private Place place;
    private String time_order;
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
        tv_time = findViewById(R.id.tv_time);
        tv_place = findViewById(R.id.tv_place);
        btn_submit = findViewById(R.id.btn_submit);
        icon_back = findViewById(R.id.icon_back);
        getWindow().setStatusBarColor(Color.parseColor("#FF029ACC"));


    }

    private void initData() {
        user = (User) getIntent().getSerializableExtra("user");
        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        time_order = tv_date.getText().toString() + tv_time.getText().toString();
    }

    public void setTimeClick(View v) {
        SetTimeDialog std = new SetTimeDialog(stadium, tv_date.getText().toString());
        std.show(getSupportFragmentManager(), "timePicker");

    }

    public void setPlaceClick(View v) {
        SetPlaceDialog std = new SetPlaceDialog(stadium, time_order);
        std.show(getSupportFragmentManager(), "adaPicker");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_date:
                setDateClick(v);
                break;
            case R.id.btn_time:
                if ("".equals(tv_date.getText().toString())) {
                    Toast.makeText(OrderStadiumActivity.this, "请先选择日期", Toast.LENGTH_SHORT).show();
                } else {
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    String this_day = (year + "年" + (month + 1) + "月" + day + "日");
                    if (this_day.equals(tv_date.getText().toString())) {
                        int time_this = calendar.get(Calendar.HOUR_OF_DAY);
                        if (time_this > Integer.parseInt(stadium.getClosetime())) {
                            Toast.makeText(OrderStadiumActivity.this, "该场馆今日已休息，请选择其他日期", Toast.LENGTH_SHORT).show();
                        } else {
                            setTimeClick(v);
                        }
                    } else {
                        setTimeClick(v);
                    }
                }
                break;
            case R.id.btn_place:
                if ("".equals(tv_time.getText().toString()) || "".equals(tv_date.getText().toString())) {
                    Toast.makeText(OrderStadiumActivity.this, "请先选择日期和时间", Toast.LENGTH_SHORT).show();
                } else {
                    setPlaceClick(v);
                }
                break;
            case R.id.btn_submit:
                if (!"".equals(tv_date.getText().toString()) && !"".equals(tv_time.getText().toString()) && !"".equals(tv_place.getText().toString())) {
                    Calendar c = Calendar.getInstance();
                    String time = c.get(Calendar.YEAR) + "年" + (c.get(Calendar.MONTH) + 1) + "月" + c.get(Calendar.DAY_OF_MONTH) + "日";
                    time_order = tv_date.getText().toString() + tv_time.getText().toString();

                    OrderStadium(user.getUserId(), stadium.getStadiumId(), time, time_order, String.valueOf(place.getPlaceId()), user.getTel());


                } else {
                    Toast.makeText(OrderStadiumActivity.this, "您有未输入的内容", Toast.LENGTH_LONG).show();
                }
        }
    }

    private void OrderStadium(int userId, int stadiumId, String time, String time_order, String s, String tel) {
        String orderURL = URL_ORDERSTADIUM;
        new OrderStadiumAsyncTask().execute(orderURL, String.valueOf(userId), String.valueOf(stadiumId), time, time_order, s, tel);
    }

    @Override
    public void onSetNumLComplete(String time) {
        tv_time.setText(time);
        time_order = tv_date.getText().toString() + tv_time.getText().toString();
    }

    @Override
    public void onSetPlaceComplete(Place place_set) {
        if (place_set == null) {
            Toast.makeText(OrderStadiumActivity.this, "没有选场地", Toast.LENGTH_SHORT).show();
        } else {
            place = place_set;
            tv_place.setText(place_set.getPlacename());
        }
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
                json.put("placeId", params[5]);
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
                        finish();
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
            DatePickerDialog dpd = new DatePickerDialog(getActivity(), R.style.ThemeDialog, this, year, month, day);
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
