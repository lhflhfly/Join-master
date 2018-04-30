package com.lhf.join.Fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lhf.join.Adapter.AppAdapter;
import com.lhf.join.Adapter.StadiumAdapter;
import com.lhf.join.Base.BaseFragment;
import com.lhf.join.Bean.App;
import com.lhf.join.Bean.Changguan;
import com.lhf.join.Bean.Notice;
import com.lhf.join.Bean.Stadium;
import com.lhf.join.Bean.User;
import com.lhf.join.R;
import com.lhf.join.View.SerachSelectDialog;
import com.lhf.join.View.Stadium.SearchStadiumActivity;
import com.lhf.join.View.User.UserInformationActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.bgabanner.BGALocalImageSize;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.lhf.join.Constant.Constant.URL;
import static com.lhf.join.Constant.Constant.URL_LOADINGORDER;
import static com.lhf.join.Constant.Constant.URL_NOTICE;
import static com.lhf.join.Constant.Constant.URL_PICTURE;
import static com.lhf.join.Constant.Constant.URL_SELECTUSERBYUSERID;

public class OrderFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private GridView gridView;
    private LinearLayoutManager layoutManager;
    private BGABanner mContentBanner;
    private List<App> mData = null;
    private List<String> mDatas;
    private LocationClient mLocationClient;
    private AppAdapter myAdaper = null;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private TextView tv_city;
    private User user;
    private TextView tv_search;
    private ImageView icon;
    private ViewFlipper vf;
    private TextView gonggao,gonggao2,gonggao3,gonggao4;
    private LinearLayout lout5;
    private LinearLayout lout6;
    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.firstorder,null);
        gridView = view.findViewById(R.id.gv1);
        vf = view.findViewById(R.id.vf);
        toolbar = view.findViewById(R.id.tb_order);
        tv_city = view.findViewById(R.id.tv_city);
        tv_search = view.findViewById(R.id.tv_search);
        recyclerView = view.findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(mContext);
        mLocationClient = new LocationClient(mContext);
        mContentBanner = view.findViewById(R.id.banner);
        gonggao =view.findViewById(R.id.gonggao1);
        gonggao2 =view.findViewById(R.id.gonggao2);
        gonggao3 =view.findViewById(R.id.gonggao3);
        gonggao4=view.findViewById(R.id.gonggao4);
        icon = view.findViewById(R.id.icon_xiala);
        lout5= view.findViewById(R.id.lout5);
        lout6 = view.findViewById(R.id.lout6);
        swipeRefreshLayout = view.findViewById(R.id.sr_order);
        return view;


    }



    @Override
    protected void initData() {
        user = (User) getActivity().getIntent().getSerializableExtra("user");
        mLocationClient.registerLocationListener(new MyLocationListener());
        requestLocation();
        LoadingGongGao();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Loading(tv_city.getText().toString());
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        tv_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSelect(v);

            }
        });
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSelect(v);
            }
        });

        mDatas = new ArrayList();
        String [] citys = {"武汉","北京","上海","深圳","兰州","成都","天津"};
        int i = 0;
        while (i < citys.length)
        {
            this.mDatas.add(citys[i] + "市");
            i += 1;
        }
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,SearchStadiumActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("user",user);
                mBundle.putSerializable("city",tv_city.getText().toString());
                intent.putExtras(mBundle);
                startActivity(intent);
            }
        });

        BGALocalImageSize localImageSize = new BGALocalImageSize(720, 1280, 320, 640);
        mContentBanner.setAdapter(new BGABanner.Adapter<ImageView, String>(){

            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, @Nullable String model, int position) {
                Glide.with(mContext)
                        .load(model)
                        .override(1024,768)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.error)
                        .centerCrop()
                        .dontAnimate()
                        .into(itemView);
            }
        });
        mData = new LinkedList<App>();
        mData.add(new App(R.drawable.lanqiu,"篮球"));
        mData.add(new App(R.drawable.pingpong,"高尔夫"));
        mData.add(new App(R.drawable.zuqiu,"足球"));
        mData.add(new App(R.drawable.tennis,"网球"));
        mData.add(new App(R.drawable.swimming,"游泳"));
        mData.add(new App(R.drawable.yumao,"羽毛球"));
        mData.add(new App(R.drawable.exercise,"健身"));
        mData.add(new App(R.drawable.archer2,"射箭"));
        myAdaper = new AppAdapter((LinkedList<App>) mData,mContext);
        gridView.setAdapter(myAdaper);
        gridView.setOnItemClickListener(this);





    }

    private void Loading(String tv_city) {
        String loadingUrl = URL_LOADINGORDER;
        new LoadingAsyncTask().execute(loadingUrl,tv_city);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(),SearchStadiumActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("city",tv_city.getText().toString());
        switch (position+1){
            case 1:
                mBundle.putSerializable("type","篮球");
                mBundle.putSerializable("user",user);
                intent.putExtras(mBundle);
                startActivity(intent);
                break;
            case 2:
                mBundle.putSerializable("type","高尔夫");
                mBundle.putSerializable("user",user);
                intent.putExtras(mBundle);
                startActivity(intent);
                break;
            case 3:
                mBundle.putSerializable("type","足球");
                mBundle.putSerializable("user",user);
                intent.putExtras(mBundle);
                startActivity(intent);
                break;
            case 4:
                mBundle.putSerializable("type","网球");
                mBundle.putSerializable("user",user);
                intent.putExtras(mBundle);
                startActivity(intent);
                break;
            case 5:
                mBundle.putSerializable("type","游泳");
                mBundle.putSerializable("user",user);
                intent.putExtras(mBundle);
                startActivity(intent);
                break;
            case 6:
                mBundle.putSerializable("type","羽毛球");
                mBundle.putSerializable("user",user);
                intent.putExtras(mBundle);
                startActivity(intent);
                break;
            case 7:
                mBundle.putSerializable("type","健身");
                mBundle.putSerializable("user",user);
                intent.putExtras(mBundle);
                startActivity(intent);
                break;
            case 8:
                mBundle.putSerializable("type","射箭");
                mBundle.putSerializable("user",user);
                intent.putExtras(mBundle);
                startActivity(intent);
                break;

        }
    }

    private class LoadingAsyncTask extends AsyncTask<String, Integer, String> {
        public LoadingAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json=new JSONObject();
            try {
                json.put("city",params[1]);
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
            List<Stadium> mData = new ArrayList<>();
            if (!"null".equals(s)){
                try {
                    JSONArray results = new JSONArray(s);
                    for(int i=0;i<results.length();i++){
                        JSONObject js= results.getJSONObject(i);
                        Stadium stadium = new Stadium();
                        stadium.setStadiumId(js.getInt("stadiumId"));
                        stadium.setStadiumname(js.getString("stadiumname"));
                        stadium.setStadiumtype(js.getString("stadiumtypeId"));
                        stadium.setArea(js.getString("area"));
                        stadium.setIndoor(js.getInt("indoor"));
                        stadium.setAircondition(js.getInt("aircondition"));
                        stadium.setCity(js.getString("city"));
                        stadium.setMainpicture(URL_PICTURE+js.getString("mainpicture"));
                        stadium.setAdress(js.getString("adress"));
                        stadium.setNum(js.getString("num"));
                        stadium.setOpentime(js.getString("opentime"));
                        mData.add(stadium);
                    }
                    List<String> list = new ArrayList();
                    for(int i=0;i<3;i++){
                        list.add(mData.get(i).getMainpicture());
                    }
                    List<String> list2 = new ArrayList();
                    for(int i=0;i<3;i++){
                        list2.add(mData.get(i).getStadiumname());
                    }
                    mContentBanner.setData(list,list2);
                    List<Stadium> mData2 = new ArrayList<>();
                    System.out.println("22");
                    for(int i=0;i<mData.size();i++){
                        Stadium stadium = new Stadium();
                        stadium.setMainpicture(mData.get(i).getMainpicture());
                        stadium.setAdress(mData.get(i).getAdress());
                        stadium.setCity(mData.get(i).getCity());
                        stadium.setAircondition(mData.get(i).getAircondition());
                        stadium.setArea(mData.get(i).getArea());
                        stadium.setStadiumname(mData.get(i).getStadiumname());
                        stadium.setIndoor(mData.get(i).getIndoor());
                        stadium.setNum(mData.get(i).getNum());
                        stadium.setStadiumtype(mData.get(i).getStadiumtype());
                        stadium.setStadiumId(mData.get(i).getStadiumId());
                        stadium.setOpentime(mData.get(i).getOpentime());
                        mData2.add(stadium);
                    }
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
                    StadiumAdapter adapter = new StadiumAdapter(mContext,mData2,user);
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setAdapter(adapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                System.out.println("结果为空");
                List<Stadium> mData2 = new ArrayList<>();
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
                StadiumAdapter adapter = new StadiumAdapter(mContext,mData2,user);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setAdapter(adapter);
                Toast.makeText(mContext,"该城市上没有体育场所加入",Toast.LENGTH_LONG).show();

            }
        }
    }

    private void LoadingGongGao(){
        String gonggaoUrl = URL_NOTICE;
        new GongGaoAsyncTask().execute(gonggaoUrl);

    }

    private class GongGaoAsyncTask extends AsyncTask<String, Integer, String> {
        public GongGaoAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json=new JSONObject();
            try {
                json.put("gonggao",1);
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
            List<Notice> mData = new ArrayList<>();
            if (!"null".equals(s)){
                try {
                    JSONArray results = new JSONArray(s);
                    for(int i=0;i<results.length();i++){
                        JSONObject js= results.getJSONObject(i);
                        Notice notice = new Notice();
                        notice.setContent(js.getString("content")) ;
                        notice.setTime(js.getString("time"));
                        mData.add(notice);
                    }
                    gonggao.setText(mData.get(0).getContent());
                    gonggao2.setText(mData.get(1).getContent());
                    gonggao3.setText(mData.get(2).getContent());
                    gonggao4.setText(mData.get(3).getContent());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                System.out.println("结果为空");
                Toast.makeText(mContext,"该城市上没有体育场所加入",Toast.LENGTH_LONG).show();

            }
        }
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

    public void doSelect(View view){
        SerachSelectDialog.Builder alert = new SerachSelectDialog.Builder(mContext);
        alert.setListData(mDatas);
        alert.setTitle("请选择城市");
        alert.setSelectedListiner(new SerachSelectDialog.Builder.OnSelectedListiner() {
            @Override
            public void onSelected(String info) {
                tv_city.setText(info);
                swipeRefreshLayout.post(new Runnable() {

                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                        Loading(tv_city.getText().toString());
                        swipeRefreshLayout.setRefreshing(true);

                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

            }
        });
        SerachSelectDialog mDialog = alert.show();
        //设置Dialog 尺寸
        mDialog.setDialogWindowAttr(0.9,0.9,getActivity());
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
                    System.out.println("12" + BDLocation.getCity());
                    if (BDLocation.getCity().equals("")) {
                        tv_city.setText("城市名");
                    }
                    for (;;)
                    {
                        tv_city.setText(BDLocation.getCity());
                        Loading(tv_city.getText().toString());
                        mLocationClient.stop();

                        return;
                    }
                }
            });
        }
    }


    public void onDestroy()
    {
        super.onDestroy();
        mLocationClient.stop();
    }
}
