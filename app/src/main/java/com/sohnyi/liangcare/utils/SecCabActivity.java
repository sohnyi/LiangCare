package com.sohnyi.liangcare.utils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.sohnyi.liangcare.R;
import com.sohnyi.liangcare.database.MyConstant;

public class SecCabActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private int type;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_audio:
                    type = MyConstant.TYPE_AUDIO;
                    return true;
                case R.id.navigation_video:
                    type = MyConstant.TYPE_VIDO;
                    return true;
                case R.id.navigation_photo:
                    type = MyConstant.TYPE_PHOTO;
                    return true;
                case R.id.navigation_document:
                    type = MyConstant.TYPE_DOC;
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec_cab);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

}
