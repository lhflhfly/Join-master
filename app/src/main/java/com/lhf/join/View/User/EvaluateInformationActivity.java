package com.lhf.join.View.User;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lhf.join.Adapter.EvaluatePagerAdapter;
import com.lhf.join.Adapter.OrderPagerAdapter;
import com.lhf.join.Base.BaseFragment;
import com.lhf.join.Bean.User;
import com.lhf.join.Fragment.NoUseOrderFragment;
import com.lhf.join.Fragment.UsedOrderFragment;
import com.lhf.join.R;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;

public class EvaluateInformationActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {
    private ImageView icon_back;
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    private RadioGroup mRg_main;
    private User user;
    private SwipeRefreshLayout swipeRefreshLayout;
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
        setContentView(R.layout.activity_evaluate_information);
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
        viewpager.setAdapter(new EvaluatePagerAdapter(getSupportFragmentManager()));
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