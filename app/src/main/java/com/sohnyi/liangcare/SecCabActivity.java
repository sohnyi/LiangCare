package com.sohnyi.liangcare;

import android.support.v4.app.Fragment;

/**
 * Created by sohnyi on 2017/4/10.
 */

public class SecCabActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return SecCabFragment.newInstance();
    }
}
