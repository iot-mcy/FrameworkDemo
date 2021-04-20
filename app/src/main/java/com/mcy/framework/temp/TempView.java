package com.mcy.framework.temp;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mcy.framework.R;

public class TempView extends AbsTempView {
    ProgressBar mPb;
    TextView mHint;
    Button mBt;
    LinearLayout mErrorContent;

    public TempView(Context context) {
        super(context);
    }

    @Override
    protected void init(View view) {
        mPb = view.findViewById(R.id.pb);
        mHint = view.findViewById(R.id.hint);
        mBt = view.findViewById(R.id.bt);
        mErrorContent = view.findViewById(R.id.error);

        mBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onTempBtClick(v, mType);
            }
        });
    }

    @Override
    protected int setLayoutId() {
        return R.layout.layout_error_temp;
    }

    @Override
    public void onError() {
        mErrorContent.setVisibility(VISIBLE);
        mPb.setVisibility(GONE);
        mHint.setText("网络错误");
        mBt.setText("点击刷新");
    }

    @Override
    public void onNull() {
        mErrorContent.setVisibility(VISIBLE);
        mPb.setVisibility(GONE);
        mHint.setText("数据为空");
        mBt.setText("点击刷新");
    }

    @Override
    public void onLoading() {
        mErrorContent.setVisibility(GONE);
        mPb.setVisibility(VISIBLE);
    }
}
