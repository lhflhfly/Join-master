package com.lhf.join.View.Find;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lhf.join.Adapter.SetStadiumAdapter;
import com.lhf.join.Adapter.StadiumAdapter;
import com.lhf.join.Bean.Place;
import com.lhf.join.Bean.Stadium;
import com.lhf.join.R;
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
import static com.lhf.join.Constant.Constant.URL_PLACENAME;
import static com.lhf.join.Constant.Constant.URL_SEARCHSTADIUM;

@SuppressLint("ValidFragment")
public class SetStadiumDialog extends DialogFragment{
    private List<String> place = new ArrayList<>();
    private ListView listView;
    private EditText et_search;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private SetStadiumListener setPlaceListener;
    private String city;
    private Stadium stadium_set;
    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Window window = getDialog().getWindow();
        View view = View.inflate(getContext(), R.layout.set_stadium,null);
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        window.setLayout(200,100);
        et_search = view.findViewById(R.id.et_search_text);
        recyclerView = view.findViewById(R.id.rv_search);

        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_MinWidth);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        city =(String)getActivity().getIntent().getSerializableExtra("city");
        city = "成都市";
        System.out.println(city);
        Search("",city);
        layoutManager = new LinearLayoutManager(getContext());
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String stadiuname = et_search.getText().toString();
                    System.out.println("11");
                    Search(stadiuname,city);
                    System.out.println("22");
                    return false;
                }
                return false;
            }
        });

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }
    public interface SetStadiumListener{
        void onSetStadiumComplete(Stadium stadium);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            setPlaceListener = (SetStadiumListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public void onDestroy() {
        setPlaceListener.onSetStadiumComplete(stadium_set);
        super.onDestroy();
    }

    private void Search(String stadiuname,String city) {
        String SearchUrl = URL_SEARCHSTADIUM;
        new SearchAsyncTask().execute(SearchUrl,stadiuname,city);
    }
    private class SearchAsyncTask extends AsyncTask<String, Integer, String> {
        public SearchAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json=new JSONObject();
            try {
                json.put("stadiumname",params[1]);
                json.put("city",params[2]);
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
                        mData.add(stadium);
                    }
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
                        mData2.add(stadium);
                    }
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
                    SetStadiumAdapter adapter = new SetStadiumAdapter(getContext(),mData2);
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(new SetStadiumAdapter.OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(Stadium stadium) {
                            stadium_set = stadium;
                            onDestroy();
                            onDismiss(getDialog());
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                System.out.println("结果为空");
                List<Stadium> mData2 = new ArrayList<>();
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
                SetStadiumAdapter adapter = new SetStadiumAdapter(getContext(),mData2);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setAdapter(adapter);
                Toast.makeText(getContext(),"没有查询到该场馆",Toast.LENGTH_SHORT).show();

            }
        }
    }

}
