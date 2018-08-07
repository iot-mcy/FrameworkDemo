package com.mcy.framework;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.mcy.framework.databinding.ActivityMainBinding;
import com.mcy.framework.rxjava.Disposables;
import com.mcy.framework.service.JsonCallback;
import com.mcy.framework.service.ServerUtil;
import com.mcy.framework.text.GetTradeQuotedPriceByID;
import com.mcy.framework.text.TestService;
import com.mcy.framework.text.TextServiceInterface;

import java.io.IOException;

import io.reactivex.Observable;
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
        getData3();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.disposeAll();
    }

    private void getData() {
        data.set("getData");


        JSONObject object = new JSONObject();
        object.put("ID", "1000");

        Observable<String> observable = ServerUtil.getInstance().create(TextServiceInterface.class).getTradeQuotedPriceList(object);

        ServerUtil.getInstance().requestServer(observable, new JsonCallback<GetTradeQuotedPriceByID>() {

            @Override
            public void onFailure() {
                Toast.makeText(MainActivity.this, "出错了！！！", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(GetTradeQuotedPriceByID object) throws IOException {
                Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_LONG).show();
                if (object != null) {
                    data.set("" + object.getID());
                }
            }
        });
    }

    private void getData2() {
        JSONObject object = new JSONObject();
        object.put("ID", 1000);
        Observable<String> observable = ServerUtil.getInstance().create(TextServiceInterface.class).getTradeQuotedPriceList(object);

        disposables.add(ServerUtil.getInstance().requestServer2(observable, new JsonCallback<String>() {

            @Override
            public void onFailure() {
                Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(String object) throws IOException {
                Toast.makeText(MainActivity.this, "成功\n" + object, Toast.LENGTH_LONG).show();

            }
        }));
    }

    private void getData3() {
        disposables.add(TestService.getData1()
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
                        Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_LONG).show();
                    }
                }));
    }
}
