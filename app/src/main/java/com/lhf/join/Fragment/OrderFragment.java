package com.lhf.join.Fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
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
import com.lhf.join.Adapter.GridAdapter;
import com.lhf.join.Adapter.MyViewPagerAdapter;
import com.lhf.join.Adapter.StadiumAdapter;
import com.lhf.join.Base.BaseFragment;
import com.lhf.join.Bean.App;
import com.lhf.join.Bean.Notice;
import com.lhf.join.Bean.Stadium;
import com.lhf.join.Bean.User;
import com.lhf.join.R;
import com.lhf.join.View.SerachSelectDialog;
import com.lhf.join.View.Stadium.SearchStadiumActivity;
import com.lhf.join.View.Stadium.StadiumActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.bgabanner.BGALocalImageSize;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.lhf.join.Constant.Constant.URL_CITY;
import static com.lhf.join.Constant.Constant.URL_LOADINGORDER;
import static com.lhf.join.Constant.Constant.URL_NOTICE;
import static com.lhf.join.Constant.Constant.URL_PICTURE;
import static com.lhf.join.Constant.Constant.URL_SPORTS;
import static com.lhf.join.Constant.Constant.URL_SPORTSTYPE;


public class OrderFragment extends BaseFragment {
    private LinearLayoutManager layoutManager;
    private BGABanner mContentBanner;
    private List<String> mDatas;
    private LocationClient mLocationClient;
    private ViewPager viewPager;
    private LinearLayout group;
    private ImageView[] ivPoints;
    private int totalPage;
    private int mPageSize = 8;
    private List<App> mData;
    private List<View> viewPagerList;
    private RecyclerView recyclerView;
    private TextView tv_city;
    private User user;
    private TextView tv_search;
    private ImageView icon;
    private ViewFlipper vf;
    private TextView gonggao, gonggao2, gonggao3, gonggao4;
    private ProgressDialog progressDialog;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.firstorder, null);
