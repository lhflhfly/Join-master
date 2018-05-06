package com.lhf.join.View.User;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lhf.join.Bean.Book;
import com.lhf.join.R;
import com.lhf.join.View.Stadium.EvaluateActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.lhf.join.Constant.Constant.URL_EVALUATESTADIUM;
import static com.lhf.join.Constant.Constant.URL_PICTURE;
import static com.lhf.join.Constant.Constant.URL_SELECTEVALUATION;

public class MyEvaluation extends AppCompatActivity {
    private ImageView icon_stadium;
    private TextView tv_stadiumname;
    private TextView tv_content;
    private RatingBar ratingBar;
    private Book book;
    private ImageView icon_back;
    private double grade;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_evaluation);
        initView();
        initData();
    }

    private void initView() {
        icon_stadium = findViewById(R.id.icon_stadium);
        tv_stadiumname = findViewById(R.id.tv_stadiumname);
        ratingBar = findViewById(R.id.ratbar);
        tv_content = findViewById(R.id.tv_content);
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
        ratingBar.setIsIndicator(true);
        System.out.println(book.getStadiumpicture());
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.error) // 设置图片加载或解码过程中发生错误显示的图片
                .showImageOnLoading(R.drawable.loading)
                .resetViewBeforeLoading(false)  // default 设置图片在加载前是否重置、复位
                .delayBeforeLoading(100)  // 下载前的延迟时间
                .build();
        ImageLoader.getInstance().displayImage(URL_PICTURE+book.getStadiumpicture(),icon_stadium,options);

        evaluate(book.getBookingId());
    }

    private void evaluate(int bookingid) {
        String url = URL_SELECTEVALUATION;
        new EvaluateAsyncTask().execute(url,String.valueOf(bookingid));
    }

    private class EvaluateAsyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json = new JSONObject();
            try {
                json.put("bookingId",params[1]);
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
                    if (!"0".equals(loginresult)) {
                        float grade = (float)results.getDouble("grade");
                        String content = results.getString("content");
                        ratingBar.setRating(grade);
                        tv_content.setText(content);
                    } else {
                        Toast.makeText(MyEvaluation.this, "获取失败,请重试", Toast.LENGTH_SHORT).show();
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
