package com.lhf.join.View.Collection;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lhf.join.Adapter.StadiumAdapter;
import com.lhf.join.Bean.Stadium;
import com.lhf.join.Bean.User;
import com.lhf.join.R;
import com.lhf.join.View.MainActivity;
import com.lhf.join.View.Stadium.SearchStadiumActivity;

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

import static com.lhf.join.Constant.Constant.URL_PICTURE;
import static com.lhf.join.Constant.Constant.URL_SEARCHCOLLECTSTADIUM;
import static com.lhf.join.Constant.Constant.URL_SEARCHSTADIUM;

public class MyCollection extends AppCompatActivity {
    private ImageView btn_back;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private User user;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection);
        initView();
        initData();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    private void initView() {
        btn_back = findViewById(R.id.icon_back);
        recyclerView = findViewById(R.id.rv_collect);
        layoutManager = new LinearLayoutManager(this);
        getWindow().setStatusBarColor(Color.parseColor("#FF029ACC"));
    }

    private void initData() {
        user = (User) getIntent().getSerializableExtra("user");
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        collectedstadium(user.getUserId());


    }

    private void collectedstadium(int userId) {
        String SearchUrl = URL_SEARCHCOLLECTSTADIUM;
        new collectedstadiumAsyncTask().execute(SearchUrl, String.valueOf(userId));
    }

    private class collectedstadiumAsyncTask extends AsyncTask<String, Integer, String> {
        public collectedstadiumAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json = new JSONObject();
            try {
                json.put("userId", params[1]);
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
            List<Stadium> mData = new ArrayList<>();
            if (!"null".equals(s)) {
                try {
                    JSONArray results = new JSONArray(s);
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject js = results.getJSONObject(i);
                        Stadium stadium = new Stadium();
                        stadium.setStadiumId(js.getInt("stadiumId"));
                        stadium.setStadiumname(js.getString("stadiumname"));
                        stadium.setStadiumtype(js.getString("stadiumtypeId"));
                        stadium.setArea(js.getString("area"));
                        stadium.setIndoor(js.getInt("indoor"));
                        stadium.setAircondition(js.getInt("aircondition"));
                        stadium.setCity(js.getString("city"));
                        stadium.setMainpicture(URL_PICTURE + js.getString("mainpicture"));
                        stadium.setAdress(js.getString("adress"));
                        stadium.setNum(js.getString("num"));
                        mData.add(stadium);
                    }
                    List<Stadium> mData2 = new ArrayList<>();
                    System.out.println("22");
                    for (int i = 0; i < mData.size(); i++) {
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
                        mData2.add(stadium);
                    }
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.addItemDecoration(new DividerItemDecoration(MyCollection.this, DividerItemDecoration.VERTICAL));
                    StadiumAdapter adapter = new StadiumAdapter(MyCollection.this, mData2, user);
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setAdapter(adapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("结果为空");
                List<Stadium> mData2 = new ArrayList<>();
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.addItemDecoration(new DividerItemDecoration(MyCollection.this, DividerItemDecoration.VERTICAL));
                StadiumAdapter adapter = new StadiumAdapter(MyCollection.this, mData2, user);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setAdapter(adapter);

            }
        }
    }
}
