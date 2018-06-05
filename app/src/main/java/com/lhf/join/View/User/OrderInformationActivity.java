package com.lhf.join.View.User;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lhf.join.Adapter.BookAdapter;
import com.lhf.join.Adapter.OrderPagerAdapter;
import com.lhf.join.Adapter.StadiumAdapter;
import com.lhf.join.Base.BaseFragment;
import com.lhf.join.Bean.App;
import com.lhf.join.Bean.Book;
import com.lhf.join.Bean.Notice;
import com.lhf.join.Bean.Stadium;
import com.lhf.join.Bean.User;
import com.lhf.join.Fragment.FindFragment;
import com.lhf.join.Fragment.MeFragment;
import com.lhf.join.Fragment.NoUseOrderFragment;
import com.lhf.join.Fragment.OrderFragment;
import com.lhf.join.Fragment.UsedOrderFragment;
import com.lhf.join.R;
import com.lhf.join.View.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.lhf.join.Constant.Constant.URL_LOADINGORDER;
import static com.lhf.join.Constant.Constant.URL_NOTICE;
import static com.lhf.join.Constant.Constant.URL_ORDERINFORMATION;
import static com.lhf.join.Constant.Constant.URL_PICTURE;

public class OrderInformationActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {
    private ImageView icon_back;
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    private RadioGroup mRg_main;
    private User user;
    private RadioButton rb_nouse;
    private RadioButton rb_used;
    private ViewPager viewpager;
    private int position;
    private List<BaseFragment> mBaseFragment;
    private Fragment mContent;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_information);
        initview();
        initFragment();
        initdata();
    }


    private void initview() {
        user = (User) getIntent().getSerializableExtra("user");
        icon_back = findViewById(R.id.icon_back);
        viewpager = findViewById(R.id.viewpager);
        mRg_main = (RadioGroup) findViewById(R.id.rg_orderinf);
        rb_nouse = (RadioButton) findViewById(R.id.rb_nouse);
        rb_used = (RadioButton) findViewById(R.id.rb_past);

        getWindow().setStatusBarColor(Color.parseColor("#FF029ACC"));


    }

    private void initdata() {
        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewpager.addOnPageChangeListener(this);
        mRg_main.check(R.id.rb_nouse);
        viewpager.setAdapter(new OrderPagerAdapter(getSupportFragmentManager()));
        viewpager.setCurrentItem(0);
        mRg_main.setOnCheckedChangeListener(this);


    }

    private void initFragment() {
        mBaseFragment = new ArrayList();
        mBaseFragment.add(new NoUseOrderFragment());
        mBaseFragment.add(new UsedOrderFragment());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

        if (state == 2) {
            switch (viewpager.getCurrentItem()) {
                case PAGE_ONE:
                    rb_nouse.setChecked(true);
                    break;
                case PAGE_TWO:
                    rb_used.setChecked(true);
                    break;

            }








            }
        }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.rb_nouse:
                viewpager.setCurrentItem(PAGE_ONE);
                break;
            case R.id.rb_past:
                viewpager.setCurrentItem(PAGE_TWO);
                break;
        }

    }
}