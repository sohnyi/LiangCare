package com.sohnyi.liangcare;

import android.support.v4.app.Fragment;

import com.sohnyi.liangcare.models.SingleFragmentActivity;

/**
 * Created by sohnyi on 2017/4/10.
 */

public class SecCabActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return SecCabFragment.newInstance();
    }
}
