package com.lhf.join.View.Find;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lhf.join.Bean.Stadium;
import com.lhf.join.R;
import com.lhf.join.View.EasyPickerView;

import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.MediaType;

@SuppressLint("ValidFragment")
public class SetTimeDialog extends DialogFragment{
    private SetTimeListener setTimeListener;
    private EasyPickerView easyPickerView_h;
    private EasyPickerView easyPickerView_m;
    private Button btn_confirm;
    private String time;
    private String hour;
    private Stadium mStadium;
    private String mDay;
    public static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
    public SetTimeDialog(Stadium stadium,String day){
        this.mStadium = stadium;
        this.mDay = day;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setGravity(Gravity.BOTTOM);//Dialog处于页面的底部
        getDialog().setCanceledOnTouchOutside(true);//点击Dialog外围可以消除Dialog
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,100);//设置高宽
        getDialog().setCanceledOnTouchOutside(false);
        View view = View.inflate(getContext(), R.layout.set_time,null);
        easyPickerView_h = view.findViewById(R.id.epv_h);
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
        initTime();

    }

    private void initTime() {
        final ArrayList<String> numList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String this_day = (year + "年" + (month + 1) + "月" + day + "日");
        int i1=0;
        if(this_day.equals(mDay)){
            int time_this=calendar.get(Calendar.HOUR_OF_DAY);
            if(time_this<Integer.parseInt(mStadium.getOpentime())){
                i1 = Integer.parseInt(mStadium.getOpentime());
            }else {
                i1 = time_this+1;
            }
            if(time_this>Integer.parseInt(mStadium.getClosetime())){
                numList.add("该场馆今日已休息");
            }else{
            for (int i=i1;i<Integer.parseInt(mStadium.getClosetime());i=i+1){
                numList.add(String.valueOf(i)+":00--"+String.valueOf(i+1)+":00");
            }
            }
        }else {
            i1 = Integer.parseInt(mStadium.getOpentime());
            for (int i=i1;i<Integer.parseInt(mStadium.getClosetime());i=i+1){
                numList.add(String.valueOf(i)+":00--"+String.valueOf(i+1)+":00");
            }
        }

        easyPickerView_h.setDataList(numList);
        easyPickerView_h.setOnScrollChangedListener(new EasyPickerView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int curIndex) {
                hour = numList.get(curIndex);
                time = String.valueOf(hour);
            }

            @Override
            public void onScrollFinished(int curIndex) {
                hour = (numList.get(curIndex));
                time = String.valueOf(hour);

            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("------------------》"+time);
                setTimeListener.onSetNumLComplete(time);
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
    public interface SetTimeListener{
        void onSetNumLComplete(String time);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            setTimeListener = (SetTimeListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }




}