//        gridView = view.findViewById(R.id.gv1);
        vf = view.findViewById(R.id.vf);
        tv_city = view.findViewById(R.id.tv_city);
        tv_search = view.findViewById(R.id.tv_search);
        recyclerView = view.findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(mContext);
        mLocationClient = new LocationClient(mContext);
        mContentBanner = view.findViewById(R.id.banner);
        gonggao = view.findViewById(R.id.gonggao1);
        gonggao2 = view.findViewById(R.id.gonggao2);
        gonggao3 = view.findViewById(R.id.gonggao3);
        gonggao4 = view.findViewById(R.id.gonggao4);
        icon = view.findViewById(R.id.icon_xiala);
        swipeRefreshLayout = view.findViewById(R.id.sr_order);
        viewPager = view.findViewById(R.id.viewpager);
        group = view.findViewById(R.id.points);
        return view;


    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initData() {
        user = (User) getActivity().getIntent().getSerializableExtra("user");
        mLocationClient.registerLocationListener(new MyLocationListener());
        requestLocation();
        LoadingGongGao();
        LoadingSports();
        LoadingCitys();
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        swipeRefreshLayout.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        swipeRefreshLayout.setEnabled(true);
                        break;
                }
                return false;
            }
        });
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

        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SearchStadiumActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("user", user);
                mBundle.putSerializable("city", tv_city.getText().toString());
                intent.putExtras(mBundle);
                startActivity(intent);
            }
        });

        BGALocalImageSize localImageSize = new BGALocalImageSize(720, 1280, 320, 640);
        mContentBanner.setAdapter(new BGABanner.Adapter<ImageView, String>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, @Nullable String model, int position) {
                Glide.with(mContext)
                        .load(model)
                        .override(1024, 768)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.error)
                        .centerCrop()
                        .dontAnimate()
                        .into(itemView);
            }
        });
    }

    //网络请求主页数据
    private void Loading(String tv_city) {
        String loadingUrl = URL_LOADINGORDER;
        new LoadingAsyncTask().execute(loadingUrl, tv_city);
    }

    private class LoadingAsyncTask extends AsyncTask<String, Integer, String> {
        public LoadingAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json = new JSONObject();
            try {
                json.put("city", params[1]);
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
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext, R.style.ThemeDialog);
            progressDialog.setMessage("加载中，请稍后....");
            progressDialog.show();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);

        }

        //返回数据进行处理
        @Override
        protected void onPostExecute(String s) {
            System.out.println("返回的数据：" + s);
            final List<Stadium> mData = new ArrayList<>();
            if (!"null".equals(s)) {
                try {
                    JSONArray results = new JSONArray(s);
                    //循环拿出接受的数据并赋值给stadium对象
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject js = results.getJSONObject(i);
                        Stadium stadium = new Stadium();
                        stadium.setStadiumId(js.getInt("stadiumId"));
                        stadium.setStadiumname(js.getString("stadiumname"));
                        stadium.setStadiumtype(js.getString("stadiumtypename"));
                        stadium.setArea(js.getString("area"));
                        stadium.setIndoor(js.getInt("indoor"));
                        stadium.setAircondition(js.getInt("aircondition"));
                        stadium.setCity(js.getString("city"));
                        stadium.setMainpicture(URL_PICTURE + js.getString("mainpicture"));
                        stadium.setAdress(js.getString("adress"));
                        stadium.setNum(js.getString("num"));
                        stadium.setOpentime(js.getString("opentime"));
                        stadium.setClosetime(js.getString("closetime"));
                        stadium.setGrade((float) js.getDouble("grade"));
                        stadium.setIconnum(js.getInt("iconnum"));
                        mData.add(stadium);
                    }
                    List<String> list = new ArrayList();
                    for (int i = 0; i < 3; i++) {
                        list.add(mData.get(i).getMainpicture());
                    }
                    List<String> list2 = new ArrayList();
                    for (int i = 0; i < 3; i++) {
                        list2.add(mData.get(i).getStadiumname());
                    }
                    mContentBanner.setData(list, list2);
                    mContentBanner.setDelegate(new BGABanner.Delegate() {
                        @Override
                        public void onBannerItemClick(BGABanner banner, View itemView, @Nullable Object model, int position) {
                            Stadium stadium = mData.get(position);
                            Intent intent = new Intent(mContext, StadiumActivity.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putSerializable("user", user);
                            mBundle.putSerializable("stadium", stadium);
                            intent.putExtras(mBundle);
                            mContext.startActivity(intent);
                        }
                    });
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                    StadiumAdapter adapter = new StadiumAdapter(mContext, mData, user);
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setAdapter(adapter);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 2000);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("结果为空");
                List<Stadium> mData2 = new ArrayList<>();
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                StadiumAdapter adapter = new StadiumAdapter(mContext, mData2, user);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
                Toast.makeText(mContext, "该城市上没有体育场所加入", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void LoadingGongGao() {
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
            JSONObject json = new JSONObject();
            try {
                json.put("gonggao", 1);
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
            System.out.println("返回的数据：" + s);
            List<Notice> mData = new ArrayList<>();
            if (!"null".equals(s)) {
                try {
                    JSONArray results = new JSONArray(s);
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject js = results.getJSONObject(i);
                        Notice notice = new Notice();
                        notice.setContent(js.getString("content"));
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
            } else {
                System.out.println("结果为空");
                Toast.makeText(mContext, "该城市上没有体育场所加入", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void LoadingSports() {
        String url = URL_SPORTS;
        new SportsAsyncTask().execute(url);

    }

    @SuppressLint("StaticFieldLeak")
    private class SportsAsyncTask extends AsyncTask<String, Integer, String> {
        public SportsAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json = new JSONObject();
            try {
                json.put("sprots", 1);
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
            System.out.println("返回的数据：" + s);
            mData = new ArrayList<>();
            if (!"null".equals(s)) {
                try {
                    JSONArray results = new JSONArray(s);
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject js = results.getJSONObject(i);
                        App app = new App();
                        app.setName(js.getString("sportsname"));
                        app.setIcon(URL_SPORTSTYPE + js.getString("sportsicon"));
                        mData.add(app);
                    }
                    totalPage = (int) Math.ceil(mData.size() * 1.0 / mPageSize);
                    viewPagerList = new ArrayList<>();
                    for (int i2 = 0; i2 < totalPage; i2++) {
                        final GridView gridView = (GridView) View.inflate(mContext, R.layout.girdview_item, null);
                        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
                        gridView.setAdapter(new GridAdapter(mData, mContext, i2, mPageSize));
                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Object obj = gridView.getItemAtPosition(position);
                                Intent intent = new Intent(getActivity(), SearchStadiumActivity.class);
                                Bundle mBundle = new Bundle();
                                mBundle.putSerializable("city", tv_city.getText().toString());
                                mBundle.putSerializable("type", ((App) obj).getName());
                                mBundle.putSerializable("user", user);
                                intent.putExtras(mBundle);
                                startActivity(intent);
                            }
                        });
                        viewPagerList.add(gridView);
                    }
                    viewPager.setAdapter(new MyViewPagerAdapter(viewPagerList));
                    ivPoints = new ImageView[totalPage];
                    for (int i3 = 0; i3 < totalPage; i3++) {
                        ivPoints[i3] = new ImageView(mContext);
                        if (i3 == 0) {
                            ivPoints[i3].setImageResource(R.drawable.point1);
                        } else {
                            ivPoints[i3].setImageResource(R.drawable.point);
                        }
                        ivPoints[i3].setPadding(1, 1, 1, 1);
                        group.addView(ivPoints[i3]);
                    }
                    viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                        @Override
                        public void onPageSelected(int position) {
                            for (int i = 0; i < totalPage; i++) {
                                if (i == position) {
                                    ivPoints[i].setImageResource(R.drawable.point1);
                                } else {
                                    ivPoints[i].setImageResource(R.drawable.point);
                                }
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("结果为空");
                Toast.makeText(mContext, "获取失败", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void LoadingCitys() {
        String url = URL_CITY;
        new CitysAsyncTask().execute(url);

    }

    private class CitysAsyncTask extends AsyncTask<String, Integer, String> {
        public CitysAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json = new JSONObject();
            try {
                json.put("citys", 1);
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
            System.out.println("返回的数据：" + s);
            mDatas = new ArrayList();
            if (!"null".equals(s)) {
                try {
                    JSONArray results = new JSONArray(s);
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject js = results.getJSONObject(i);
                        String city = js.getString("cityname");
                        mDatas.add(city);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("结果为空");
                Toast.makeText(mContext, "获取城市失败", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void initLocation() {
        LocationClientOption localLocationClientOption = new LocationClientOption();
        localLocationClientOption.setIsNeedAddress(true);
        localLocationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocationClient.setLocOption(localLocationClientOption);
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    public void doSelect(View view) {
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
        mDialog.setDialogWindowAttr(0.9, 0.9, getActivity());
    }

    private class MyLocationListener implements BDLocationListener {
        private MyLocationListener() {
        }

        public void onReceiveLocation(final BDLocation BDLocation) {

            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    System.out.println("12" + BDLocation.getCity());
                    if ("".equals(BDLocation.getCity())) {
                        tv_city.setText("城市名");
                    } else {
                        tv_city.setText(BDLocation.getCity());
                        Loading(tv_city.getText().toString());
                        mLocationClient.stop();
                        return;
                    }
                }
            });
        }
    }


    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }
}
