package com.sohnyi.liangcare;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
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

    private boolean lockAppList;
    private boolean lockCab;

    private SharedPreferences pref;
    private CardView mAppLockCard;
    private CardView mSecCabCard;
    private Toolbar mToolbar;

    private PermissionsChecker mChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        lockAppList = true;
        lockCab = true;


        mAppLockCard = (CardView) findViewById(R.id.app_lock_cardView);
        mSecCabCard = (CardView) findViewById(R.id.sec_cab_cardView);
        mChecker = new PermissionsChecker(this);
        mAppLockCard.setOnClickListener(this);
        mSecCabCard.setOnClickListener(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
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
                if (lockAppList) {
                    /*应用退出后首次打开转入登录界面*/
                    intent = LoginActivity.newIntent(MainActivity.this,
                            getPackageName());
                } else {
                    /*应用未退出直接转入应用锁界面*/
                    intent = new Intent(MainActivity.this,
                            AppLockActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.sec_cab_cardView:
                boolean is_first_open = pref
                        .getBoolean("secCab_isFirstOpen", true);
                com.sohnyi.liangcare.utils.LogUtil.d(TAG,
                        "onClick: is_first_open" + is_first_open);
                /*判断是否首次打开*/
                if (is_first_open) {
                    /*转入创建文件保密柜的界面*/
                    intent = new Intent(MainActivity.this,
                            CreateSecCabActivity.class);
                } else if (lockCab){
                    /*转入文件保密柜登录界面*/
                    intent = new Intent(MainActivity.this,
                            SecCabLogin.class);
                } else {
                    /*应用未退出直接转入文件加密柜界面*/
                    intent = new Intent(MainActivity.this,
                            SecCabActivity.class);
                }
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
