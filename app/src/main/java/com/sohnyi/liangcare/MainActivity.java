package com.sohnyi.liangcare;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;

import com.sohnyi.liangcare.ui.LoginActivity;
import com.sohnyi.liangcare.ui.SecCabLogin;
import com.sohnyi.liangcare.utils.PermissionsActivity;
import com.sohnyi.liangcare.utils.PermissionsChecker;

import org.litepal.tablemanager.Connector;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    /*权限请求码*/
    private static final int REQUEST_CODE = 0;
    private final String[] PERMISSIONS = new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private SharedPreferences pref;

    private CardView mAppLockCad;
    private CardView mSecCabCad;
    private CardView mVirusCad;

    private PermissionsChecker mChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        mAppLockCad = (CardView) findViewById(R.id.app_lock_cardView);
        mSecCabCad = (CardView) findViewById(R.id.sec_cab_cardView);
        mVirusCad = (CardView) findViewById(R.id.virus_scan_cardView);
        mChecker = new PermissionsChecker(this);


        mAppLockCad.setOnClickListener(this);
        mSecCabCad.setOnClickListener(this);
        mVirusCad.setOnClickListener(this);
        Connector.getDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.app_lock_cardView :
                intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                Log.d(TAG , "Start AppLockActivity");
                break;
            case R.id.sec_cab_cardView:
                boolean is_first_open = pref.getBoolean("secCab_isFirstOpen", true);
                Log.d(TAG, "onClick: is_first_open" + is_first_open);
                if (is_first_open) {
                    intent = new Intent(MainActivity.this, CreateSecCabActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, SecCabLogin.class);
                }
                startActivity(intent);
                break;
            case R.id.virus_scan_cardView:
                intent = new Intent(MainActivity.this, VirusScanActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }

}
