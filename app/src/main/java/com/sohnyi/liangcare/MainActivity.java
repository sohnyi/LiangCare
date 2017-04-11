package com.sohnyi.liangcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.litepal.tablemanager.Connector;

import ui.LoginActivity;
import ui.SecCabLogin;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private SharedPreferences pref;

    private Button mAppLockBut;
    private Button mSecCabBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteDatabase database = Connector.getDatabase();

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        mAppLockBut = (Button) findViewById(R.id.app_lock);
        mSecCabBut = (Button) findViewById(R.id.security_cabinet);


        mAppLockBut.setOnClickListener(this);
        mSecCabBut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.app_lock :
                intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                Log.d(TAG , "Start AppLockActivity");
                break;
            case R.id.security_cabinet:
                boolean is_first_open = pref.getBoolean("secCab_isFirstOpen", true);
                Log.d(TAG, "onClick: is_first_open" + is_first_open);
                if (is_first_open) {
                    intent = new Intent(MainActivity.this, CreateSecCabActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, SecCabLogin.class);
                }
                startActivity(intent);
                break;
        }
    }
}
