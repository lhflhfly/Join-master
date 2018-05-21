package com.lhf.join.View.Find;

import android.graphics.Color;
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

import static com.lhf.join.Constant.Constant.URL_JOINFIND;


public class FindActivity_Me extends AppCompatActivity {
    private TextView tv_username;
    private TextView tv_stadiumname;
    private TextView tv_time;
    private TextView tv_num;
    private TextView tv_num_join;
    private TextView tv_remark;
    private User user;
    private Need need;
    private ImageView icon_back;
    private ImageView user_proflie;
    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find__me);
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
        icon_back= findViewById(R.id.icon_back);
        user_proflie= findViewById(R.id.user_proflie);
        getWindow().setStatusBarColor(Color.parseColor("#FF029ACC"));
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
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.me) // 设置图片加载或解码过程中发生错误显示的图片
                .showImageOnLoading(R.drawable.loading)
                .resetViewBeforeLoading(false)  // default 设置图片在加载前是否重置、复位
                .delayBeforeLoading(100)  // 下载前的延迟时间
                .build();
        ImageLoader.getInstance().displayImage(need.getProflie(), user_proflie,options);
        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


}
