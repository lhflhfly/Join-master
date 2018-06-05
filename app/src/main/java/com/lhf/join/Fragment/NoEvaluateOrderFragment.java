package com.lhf.join.Fragment;

import android.os.AsyncTask;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lhf.join.Adapter.BookAdapter;
import com.lhf.join.Base.BaseFragment;
import com.lhf.join.Bean.Book;
import com.lhf.join.Bean.User;
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

import static com.lhf.join.Constant.Constant.URL_EVALUATEINFORMATION;
import static com.lhf.join.Constant.Constant.URL_ORDERINFORMATION;
import static com.lhf.join.Constant.Constant.URL_PICTURE;
import static com.lhf.join.Constant.Constant.URL_PROFLIE;

public class NoEvaluateOrderFragment extends BaseFragment{
    private User user;
    private RecyclerView recyclerView;
    private TextView tv_noevaluation;
    private LinearLayoutManager layoutManager;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.nouse, null);
        recyclerView = view.findViewById(R.id.rv_nouse);
        layoutManager = new LinearLayoutManager(mContext);
        tv_noevaluation = view.findViewById(R.id.tv_noevaluation);
        return view;


    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void initData() {
        user = (User) getActivity().getIntent().getSerializableExtra("user");
        orderInformation_nouse(user);


    }

    private void orderInformation_nouse(User user) {
        String loadingUrl = URL_EVALUATEINFORMATION;
        new orderInformationAsyncTask().execute(loadingUrl, String.valueOf(user.getUserId()));
    }

    private class orderInformationAsyncTask extends AsyncTask<String, Integer, String> {
        public orderInformationAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            int method = 1;
            JSONObject json = new JSONObject();
            try {
                json.put("userId", params[1]);
                json.put("method", method);
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
            List<Book> mData = new ArrayList<>();
            if (!"null".equals(s)) {
                try {
                    JSONArray results = new JSONArray(s);
                    for (int i=results.length()-1;i>=0;i--) {
                        JSONObject js = results.getJSONObject(i);
                        Book book = new Book();
                        book.setUserId(user.getUserId());
                        book.setBookingId(js.getInt("bookingId"));
                        book.setStadiumname(js.getString("stadiumname"));
                        book.setPlaceName(js.getString("placename"));
                        book.setTime(js.getString("time"));
                        book.setTime_order(js.getString("time_order"));
                        book.setStadiumpicture(URL_PICTURE+js.getString("mainpicture"));
                        book.setStadiumId(js.getInt("stadiumId"));
                        mData.add(book);
                    }
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                    BookAdapter adapter = new BookAdapter(mContext, mData,0);
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("结果为空");
                tv_noevaluation.setVisibility(View.VISIBLE);
                tv_noevaluation.setText("当前没有待评论的预约订单");
                List<Book> mData2 = new ArrayList<>();
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                BookAdapter adapter = new BookAdapter(mContext, mData2,0);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setAdapter(adapter);

            }
        }
    }
}
