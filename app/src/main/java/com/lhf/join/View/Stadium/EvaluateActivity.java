package com.lhf.join.View.Stadium;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lhf.join.Bean.Book;
import com.lhf.join.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.lhf.join.Constant.Constant.URL_EVALUATESTADIUM;
import static com.lhf.join.Constant.Constant.URL_ORDERSTADIUM;

public class EvaluateActivity extends AppCompatActivity {
    private ImageView icon_stadium;
    private TextView tv_stadiumname;
    private EditText et_content;
    private RatingBar ratingBar;
    private Button btn_submit;
    private Book book;
    private double grade;
    private ImageView icon_back;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate);
        initView();
        initData();
    }

    private void initView() {
        icon_stadium = findViewById(R.id.icon_stadium);
        tv_stadiumname = findViewById(R.id.tv_stadiumname);
        ratingBar = findViewById(R.id.ratbar);
        btn_submit = findViewById(R.id.btn_submit);
        et_content = findViewById(R.id.et_content);
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
        book = (Book) getIntent().getSerializableExtra("book");
        tv_stadiumname.setText(book.getStadiumname());
        System.out.println(book.getStadiumpicture());
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.error) // 设置图片加载或解码过程中发生错误显示的图片
                .showImageOnLoading(R.drawable.loading)
                .resetViewBeforeLoading(false)  // default 设置图片在加载前是否重置、复位
                .delayBeforeLoading(100)  // 下载前的延迟时间
                .build();
        ImageLoader.getInstance().displayImage(book.getStadiumpicture(),icon_stadium,options);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grade = (double) ratingBar.getRating();
                String content = et_content.getText().toString();
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                String evaluatetime = (year + "年" + (month + 1) + "月" + day + "日");
                EvaluateStadium(book.getStadiumId(),grade,book.getBookingId(),content,book.getUserId(),evaluatetime);

            }
        });
    }

    private void EvaluateStadium(int stadiumId, double grade ,int bookingid,String content,int userId,String time) {
        String orderURL = URL_EVALUATESTADIUM;
        new EvaluateStadiumAsyncTask().execute(orderURL,String.valueOf(stadiumId), String.valueOf(grade), String.valueOf(bookingid),content,String.valueOf(userId),time);
    }

    private class EvaluateStadiumAsyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json = new JSONObject();
            try {
                json.put("stadiumId", params[1]);
                json.put("grade",params[2]);
                json.put("bookingId",params[3]);
                json.put("content",params[4]);
                json.put("userId",params[5]);
                json.put("evaluatetime",params[6]);
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
                        Toast.makeText(EvaluateActivity.this, "评价成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EvaluateActivity.this, "评价失败，请重试", Toast.LENGTH_SHORT).show();
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
