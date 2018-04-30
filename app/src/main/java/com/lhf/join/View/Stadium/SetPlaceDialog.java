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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lhf.join.Adapter.BookAdapter;
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
    private ListView listView;
    private TextView tv;
    private SetPlaceListener setPlaceListener;
    private String place1;
    private Stadium mStadium;
    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");

    @SuppressLint("ValidFragment")
    public SetPlaceDialog(Stadium mStadium) {
        this.mStadium = mStadium;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = View.inflate(getContext(), R.layout.list_place,null);
        listView = view.findViewById(R.id.lv_place);
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
        getPlace(mStadium);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }
    public interface SetPlaceListener{
        void onSetPlaceComplete(String place1);
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
        setPlaceListener.onSetPlaceComplete(place1);
        super.onDestroy();
    }

    private void getPlace(Stadium stadium) {
        String loadingUrl = URL_PLACENAME;
        new getPlaceAsyncTask().execute(loadingUrl,String.valueOf(stadium.getStadiumId()));
    }

    private class getPlaceAsyncTask extends AsyncTask<String, Integer, String> {
        public getPlaceAsyncTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String results = null;
            JSONObject json=new JSONObject();
            try {
                json.put("stadiumId",params[1]);
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
                    for (int i=0;i<mData.size();i++){
                        place.add(mData.get(i).getPlacename());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_expandable_list_item_1,place);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            place1 = place.get(position);
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
