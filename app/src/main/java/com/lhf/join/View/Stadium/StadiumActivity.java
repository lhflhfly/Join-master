package com.lhf.join.View.Stadium;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lhf.join.Adapter.StadiumAdapter;
import com.lhf.join.Bean.Stadium;
import com.lhf.join.Bean.User;
import com.lhf.join.R;
import com.lhf.join.View.Find.InsertNeedActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sackcentury.shinebuttonlib.ShineButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.lhf.join.Constant.Constant.URL_DELETECOLLECTION;
import static com.lhf.join.Constant.Constant.URL_INSERTCOLLECTION;
import static com.lhf.join.Constant.Constant.URL_ISCOLLECTED;
import static com.lhf.join.Constant.Constant.URL_PICTURE;
import static com.lhf.join.Constant.Constant.URL_SEARCHSTADIUM;

public class StadiumActivity extends AppCompatActivity {
    private TextView tv;
    private TextView tv_stadiumname;
    private TextView tv_area;
    private TextView tv_type;
    private TextView tv_num;
    private TextView tv_indoor;
    private TextView tv_aircondition;
    private TextView tv_adress;
    private TextView tv_opentime;
    private Stadium stadium;
    private ImageView icon_back;
    private ImageView icon_share;
    private ImageView icon_stadium;
    private RatingBar ratingBar;
    private Button btn_order;
    private User user;
    private boolean firstcollect = true;
    private boolean firstcollect2 = true;
    private ToggleButton shineButton;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stadium);
        stadium = (Stadium) getIntent().getSerializableExtra("stadium");
        initview();
        initdata();


    }

    private void initview() {
        tv = findViewById(R.id.tv_stadiumname);
        icon_back = findViewById(R.id.icon_back);
        icon_share = findViewById(R.id.icon_share);
        icon_stadium = findViewById(R.id.icon_stadium);
        tv_stadiumname = findViewById(R.id.tv_stadiumname1);
        tv_type = findViewById(R.id.tv_changguan_type);
        tv_area = findViewById(R.id.tv_area);
        tv_num = findViewById(R.id.tv_num);
        tv_indoor = findViewById(R.id.tv_indoor);
        tv_aircondition = findViewById(R.id.tv_aircondition);
        tv_opentime = findViewById(R.id.tv_opentime);
        tv_adress = findViewById(R.id.tv_adress);
        ratingBar = findViewById(R.id.ratbar);
        btn_order = findViewById(R.id.btn_order);
        shineButton = findViewById(R.id.po_image3);
        getWindow().setStatusBarColor(Color.parseColor("#FF029ACC"));

    }

    private void initdata() {
        user = (User) getIntent().getSerializableExtra("user");
        isCollectd(stadium.getStadiumId(), user.getUserId());
        System.out.println("userId:" + user.getUserId());
        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv.setText(stadium.getStadiumname());
        tv_stadiumname.setText(stadium.getStadiumname());
        tv_type.setText(stadium.getStadiumtype());
        tv_area.setText(stadium.getArea() + "平方米");
        tv_num.setText(stadium.getNum() + "人");
        tv_opentime.setText(stadium.getOpentime());
        if (stadium.getIndoor() == 1) {
            tv_indoor.setText(" 是");
        } else {
            tv_indoor.setText(" 否");
        }
        if (stadium.getAircondition() == 1) {
            tv_aircondition.setText(" 是");
        } else {
            tv_aircondition.setText(" 否");
        }
        tv_adress.setText(stadium.getCity() + stadium.getAdress());
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.error) // 设置图片加载或解码过程中发生错误显示的图片
                .showImageOnLoading(R.drawable.loading)
                .resetViewBeforeLoading(false)  // default 设置图片在加载前是否重置、复位
                .delayBeforeLoading(1000)  // 下载前的延迟时间
                .build();
        ImageLoader.getInstance().displayImage(stadium.getMainpicture(),icon_stadium,options);
//        Glide.with(this)
//                .load(stadium.getMainpicture())
//                .placeholder(R.drawable.loading)
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .error(R.drawable.error)
//                .into(icon_stadium);
        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StadiumActivity.this, OrderStadiumActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("user", user);
                mBundle.putSerializable("stadium", stadium);
                intent.putExtras(mBundle);
                startActivity(intent);
            }
        });
        shineButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    if (firstcollect) {
                        collect(stadium.getStadiumId(), user.getUserId(), true);
                    } else {
                    }
                } else {
                    if (firstcollect2) {
                        collect(stadium.getStadiumId(), user.getUserId(), false);
                    } else {
                    }
                }
            }
        });
        icon_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareText("分享场馆地址","123",stadium.getStadiumname()+"\n地址："+tv_adress.getText().toString());

            }
        });


    }

    private void collect(int stadiunmId, int userId, boolean flag) {
        String SearchUrl = null;
        if (flag) {
            SearchUrl = URL_INSERTCOLLECTION;
        } else {
            SearchUrl = URL_DELETECOLLECTION;
        }
        new insertCollectionAsyncTask().execute(SearchUrl, String.valueOf(stadiunmId), String.valueOf(userId));
    }

    private class insertCollectionAsyncTask extends AsyncTask<String, Integer, String> {
        public insertCollectionAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json = new JSONObject();
            try {
                json.put("stadiumId", params[1]);
                json.put("userId", params[2]);
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
                Request request = new Request.Builder()
                        .url(params[0])
                        .post(requestBody)
                        .build();
                response = okHttpClient.newCall(request).execute();
                results = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.println("返回的数据：" + s);
            if (!"".equals(s)) {
                try {
                    JSONObject results = new JSONObject(s);
                    String loginresult = results.getString("result");
                    if (loginresult.equals("1")) {
                        Toast.makeText(StadiumActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                    } else if (loginresult.equals("2")) {
                        Toast.makeText(StadiumActivity.this, "取消收藏", Toast.LENGTH_SHORT).show();
                    } else if (loginresult.equals("3")) {

                    } else {
                        Toast.makeText(StadiumActivity.this, "系统异常", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("结果为空");
            }
        }
    }

    private void isCollectd(int stadiunmId, int userId) {
        String SearchUrl = URL_ISCOLLECTED;
        new isCollectionAsyncTask().execute(SearchUrl, String.valueOf(stadiunmId), String.valueOf(userId));
    }

    private class isCollectionAsyncTask extends AsyncTask<String, Integer, String> {
        public isCollectionAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json = new JSONObject();
            try {
                json.put("stadiumId", params[1]);
                json.put("userId", params[2]);
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
                Request request = new Request.Builder()
                        .url(params[0])
                        .post(requestBody)
                        .build();
                response = okHttpClient.newCall(request).execute();
                results = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.println("返回的数据：" + s);
            if (!"".equals(s)) {
                try {
                    JSONObject results = new JSONObject(s);
                    String loginresult = results.getString("result");
                    if (loginresult.equals("1")) {
                        shineButton.setChecked(false);
                    } else {
                        shineButton.setChecked(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("结果为空");
            }
        }
    }

    private void shareText(String dlgTitle, String subject, String content) {
        if (content == null || "".equals(content)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        if (subject != null && !"".equals(subject)) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }

        intent.putExtra(Intent.EXTRA_TEXT, content);

        // 设置弹出框标题
        if (dlgTitle != null && !"".equals(dlgTitle)) { // 自定义标题
            startActivity(Intent.createChooser(intent, dlgTitle));
        } else { // 系统默认标题
            startActivity(intent);
        }
    }
}
