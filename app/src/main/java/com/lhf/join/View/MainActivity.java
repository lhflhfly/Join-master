package com.lhf.join.View;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lhf.join.Base.BaseFragment;
import com.lhf.join.Bean.User;
import com.lhf.join.Fragment.FindFragment;
import com.lhf.join.Fragment.MeFragment;
import com.lhf.join.Fragment.OrderFragment;
import com.lhf.join.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RadioButton rb_order;
    private RadioButton rb_find;
    private RadioButton rb_me;
    private RadioGroup mRg_main;
    private Drawable drawable1;
    private Drawable drawable2;
    private Drawable drawable3;
    private int position;
    private List<BaseFragment> mBaseFragment;
    private Fragment mContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initFragment();
        setListener();

    }

    public void initView() {
        this.mRg_main = ((RadioGroup) findViewById(R.id.rg));
        this.rb_order = ((RadioButton) findViewById(R.id.rb_order));
        this.rb_find = ((RadioButton) findViewById(R.id.rb_find));
        this.rb_me = ((RadioButton) findViewById(R.id.rb_me));
        this.drawable1 = getResources().getDrawable(R.drawable.btn_order);
        this.drawable2 = getResources().getDrawable(R.drawable.btn_find);
        this.drawable3 = getResources().getDrawable(R.drawable.btn_me);
        this.drawable1.setBounds(0, 0, 69, 69);
        this.drawable2.setBounds(0, 0, 69, 69);
        this.drawable3.setBounds(0, 0, 69, 69);
        this.rb_order.setCompoundDrawables(null, this.drawable1, null, null);
        this.rb_find.setCompoundDrawables(null, this.drawable2, null, null);
        this.rb_me.setCompoundDrawables(null, this.drawable3, null, null);
        getWindow().setStatusBarColor(Color.parseColor("#FF029ACC"));

    }


    private void initFragment() {
        this.mBaseFragment = new ArrayList();
        this.mBaseFragment.add(new OrderFragment());
        this.mBaseFragment.add(new FindFragment());
        this.mBaseFragment.add(new MeFragment());
//        this.mBaseFragment.add(new FindFragment());
//        this.mBaseFragment.add(new MeFragment());
    }


    private void setListener() {
        this.mRg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        this.mRg_main.check(R.id.rb_order);
    }


    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_order:
                    position = 0;
                    break;
                case R.id.rb_find:
                    position = 1;
                    break;
                case R.id.rb_me:
                    position = 2;
                    break;
                default:
                    position = 0;
                    break;

            }

            BaseFragment to = getFragemnt();

            switchFragment(mContent, to);


        }

        private void switchFragment(Fragment from, Fragment to) {
            if (from != to) {
                mContent = to;
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if (!to.isAdded()) {
                    if (from != null) {
                        ft.hide(from);
                    }
                    if (to != null) {
                        ft.add(R.id.framelayout, to).commit();
                    }
                } else {
                    if (from != null) {
                        ft.hide(from);
                    }
                    if (to != null) {
                        ft.show(to).commit();
                    }
                }
            }
        }

        private BaseFragment getFragemnt() {
            BaseFragment fragment = mBaseFragment.get(position);
            return fragment;
        }
    }

// private void test() {
//     Intent intent = null;
//     Bundle bundle = new Bundle();
//     bundle.putString("name", "HzandYy");
//     bundle.putInt("age", 12);
//     intent.putExtras(bundle);
//     User user = null;
//     bundle.putSerializable("", user);
////     bundle.putSerializable();
//
//     Intent intent1 = getIntent();
//     Bundle bundle1 = intent.getExtras();
//     int age = bundle1.getInt("age", 0);
// }

}
