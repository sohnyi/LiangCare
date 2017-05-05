package com.sohnyi.liangcare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.sohnyi.liangcare.utils.ToastUtil;

/**
 * Created by sohnyi on 2017/4/13.
 */

public class SecCabFileListActivity extends FragmentActivity {
    private static final String FILE_TYPE =
            "com.sohnyi.liangcare.file_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec_cab_file_list);

        int file_type = (int) getIntent().getSerializableExtra(FILE_TYPE);
        ToastUtil.showToast(getApplicationContext(), "file type:" + file_type);

        // TODO: 2017/4/13 create a file find util. 
    }

    public static Intent newIntent(Context packageContext, int fileType) {
        Intent intent = new Intent(packageContext, SecCabFileListActivity.class);
        intent.putExtra(FILE_TYPE, fileType);
        return intent;
    }
}
