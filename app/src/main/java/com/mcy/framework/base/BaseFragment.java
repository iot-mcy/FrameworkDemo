package com.mcy.framework.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentContainer;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mcy.framework.temp.AbsTempView;
import com.mcy.framework.temp.OnTempBtClickListener;
import com.mcy.framework.temp.TempView;
import com.mcy.framework.utils.ReflectionUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;

/**
 * BaseFragment
 */
public abstract class BaseFragment extends Fragment implements OnTempBtClickListener {
    private static final String TAG = "BaseFragment";

    protected RxPermissions rxPermissions;

    protected ViewDataBinding viewDataBinding;

    private ViewGroup mParent;
    protected AbsTempView mTempView;
    protected boolean useTempView = true;

    private boolean isFirst = true; // 是否为第一次加载
    private boolean isView = false; //view是否加载完（防止空指针）
    private boolean isVisible = false; //是否对用户可见

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
        if (useTempView) {
            mTempView = new TempView(getContext());
            mTempView.setBtListener(this);
        }
        isView = true;
        return viewDataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Field field = ReflectionUtil.getField(getClass(), "mContainer");
        try {
            mParent = (ViewGroup) field.get(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        initView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Field field = ReflectionUtil.getField(getClass(), "mContainerId");
        Field containerField = ReflectionUtil.getField(getFragmentManager().getClass(), "mContainer");
        try {
            int id = (int) field.get(this);
            FragmentContainer container = (FragmentContainer) containerField.get(getFragmentManager());
            mParent = (ViewGroup) container.onFindViewById(id);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isVisible = true;
            myLoadData();
        } else {
            isVisible = false;
        }
    }

    private void myLoadData() {
        if (isFirst && isVisible && isView) { // 加载数据时判断是否完成view的初始化，以及是不是第一次加载此数据,以及用户是否切换到了这个fragment
            loadData();
            isFirst = false; // 加载第一次数据后改变状态，后续不再重复加载
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Object myEvent) {
        // TODO: 2021/4/20
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
        mTempView.setType(type);
        if (mParent != null) {
            int size = ViewGroup.LayoutParams.MATCH_PARENT;
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(size, size);
            if (mParent instanceof ViewPager) {
                //                ViewPager vp = (ViewPager) mParent;
                //                int position = vp.getCurrentItem();
                //                View child = vp.getChildAt(position);
                //                L.d(TAG, "hashcode ==> " + child.hashCode());
                View child = viewDataBinding.getRoot();
                if (child instanceof LinearLayout) {
                    LinearLayout ll = (LinearLayout) child;
                    ll.removeView(mTempView);
                    ll.addView(mTempView, 0, lp);
                } else if (child instanceof RelativeLayout || child instanceof FrameLayout) {
                    ViewGroup vg = (ViewGroup) child;
                    vg.removeView(mTempView);
                    vg.addView(mTempView, lp);
                } else {
                    Log.e(TAG, "框架的填充只支持，LinearLayout、RelativeLayout、FrameLayout");
                }
            } else {
                mParent.removeView(mTempView);
                mParent.addView(mTempView, lp);
            }
        }
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
                if (mParent != null) {
                    if (mParent instanceof ViewPager) {
                        //                        ViewPager vp = (ViewPager) mParent;
                        //                        int position = vp.getCurrentItem();
                        //                        View child = vp.getChildAt(position);
                        View child = viewDataBinding.getRoot();
                        ViewGroup vg = (ViewGroup) child;
                        if (vg != null) {
                            vg.removeView(mTempView);
                        }
                    } else {
                        mParent.removeView(mTempView);
                    }
                }
            }
        }, delay);
    }

    @Override
    public void onBtTempClick(View view, int type) {
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