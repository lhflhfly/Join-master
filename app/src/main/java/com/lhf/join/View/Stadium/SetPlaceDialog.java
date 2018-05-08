package com.lhf.join.View.Stadium;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lhf.join.Adapter.BookAdapter;
import com.lhf.join.Adapter.SetPlaceAdapter;
import com.lhf.join.Bean.Book;
import com.lhf.join.Bean.Place;
import com.lhf.join.Bean.Stadium;
import com.lhf.join.Bean.User;
import com.lhf.join.R;
import com.lhf.join.View.User.OrderInformationActivity;

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

import static com.lhf.join.Constant.Constant.URL_ORDERINFORMATION;
import static com.lhf.join.Constant.Constant.URL_PLACENAME;

@SuppressLint("ValidFragment")
public class SetPlaceDialog extends DialogFragment{
    private List<String> place = new ArrayList<>();
    private RecyclerView recyclerView;
    private Place place_set;
    private LinearLayoutManager layoutManager;
    private SetPlaceListener setPlaceListener;
    private Stadium mStadium;
    private String mTime;
    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");

    @SuppressLint("ValidFragment")
    public SetPlaceDialog(Stadium mStadium,String time) {
        this.mStadium = mStadium;
        this.mTime = time;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = View.inflate(getContext(), R.layout.list_place,null);
        recyclerView = view.findViewById(R.id.rv_place);
        layoutManager = new LinearLayoutManager(getContext());
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
        getPlace(mStadium,mTime);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }
    public interface SetPlaceListener{
        void onSetPlaceComplete(Place place);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            setPlaceListener = (SetPlaceListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public void onDestroy() {
        setPlaceListener.onSetPlaceComplete(place_set);
        super.onDestroy();
    }

    private void getPlace(Stadium stadium,String time) {
        String loadingUrl = URL_PLACENAME;
        new getPlaceAsyncTask().execute(loadingUrl,String.valueOf(stadium.getStadiumId()),time);
    }

    private class getPlaceAsyncTask extends AsyncTask<String, Integer, String> {
        public getPlaceAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            System.out.println("000"+params[2]);
            JSONObject json=new JSONObject();
            try {
                json.put("stadiumId",params[1]);
                json.put("timeorder",params[2]);
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
            List<Place> mData = new ArrayList<>();
            if (!"null".equals(s)){
                try {
                    JSONArray results = new JSONArray(s);
                    for(int i=0;i<results.length();i++){
                        JSONObject js= results.getJSONObject(i);
                        Place place = new Place();
                        place.setStadiumId(js.getInt("stadiumId"));
                        place.setPlaceId(js.getInt("placeId"));
                        place.setState(js.getString("state"));
                        place.setMaterial(js.getString("material"));
                        place.setPlacename(js.getString("placename"));
                        mData.add(place);
                    }
                    SetPlaceAdapter adapter = new SetPlaceAdapter(getContext(),mData);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(new SetPlaceAdapter.OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(Place place) {
                            place_set =place;
                            onDestroy();
                            onDismiss(getDialog());
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                System.out.println("结果为空");
                Toast.makeText(getContext(),"该场馆暂时没添加场地",Toast.LENGTH_LONG).show();

            }
        }
    }

}
