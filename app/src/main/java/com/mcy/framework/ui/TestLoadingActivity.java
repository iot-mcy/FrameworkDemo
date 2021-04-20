package com.mcy.framework.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.mcy.framework.R;
import com.mcy.framework.base.BaseActivity;
import com.mcy.framework.databinding.ActivityTestLoadingBinding;
import com.mcy.framework.temp.ITempView;

import java.util.ArrayList;

public class TestLoadingActivity extends BaseActivity {

    private ActivityTestLoadingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showTempView(ITempView.LOADING);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hintTempView();
            }
        }, 2000);
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_test_loading;
    }

    @Override
    protected void initView() {
        binding = (ActivityTestLoadingBinding) viewDataBinding;
        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            fragments.add(BlankFragment.newInstance(String.valueOf(i), ""));
            strings.add(String.valueOf(i));
        }

        adapter.setFragments(fragments);
        adapter.setTabNames(strings);
        binding.viewPage.setAdapter(adapter);
        binding.viewPage.setOffscreenPageLimit(3);
    }

    private ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        private FragmentManager mFragmentManager;
        private FragmentTransaction mCurTransaction;
        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> tabNames = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

        public void setFragments(ArrayList<Fragment> fragments) {
            this.fragments = fragments;
        }

        public ArrayList<Fragment> getFragments() {
            return fragments;
        }

        public void setTabNames(ArrayList<String> tabNames) {
            this.tabNames = tabNames;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        /**
         * 清除缓存fragment
         *
         * @param container ViewPager
         */
        public void clear(ViewGroup container) {
            if (this.mCurTransaction == null) {
                this.mCurTransaction = this.mFragmentManager.beginTransaction();
            }

            for (int i = 0; i < fragments.size(); i++) {
                long itemId = this.getItemId(i);
                String name = makeFragmentName(container.getId(), itemId);
                Fragment fragment = this.mFragmentManager.findFragmentByTag(name);
                if (fragment != null) {//根据对应的ID，找到fragment，删除
                    mCurTransaction.remove(fragment);
                }
            }
            mCurTransaction.commitNowAllowingStateLoss();
        }

        /**
         * 等同于FragmentPagerAdapter的makeFragmentName方法，
         * 由于父类的该方法是私有的，所以在此重新定义
         *
         * @param viewId
         * @param id
         * @return
         */
        private String makeFragmentName(int viewId, long id) {
            return "android:switcher:" + viewId + ":" + id;
        }
    }
}