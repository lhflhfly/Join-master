package com.lhf.join.View;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lhf.join.R;


/**
 * Created by whieenz on 2017/7/19.
 */

public class DialogSearchView extends LinearLayout implements View.OnClickListener {

    /**
     * 输入框 
     */
    private EditText etInput;

    /**
     * 删除键 
     */
    private ImageView ivDelete;

    /**
     * 上下文对象 
     */
    private Context mContext;

    /**
     * 搜索回调接口 
     */
    private DialogSearchViewListener mListener;

    /**
     * 设置搜索回调接口 
     *
     * @param listener 监听者 
     */
    public void setDialogSearchViewListener(DialogSearchViewListener listener) {
        mListener = listener;
    }

    public DialogSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_search_layout, this);
        initViews();
    }

    private void initViews() {
        etInput = (EditText) findViewById(R.id.et_search_text);
        ivDelete = (ImageView) findViewById(R.id.imb_search_clear);
        ivDelete.setOnClickListener(this);
        etInput.addTextChangedListener(new EditChangedListener());
        etInput.setOnClickListener(this);

    }

    private class EditChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (!"".equals(charSequence.toString())) {
                ivDelete.setVisibility(VISIBLE);
                //更新autoComplete数据
                if (mListener != null) {
                    mListener.onQueryTextChange(charSequence + "");
                }
            } else {
                ivDelete.setVisibility(GONE);
            }

        }
        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imb_search_clear:
                etInput.setText("");
                if (mListener != null) {
                    mListener.onQueryTextChange("");
                }
                ivDelete.setVisibility(GONE);
                break;
        }
    }
    /**
     * search view回调方法 
     */
    public interface DialogSearchViewListener {
        boolean onQueryTextChange(String text);
    }
}  