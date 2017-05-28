package com.sohnyi.liangcare.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sohnyi.liangcare.AppLockActivity;
import com.sohnyi.liangcare.R;
import com.sohnyi.liangcare.utils.LogUtil;
import com.sohnyi.liangcare.utils.Encoder;
import com.sohnyi.liangcare.utils.ToastUtil;

/**
 * Created by sohnyi on 2017/3/11.
 */

public class LoginActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    public static final String EXTRA_PACKAGE_NAME
            = "com.sohnyi.liangcare.top_packageName";

    private boolean confirm = false;
    private String init_pass = null;

    private SharedPreferences pref;
    private PackageManager mManager;
    private String getExtraPackageName;
    private Drawable icon;
    private String selfPackageName;

    private ImageView mAppLockIcon;
    private TextView mPassInputShow;
    private ImageButton mImageButton;
    private TextView mInputTip;
    private Button mBut1;
    private Button mBut2;
    private Button mBut3;
    private Button mBut4;
    private Button mBut5;
    private Button mBut6;
    private Button mBut7;
    private Button mBut8;
    private Button mBut9;
    private Button mBut0;
    private Button mButOk;

    public static Intent newIntent(Context packageContext,
                                   String packageName) {
        Intent intent = new Intent(packageContext,
                LoginActivity.class);
        intent.putExtra(EXTRA_PACKAGE_NAME, packageName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getExtraPackageName = getIntent().getStringExtra(EXTRA_PACKAGE_NAME);
        mManager = getPackageManager();
        if (!TextUtils.isEmpty(getExtraPackageName)) {
            try {
                icon = mManager.getApplicationIcon(getExtraPackageName);
            } catch (PackageManager.NameNotFoundException e) {
                icon = getDrawable(R.mipmap.ic_launcher);
                LogUtil.e(TAG, e.getMessage());
            }
        }

        selfPackageName = getPackageName();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean is_first_open = pref.getBoolean("appLock_isFirstOpen",
                true);

        initView();
        if (is_first_open) {
            mInputTip.setText(R.string.set_password);
        } else {
            mInputTip.setText(R.string.enter_pass);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*数字键盘按钮*/
            case R.id.but0 :
                mPassInputShow.append("0");
                break;
            case R.id.but1 :
                mPassInputShow.append("1");
                break;
            case R.id.but2 :
                mPassInputShow.append("2");
                break;
            case R.id.but3 :
                mPassInputShow.append("3");
                break;
            case R.id.but4 :
                mPassInputShow.append("4");
                break;
            case R.id.but5 :
                mPassInputShow.append("5");
                break;
            case R.id.but6 :
                mPassInputShow.append("6");
                break;
            case R.id.but7 :
                mPassInputShow.append("7");
                break;
            case R.id.but8 :
                mPassInputShow.append("8");
                break;
            case R.id.but9 :
                mPassInputShow.append("9");
                break;
            case R.id.but_ok :
                String password = pref.getString("appLock_password", "");
                /*设置密码*/
                if (password.equals("")) {
                    Log.d(TAG, "onClick: confirm: " + confirm);
                    if (!confirm) {
                         init_pass = mPassInputShow.getText().toString().trim();
                        if (init_pass.length() >= 4) {
                            confirm = !confirm;
                            mInputTip.setText(R.string.confirm_password);
                        } else {
                            ToastUtil.showToast(getApplicationContext(),
                                    getString(R.string.min_length_is_4));
                        }
                    } else {
                       String conf_pass = mPassInputShow.getText().toString().trim();
                        Log.d(TAG, "onClick: init_pass: " + init_pass);
                        Log.d(TAG, "onClick: conf_pass: " + conf_pass);
                        if (conf_pass.equals(init_pass)) {
                            try {
                                Log.d(TAG, "onClick: try to get md5");
                                password = Encoder.md5Encode(conf_pass);
                                Log.d(TAG, "onClick: password: " + password);
                                setPassword(password);
                                Intent intent = new Intent(LoginActivity.this, AppLockActivity.class);
                                startActivity(intent);
                                this.finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "onClick: " + e);
                            }
                        } else {
                            confirm = !confirm;
                            init_pass = null;
                            ToastUtil.showToast(this, getString(R.string.pass_no_match));
                            mInputTip.setText(getText(R.string.set_password));
                        }
                    }

                } else {
                    String in_pass = mPassInputShow.getText().toString().trim();
                    try {
                        in_pass = Encoder.md5Encode(in_pass);
                        if (in_pass.equals(password)) {
                            if (getExtraPackageName.equals(getPackageName())) {
                                LogUtil.d(TAG, "getPackageName=" + getPackageName());
                                Intent intent = new Intent(LoginActivity.this, AppLockActivity.class);
                                startActivity(intent);
                            }
                            this.finish();
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.wrong_pass);
                        }
                        LogUtil.d(TAG, "password: " + password);
                        LogUtil.d(TAG, "enter: " + in_pass);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                mPassInputShow.setText(null);
                break;
            case R.id.imageButton:
                if (mPassInputShow.getText().length() > 0) {
                    mPassInputShow.setText(mPassInputShow.
                            getText().subSequence(0, mPassInputShow.getText().length() - 1));
                }
                break;
            default:
                break;

        }
    }

    public void initView() {
        mInputTip = (TextView) findViewById(R.id.pass_input_tip);
        mAppLockIcon = (ImageView) findViewById(R.id.app_lock_icon);
        mPassInputShow = (TextView) findViewById(R.id.pass_input_show);
        mImageButton = (ImageButton) findViewById(R.id.imageButton);
        mBut0 = (Button) findViewById(R.id.but0);
        mBut1 = (Button) findViewById(R.id.but1);
        mBut2 = (Button) findViewById(R.id.but2);
        mBut3 = (Button) findViewById(R.id.but3);
        mBut4 = (Button) findViewById(R.id.but4);
        mBut5 = (Button) findViewById(R.id.but5);
        mBut6 = (Button) findViewById(R.id.but6);
        mBut7 = (Button) findViewById(R.id.but7);
        mBut8 = (Button) findViewById(R.id.but8);
        mBut9 = (Button) findViewById(R.id.but9);
        mButOk = (Button) findViewById(R.id.but_ok);
        mAppLockIcon.setImageDrawable(icon);
        mImageButton.setOnClickListener(this);
        mBut0.setOnClickListener(this);
        mBut1.setOnClickListener(this);
        mBut2.setOnClickListener(this);
        mBut3.setOnClickListener(this);
        mBut4.setOnClickListener(this);
        mBut5.setOnClickListener(this);
        mBut6.setOnClickListener(this);
        mBut7.setOnClickListener(this);
        mBut8.setOnClickListener(this);
        mBut9.setOnClickListener(this);
        mButOk.setOnClickListener(this);
    }

    public void setPassword(String pass) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("appLock_isFirstOpen", false)
                .putString("appLock_password", pass)
                .apply();
        ToastUtil.showToast(this, getString(R.string.pass_set_suss));
        Log.d(TAG, "setPassword: set is first open value");
    }

    /*重写返回键事件监听*/
    @Override
    public void onBackPressed() {
        /*本应用内实现原有返回功能*/
        if (getExtraPackageName.equals(selfPackageName)) {
            super.onBackPressed();
        } else {
            /*返回键实现Home键功能*/
            Intent intent = new Intent(Intent.ACTION_MAIN)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            finish();
        }
    }
}
