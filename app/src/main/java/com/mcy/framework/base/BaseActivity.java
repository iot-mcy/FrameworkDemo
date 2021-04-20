package com.mcy.framework.base;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.mcy.framework.temp.AbsTempView;
import com.mcy.framework.temp.OnTempBtClickListener;
import com.mcy.framework.temp.TempView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class BaseActivity extends AppCompatActivity implements OnTempBtClickListener {

    protected Intent intent;

    protected RxPermissions rxPermissions;

    protected ViewDataBinding viewDataBinding;

    protected AbsTempView mTempView;
    protected boolean useTempView = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewDataBinding = DataBindingUtil.setContentView(this, setLayoutId());
        intent = getIntent();
        rxPermissions = new RxPermissions(this);
        EventBus.getDefault().register(this);

        initView();
        if (useTempView) {
            mTempView = new TempView(this);
            mTempView.setBtListener(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Object myEvent) {

    }

    protected abstract int setLayoutId();

    protected abstract void initView();

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 获取填充View
     */
    protected AbsTempView getTempView() {
        return mTempView;
    }

    /**
     * 是否使用填充界面
     */
    protected void setUseTempView(boolean useTempView) {
        this.useTempView = useTempView;
    }

    /**
     * 显示占位布局
     *
     * @param type {@link TempView#ERROR}
     *             {@link TempView#DATA_NULL}
     *             {@link TempView#LOADING}
     */
    protected void showTempView(int type) {
        if (mTempView == null || !useTempView) {
            return;
        }
        mTempView.setVisibility(View.VISIBLE);
        mTempView.setType(type);
        setContentView(mTempView);
    }

    /**
     * 关闭占位布局
     */
    protected void hintTempView() {
        hintTempView(0);
    }

    /**
     * 延时关闭占位布局
     */
    protected void hintTempView(int delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mTempView == null || !useTempView) {
                    return;
                }
                mTempView.clearFocus();
                mTempView.setVisibility(View.GONE);
                setContentView(viewDataBinding.getRoot());
            }
        }, delay);
    }

    @Override
    public void onBtTempClick(View view, int type) {

    }
}