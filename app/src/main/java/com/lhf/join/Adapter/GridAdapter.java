package com.lhf.join.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lhf.join.Bean.App;
import com.lhf.join.Bean.User;
import com.lhf.join.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;


public class GridAdapter extends BaseAdapter {
    private List<App> mData;
    private Context mContext;
    private int mIndex;
    private int mPargerSize;
    private User mUser;

    public GridAdapter(List<App> mData, Context mContext,int mIndex,int mPargerSize) {
        this.mData = mData;
        this.mContext = mContext;
        this.mIndex = mIndex;
        this.mPargerSize = mPargerSize;

    }

    @Override
    public int getCount() {
        return mData.size()>(mIndex+1)* mPargerSize ? mPargerSize :(mData.size() - mIndex*mPargerSize);
    }

    @Override
    public App getItem(int position) {
        return mData.get(position+mIndex*mPargerSize);
    }

    @Override
    public long getItemId(int position) {
        return position+ mIndex*mPargerSize;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = View.inflate(mContext,R.layout.item_app,null);
            holder.tv_name = convertView.findViewById(R.id.name);
            holder.iv_nul = convertView.findViewById(R.id.img_icon);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        final int pos = position +mIndex*mPargerSize;
        holder.tv_name.setText(mData.get(pos).getName());
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(mContext);
        ImageLoader.getInstance().init(configuration);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.sportserror) // 设置图片加载或解码过程中发生错误显示的图片
                .showImageOnLoading(R.drawable.lodingsports)
                .resetViewBeforeLoading(false)  // default 设置图片在加载前是否重置、复位
                .delayBeforeLoading(0)  // 下载前的延迟时间
                .build();
        ImageLoader.getInstance().displayImage(mData.get(pos).getIcon(), holder.iv_nul,options);
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext,SearchStadiumActivity.class);
//                Bundle mBundle = new Bundle();
//                mBundle.putSerializable("type",mData.get(pos).getName());
//                mBundle.putSerializable("user",mUser);
//                intent.putExtras(mBundle);
//                mContext.startActivity(intent);
//
//            }
//        });
        return convertView;
    }
    static class ViewHolder{
        private TextView tv_name;
        private ImageView iv_nul;
    }
}
