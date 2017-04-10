package com.sohnyi.liangcare;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sohnyi on 2017/4/10.
 */

public class SecCabFragment extends Fragment {

    public static SecCabFragment newInstance() {
        return new SecCabFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sec_cab, container, false);

        initViews(view);



        return view;
    }

    public void initViews(View v) {

    }

}
