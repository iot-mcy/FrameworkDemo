package com.mcy.framework;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.mcy.framework.databinding.ActivityMainBinding;
import com.mcy.framework.rxjava.Disposables;
import com.mcy.framework.text.GetTradeQuotedPriceByID;
import com.mcy.framework.text.TestService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    public ObservableField<String> data = new ObservableField<>("hello world!");

    private ActivityMainBinding binding;

    private Disposables disposables = new Disposables();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        getData1();
//        getData2();
        getUserByUsername(1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.disposeAll();
    }

    private void getData1() {
        disposables.add(TestService.getData1()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GetTradeQuotedPriceByID>() {
                    @Override
                    public void accept(GetTradeQuotedPriceByID getTradeQuotedPriceByID) throws Exception {
                        if (getTradeQuotedPriceByID != null) {
                            String json = JSON.toJSONString(getTradeQuotedPriceByID);
                            Toast.makeText(MainActivity.this, "成功\n" + json, Toast.LENGTH_LONG).show();
                            data.set(json);
                        } else {
                            Toast.makeText(MainActivity.this, "失败\n", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "失败\n" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }));
    }

    private void getData2() {
        disposables.add(TestService.getData2()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GetTradeQuotedPriceByID>() {
                    @Override
                    public void accept(GetTradeQuotedPriceByID getTradeQuotedPriceByID) throws Exception {
                        if (getTradeQuotedPriceByID != null) {
                            String json = JSON.toJSONString(getTradeQuotedPriceByID);
                            Toast.makeText(MainActivity.this, "成功\n" + json, Toast.LENGTH_LONG).show();
                            data.set(json);
                        } else {
                            Toast.makeText(MainActivity.this, "失败\n", Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "失败\n" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }));
    }

    private void getUserByUsername(int userID) {
        disposables.add(TestService.getUserByUserID(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Toast.makeText(MainActivity.this, "成功\n" + s, Toast.LENGTH_LONG).show();
                        data.set(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "失败\n" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }));
    }
}
