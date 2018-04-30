package com.lhf.join.View.Stadium;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lhf.join.Adapter.StadiumAdapter;
import com.lhf.join.Bean.Changguan;
import com.lhf.join.Bean.Stadium;
import com.lhf.join.Bean.User;
import com.lhf.join.Fragment.OrderFragment;
import com.lhf.join.R;

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
import static com.lhf.join.Constant.Constant.URL_PICTURE;
import static com.lhf.join.Constant.Constant.URL_SEARCHSTADIUM;

public class SearchStadiumActivity extends AppCompatActivity {
    private EditText et_search;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private String city;
    private User user;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#FF029ACC"));
        setContentView(R.layout.activity_search_stadium);
        initview();
        initdata();
    }


    private void initview() {

        et_search = findViewById(R.id.et_search_text);
        recyclerView = findViewById(R.id.rv_search);
        layoutManager = new LinearLayoutManager(this);


    }

    private void initdata() {
        user = (User) getIntent().getSerializableExtra("user");
        String type = (String) getIntent().getSerializableExtra("type");
        city = (String) getIntent().getSerializableExtra("city");
        System.out.println("type:" + type);
        System.out.println("city:" + city);
        if (type != null) {
            Search(type, city);
            et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        String stadiuname = et_search.getText().toString();
                        System.out.println("11");
                        Search(stadiuname, city);
                        System.out.println("22");
                        return false;
                    }
                    return false;
                }
            });
        } else {
            et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        String stadiuname = et_search.getText().toString();
                        System.out.println("11");
                        Search(stadiuname, city);
                        System.out.println("22");
                        return false;
                    }
                    return false;
                }
            });
        }



    }

    private void Search(String stadiuname, String city) {
        String SearchUrl = URL_SEARCHSTADIUM;
        new SearchAsyncTask().execute(SearchUrl, stadiuname, city);
    }

    private class SearchAsyncTask extends AsyncTask<String, Integer, String> {
        public SearchAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json = new JSONObject();
            try {
                json.put("stadiumname", params[1]);
                json.put("city", params[2]);
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
                        stadium.setOpentime(js.getString("opentime"));
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
                        stadium.setOpentime(mData.get(i).getOpentime());
                        mData2.add(stadium);
                    }
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.addItemDecoration(new DividerItemDecoration(SearchStadiumActivity.this, DividerItemDecoration.VERTICAL));
                    StadiumAdapter adapter = new StadiumAdapter(SearchStadiumActivity.this, mData2, user);
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setAdapter(adapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("结果为空");
                List<Stadium> mData2 = new ArrayList<>();
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.addItemDecoration(new DividerItemDecoration(SearchStadiumActivity.this, DividerItemDecoration.VERTICAL));
                StadiumAdapter adapter = new StadiumAdapter(SearchStadiumActivity.this, mData2, user);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setAdapter(adapter);
                Toast.makeText(SearchStadiumActivity.this, "没有查询到该场馆", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
