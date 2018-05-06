package com.lhf.join.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.lhf.join.Fragment.EvaluatedOrderFragment;
import com.lhf.join.Fragment.NoEvaluateOrderFragment;
import com.lhf.join.Fragment.NoUseOrderFragment;
import com.lhf.join.Fragment.UsedOrderFragment;
import com.lhf.join.View.User.OrderInformationActivity;

/**
 * Created by Administrator on 2018/4/10.
 */

public class EvaluatePagerAdapter extends FragmentPagerAdapter {

    private final int PAGER_COUNT = 2;
    private EvaluatedOrderFragment evaluatedOrderFragment = null;
    private NoEvaluateOrderFragment noEvaluateOrderFragment = null;




    public EvaluatePagerAdapter(FragmentManager fm) {
        super(fm);
        evaluatedOrderFragment = new EvaluatedOrderFragment();
        noEvaluateOrderFragment = new NoEvaluateOrderFragment();

    }


    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("position Destory" + position);
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case OrderInformationActivity.PAGE_ONE:
                fragment = noEvaluateOrderFragment;
                break;
            case OrderInformationActivity.PAGE_TWO:
                fragment = evaluatedOrderFragment;
                break;
        }
        return fragment;
    }

}
