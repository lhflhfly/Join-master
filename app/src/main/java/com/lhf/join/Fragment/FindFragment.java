package com.lhf.join.Fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lhf.join.Adapter.FindAdapter;
import com.lhf.join.Adapter.NeedAdapter;
import com.lhf.join.Base.BaseFragment;
import com.lhf.join.Bean.Need;
import com.lhf.join.Bean.User;
import com.lhf.join.R;
import com.lhf.join.View.Find.InsertNeedActivity;
import com.lhf.join.View.FixedRecyclerView;
import com.melnykov.fab.ObservableScrollView;

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

import static com.lhf.join.Constant.Constant.URL_FINDINFORMATION;

public class FindFragment extends BaseFragment{
    private User user;
    private String city;
    private com.melnykov.fab.FloatingActionButton floatingActionButton;
    private LocationClient mLocationClient;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ObservableScrollView sv_find;
    private TextView tv_nofind;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");




    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.first_find,null);
        floatingActionButton=view.findViewById(R.id.fab_add_comment);
        mLocationClient = new LocationClient(mContext);
        recyclerView = view.findViewById(R.id.rv_find);
        tv_nofind = view.findViewById(R.id.tv_nofind);
        layoutManager = new LinearLayoutManager(mContext);
        swipeRefreshLayout = view.findViewById(R.id.sr_find);
        sv_find = view.findViewById(R.id.sv_find);




        return view;


    }

    @Override
    public void onResume() {
        super.onResume();
//        RefrshUser(userId);

    }

    @Override
    protected void initData() {
        tv_nofind.setText("");
        user = (User) getActivity().getIntent().getSerializableExtra("user");
        mLocationClient.registerLocationListener(new MyLocationListener());
        requestLocation();

        floatingActionButton.attachToScrollView(sv_find);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("".equals(city)){
                    Toast.makeText(mContext,"未获取到当前城市",Toast.LENGTH_LONG).show();

                }else{
                    Intent intent = new Intent(getContext(), InsertNeedActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("user",user);
                    mBundle.putSerializable("city","成都市");
                    intent.putExtras(mBundle);
                    startActivity(intent);
                }
            }
        });
        findInformation(user);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        sv_find.getViewTreeObserver().addOnScrollChangedListener(new  ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                swipeRefreshLayout.setEnabled(sv_find.getScrollY()==0);
            }
        });

//        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener(){
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                int topRowVerticalPosition =
//                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
//                swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
//
//            }
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//        });

    }


    private void initLocation()
    {
        LocationClientOption localLocationClientOption = new LocationClientOption();
        localLocationClientOption.setIsNeedAddress(true);
        localLocationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocationClient.setLocOption(localLocationClientOption);
    }


    private void requestLocation()
    {
        initLocation();
        mLocationClient.start();
    }


    private class MyLocationListener implements BDLocationListener
    {
        private MyLocationListener() {}

        public void onReceiveLocation(final BDLocation BDLocation)
        {

            getActivity().runOnUiThread(new Runnable()
            {
                public void run()
                {
                   city = BDLocation.getCity();


                }
            });
        }
    }

    private void findInformation(User user) {
        String loadingUrl = URL_FINDINFORMATION;
        new findInformationAsyncTask().execute(loadingUrl,String.valueOf(user.getUserId()));
    }

    private class findInformationAsyncTask extends AsyncTask<String, Integer, String> {
        public findInformationAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            int method = 1;
            String results = null;
            JSONObject json=new JSONObject();
            try {
                json.put("userId",params[1]);
                json.put("method",method);
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
                Request request = new Request.Builder()
                        .url(params[0])
                        .post(requestBody)
                        .build();
                response=okHttpClient.newCall(request).execute();
                results=response.body().string();
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
            System.out.println("返回的数据："+s);
            List<Need> mData = new ArrayList<>();
            if (!"null".equals(s)){
                try {
                    JSONArray results = new JSONArray(s);
                    for(int i=results.length()-1;i>=0;i--){
                        JSONObject js= results.getJSONObject(i);
                        Need need = new Need();
                        need.setNeedId(js.getInt("needId"));
                        need.setUserId(js.getInt("userId"));
                        need.setUsername(js.getString("username"));
                        need.setStadiumname(js.getString("stadiumname"));
                        need.setTime(js.getString("time"));
                        need.setNum(js.getInt("num"));
                        need.setNum_join(js.getInt("num_join"));
                        need.setRemark(js.getString("remark"));
                        mData.add(need);
                    }
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
                    FindAdapter adapter = new FindAdapter(mContext,mData,user,true);
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                System.out.println("结果为空");
                tv_nofind.setText("当前没有召集信息");
                List<Need> mData2 = new ArrayList<>();
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
                NeedAdapter adapter = new NeedAdapter(mContext,mData2);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setAdapter(adapter);
            }
        }
    }

}
