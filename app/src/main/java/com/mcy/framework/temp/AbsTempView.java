package com.mcy.framework.temp;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public abstract class AbsTempView extends LinearLayout implements ITempView {
    private static String TAG = "AbsTempView";
    protected int mType = ERROR;
    private OnTempBtClickListener mBtListener;

    public AbsTempView(Context context) {
        this(context, null);
    }

    public AbsTempView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(setLayoutId(), this);
        init(view);
    }

    protected abstract void init(View view);

    /**
     * 如果界面有按钮，则需要对着按钮的点击事件进行监听
     */
    public void setBtListener(@NonNull OnTempBtClickListener listener) {
        mBtListener = listener;
    }

    protected abstract int setLayoutId();

    /**
     * 将按钮点击事件传递给TempView调用类
     *
     * @param type {@link ITempView}
     */
    protected void onTempBtClick(View view, int type) {
        if (mBtListener != null) {
            mBtListener.onBtTempClick(view, type);
        }
    }

    @Override
    public void setType(int type) {
        mType = type;
        if (type == LOADING) {
            onLoading();
            return;
        }
        if (type == ERROR) {
            onError();
        } else if (type == DATA_NULL) {
            onNull();
        } else {
            Log.e(TAG, "类型错误");
        }
    }
}
