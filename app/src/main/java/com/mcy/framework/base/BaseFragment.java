package com.mcy.framework.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * BaseFragment
 */
public abstract class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";

    protected RxPermissions rxPermissions;

    protected ViewDataBinding viewDataBinding;

    /**
     * 是否完成View初始化
     */
    private boolean isOk = false;
    /**
     * 是否为第一次加载
     */
    private boolean isFirst = true;

    public BaseFragment() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rxPermissions = new RxPermissions(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewDataBinding = DataBindingUtil.inflate(inflater, setLayoutId(), container, false);
        return viewDataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        // 完成initView后改变view的初始化状态为完成
        isOk = true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 在onResume中进行数据懒加载
        initLoadData();
    }

    private void initLoadData() {
        // 加载数据时判断是否完成view的初始化，以及是不是第一次加载此数据
        if (isOk && isFirst) {
            loadData();
            // 加载第一次数据后改变状态，后续不再重复加载
            isFirst = false;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Object myEvent) {
        // TODO: 2021/4/20
    }

    /**
     * 设子fragment的LayoutID
     *
     * @return
     */
    protected abstract int setLayoutId();

    /**
     * 子fragment实现初始化
     */
    protected abstract void initView();

    /**
     * 子fragment实现懒加载的方法
     */
    protected abstract void loadData();


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}