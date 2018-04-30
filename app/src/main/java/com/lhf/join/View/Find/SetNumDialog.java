package com.lhf.join.View.Find;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lhf.join.Bean.Place;
import com.lhf.join.Bean.Stadium;
import com.lhf.join.R;
import com.lhf.join.View.EasyPickerView;

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

import static com.lhf.join.Constant.Constant.URL_PLACENAME;

@SuppressLint("ValidFragment")
public class SetNumDialog extends DialogFragment{
    private SetNumListener setNumListener;
    private EasyPickerView easyPickerView_num;
    private Button btn_confirm;
    private int num;
    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setGravity(Gravity.BOTTOM);//Dialog处于页面的底部
        getDialog().setCanceledOnTouchOutside(true);//点击Dialog外围可以消除Dialog
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,100);//设置高宽
        getDialog().setCanceledOnTouchOutside(false);
        View view = View.inflate(getContext(), R.layout.set_num,null);
        easyPickerView_num= view.findViewById(R.id.epv_num);
        btn_confirm = view.findViewById(R.id.btn_confirm);

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
        initNum();
        System.out.println("--------------->"+num);

    }

    private void initNum() {
        final ArrayList<String> numList = new ArrayList<>();
        for (int i=1;i<21;i++){
             numList.add(String.valueOf(i));
        }
        easyPickerView_num.setDataList(numList);
        easyPickerView_num.setOnScrollChangedListener(new EasyPickerView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int curIndex) {
                num = Integer.parseInt(numList.get(curIndex));
            }

            @Override
            public void onScrollFinished(int curIndex) {
                num = Integer.parseInt(numList.get(curIndex));

            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNumListener.onSetPlaceComplete(num);
                dismiss();
            }
        });

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }
    public interface SetNumListener{
        void onSetPlaceComplete(int num1);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            setNumListener = (SetNumListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }




}
