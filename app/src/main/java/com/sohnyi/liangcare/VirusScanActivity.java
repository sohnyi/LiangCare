package com.sohnyi.liangcare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sohnyi.liangcare.utils.ToastUtil;

public class VirusScanActivity extends AppCompatActivity {
    private static final String TAG = "VirusScanActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virus_scan);

        ToastUtil.showToast(this, TAG);
    }
}
