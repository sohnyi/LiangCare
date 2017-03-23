package com.sohnyi.liangcare;

import android.support.v4.app.Fragment;

/**
 * Created by sohnyi on 2017/3/12.
 */

public class AppLockActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return AppLockFragment.newInstance();
    }
}
