package com.mcy.framework.AppUpdate;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

import static com.mcy.framework.AppUpdate.InstallApk.GET_UNKNOWN_APP_SOURCES;

public class ActionManageUnknownAPPSourcesActivity extends AppCompatActivity {

    private static final String TAG = "ActionManageUnknownAPPSourcesActivity";
    private File file;
    private String fileName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        fileName = getIntent().getStringExtra("");
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        startActivityForResult(intent, GET_UNKNOWN_APP_SOURCES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_UNKNOWN_APP_SOURCES) {
//            checkOreoPermission(this);
            finish();
        }
    }
}
