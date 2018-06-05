package com.lhf.join.View.Stadium;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.lhf.join.Adapter.EvaluatonAdapter;
import com.lhf.join.Adapter.StadiumAdapter;
import com.lhf.join.Bean.Evaluation;
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
import static com.lhf.join.Constant.Constant.URL_GETEVALUATEINFORMATION;
import static com.lhf.join.Constant.Constant.URL_INSERTCOLLECTION;
import static com.lhf.join.Constant.Constant.URL_ISCOLLECTED;
import static com.lhf.join.Constant.Constant.URL_PICTURE;
import static com.lhf.join.Constant.Constant.URL_PROFLIE;
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
    private TextView tv_evaluation;
    private TextView tv_noevaluation;
    private TextView tv_iconnum;
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
    private RecyclerView recyclerView;
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
        tv_evaluation = findViewById(R.id.tv);
        tv_iconnum = findViewById(R.id.tv_count);
        recyclerView = findViewById(R.id.rv_evaluation);
        tv_noevaluation = findViewById(R.id.tv_noevaluation);

        getWindow().setStatusBarColor(Color.parseColor("#FF029ACC"));

    }

    private void initdata() {
        if(stadium.getStadiumtype().equals("滑冰")){
            btn_order.setVisibility(View.GONE);
        }
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
        tv_iconnum.setText(String.valueOf(stadium.getIconnum()));
        tv_area.setText(stadium.getArea() + "平方米");
        tv_num.setText(stadium.getNum() + "人");
        tv_opentime.setText(stadium.getOpentime()+":00--"+stadium.getClosetime()+":00");
        ratingBar.setRating(stadium.getGrade());
        ratingBar.setIsIndicator(true);
        tv_adress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    try {
                        Intent intent = Intent.getIntent("intent://map/direction?destination="+stadium.getAdress() + // 终点
                                "&mode=driving&" + // 导航路线方式
                                "&src=新疆和田#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                        startActivity(intent); // 启动调用
                    } catch (Exception e) {

                    }
            }
        });
        icon_stadium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StadiumActivity.this, StadiumIconActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("stadium", stadium);
                intent.putExtras(mBundle);
                startActivity(intent);
            }
        });
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
                .delayBeforeLoading(0)  // 下载前的延迟时间
                .build();
        ImageLoader.getInstance().displayImage(stadium.getMainpicture(),icon_stadium,options);
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
                String text = stadium.getStadiumname()
                        +"\n地址："
                        +tv_adress.getText().toString()
                        +"\n项目："+stadium.getStadiumtype()
                        +"\n场馆面积："+tv_area.getText().toString()
                        +"\n营业时间："+tv_opentime.getText().toString()
                        +"\n评分："+stadium.getGrade();
                shareText("分享场馆","123",text);

            }
        });
        loadingEvaluation(stadium.getStadiumId());


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

    private void loadingEvaluation(int stadiunmId) {
        String SearchUrl = URL_GETEVALUATEINFORMATION;
        new loadingEvaluationAsyncTask().execute(SearchUrl, String.valueOf(stadiunmId));
    }

    private class loadingEvaluationAsyncTask extends AsyncTask<String, Integer, String> {
        public loadingEvaluationAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json = new JSONObject();
            try {
                json.put("stadiumId", params[1]);
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
            List<Evaluation> mData = new ArrayList<>();
            if (!"null".equals(s)) {
                try {
                    JSONArray results = new JSONArray(s);
                    //循环拿出接受的数据并赋值给stadium对象
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject js = results.getJSONObject(i);
                        Evaluation evaluation = new Evaluation();
                        evaluation.setUsername(js.getString("username"));
                        evaluation.setContent(js.getString("content"));
                        evaluation.setIcon(URL_PROFLIE+js.getString("proflie"));
                        evaluation.setGrade(js.getDouble("grade"));
                        evaluation.setEvaluatetime(js.getString("evaluatetime"));
                        mData.add(evaluation);
                    }
                    tv_evaluation.setText("--用户评论("+results.length()+")--");
                    EvaluatonAdapter adapter = new EvaluatonAdapter(StadiumActivity.this,mData);
                    recyclerView.setNestedScrollingEnabled(false);
                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(staggeredGridLayoutManager);
                    recyclerView.addItemDecoration(new DividerItemDecoration(StadiumActivity.this, DividerItemDecoration.VERTICAL));
                    recyclerView.setAdapter(adapter);


            } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                System.out.println("结果为空");
                List<Evaluation> mData2 = new ArrayList<>();
                tv_evaluation.setText("--用户评论("+mData2.size()+")--");
                tv_noevaluation.setVisibility(View.VISIBLE);
                recyclerView.addItemDecoration(new DividerItemDecoration(StadiumActivity.this, DividerItemDecoration.VERTICAL));
                EvaluatonAdapter adapter = new EvaluatonAdapter(StadiumActivity.this, mData2);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setAdapter(adapter);
                Toast.makeText(StadiumActivity.this, "该场馆暂无评论", Toast.LENGTH_SHORT).show();
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
