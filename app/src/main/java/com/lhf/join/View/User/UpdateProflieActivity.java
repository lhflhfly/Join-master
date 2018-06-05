package com.lhf.join.View.User;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lhf.join.Bean.User;
import com.lhf.join.R;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;
import org.xutils.http.body.MultipartBody;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

import static com.lhf.join.Constant.Constant.URL_UPDATEPROFILE;

public class UpdateProflieActivity extends AppCompatActivity {
    private Button btn;
    private Button btn_xuanze;
    private String path = null;
    private User user;
    private TextView tv_progress;
    private ImageView icon_back;
    private ImageView icon_choice;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateproflie);
        initView();
        initData();
    }

    private void initView() {
        btn = findViewById(R.id.btn);
        btn_xuanze = findViewById(R.id.btn_xuancze);
        tv_progress = findViewById(R.id.tv_progress);
        icon_back = findViewById(R.id.icon_back);
        tv_progress.setVisibility(View.GONE);
        icon_choice = findViewById(R.id.icon_choice);
        progressBar = findViewById(R.id.progress);
        getWindow().setStatusBarColor(Color.parseColor("#FF029ACC"));
    }

    private void initData() {
        progressBar.setVisibility(View.GONE);
        user = (User) getIntent().getSerializableExtra("user");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(path==null){
                    Toast.makeText(UpdateProflieActivity.this,"请先选择图片",Toast.LENGTH_SHORT).show();
                }else {
                    multiFileUpload();
                }
            }
        });
        btn_xuanze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });
        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data==null){
            Toast.makeText(UpdateProflieActivity.this,"没有选择图片",Toast.LENGTH_SHORT).show();
        }else{
            if(requestCode==1)
            {
                //获得图片的uri
                Uri uri = data.getData();
                //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
                ContentResolver cr = this.getContentResolver();
                Bitmap bitmap;
                //Bitmap bm; //这是一种方式去读取图片
                try
                {
                    //bm = MediaStore.Images.Media.getBitmap(cr, uri);
                    //pic.setImageBitmap(bm);
                    bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    System.out.println("GOOD");
                    //第一种方式去读取路径
                    //String[] proj = {MediaStore.Images.Media.DATA};
                /*
                 //好像是android多媒体数据库的封装接口，具体的看Android文档
                Cursor cursor = managedQuery(uri, proj, null, null, null);
                //按我个人理解 这个是获得用户选择的图片的索引值
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
              //将光标移至开头 ，这个很重要，不小心很容易引起越界
                cursor.moveToFirst();
                //最后根据索引值获取图片路径
                String path = cursor.getString(column_index);
                System.out.println(path);
                   */
                    icon_choice.setImageBitmap(bitmap);
                    //第二种方式去读取路径
                    Cursor cursor =this.getContentResolver().query(uri, null, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    path = cursor.getString(column_index);
                    System.out.println(path);
                }
                catch (Exception e)
                {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                    System.out.println("BAD");
                }

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void multiFileUpload()
    {
        String mBaseUrl = URL_UPDATEPROFILE;
        RequestParams params = new RequestParams(mBaseUrl);
        //创建List<KeyValue>对象
        List<KeyValue> list = new ArrayList<>();
        //给list中添加数据，filePah是上传的文件路径，比如sd卡中图片
        list.add(new KeyValue("file", new File(path)));//文件流数据
        list.add(new KeyValue("userId",user.getUserId()));
        //其它参数，根据项目而定，比如我的项目中要传入的参数是json格式的
        //创建MultipartBody
        MultipartBody body = new MultipartBody(list, "UTF-8");
        //添加请求参数
        params.setRequestBody(body);
        x.http().post(params, new Callback.ProgressCallback<String>() {


            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                tv_progress.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax((int)total);
                progressBar.setProgress((int)current);

            }

            @Override
            public void onSuccess(String result) {
                progressBar.setVisibility(View.GONE);
                tv_progress.setVisibility(View.GONE);
                Toast.makeText(UpdateProflieActivity.this, "上传成功", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }


}


