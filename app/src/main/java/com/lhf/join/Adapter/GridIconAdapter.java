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


public class GridIconAdapter extends BaseAdapter {
    private List<String> mData;
    private Context mContext;

    public GridIconAdapter(List<String> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = View.inflate(mContext,R.layout.list_icon,null);
            holder.iv_nul = convertView.findViewById(R.id.img_icon);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        final int pos = position;
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(mContext);
        ImageLoader.getInstance().init(configuration);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.sportserror) // 设置图片加载或解码过程中发生错误显示的图片
                .showImageOnLoading(R.drawable.lodingsports)
                .resetViewBeforeLoading(false)  // default 设置图片在加载前是否重置、复位
                .delayBeforeLoading(0)  // 下载前的延迟时间
                .build();
        ImageLoader.getInstance().displayImage(mData.get(pos), holder.iv_nul,options);
        return convertView;
    }
    static class ViewHolder{
        private ImageView iv_nul;
    }
}
