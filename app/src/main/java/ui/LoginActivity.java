package ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sohnyi.liangcare.AppLockActivity;
import com.sohnyi.liangcare.R;

import java.security.MessageDigest;

import utils.ShowToast;

/**
 * Created by sohnyi on 2017/3/11.
 */

public class LoginActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private boolean confirm = false;
    private String init_pass = null;

    private SharedPreferences pref;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean is_first_open = pref.getBoolean("isFirstOpen", true);
        Log.d(TAG, "onCreate: is first open:" + is_first_open);

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
                String password = pref.getString("password", "");
                if (password.equals("")) {
                    Log.d(TAG, "onClick: confirm :" + confirm);
                    if (!confirm) {
                         init_pass = mPassInputShow.getText().toString().trim();
                        if (init_pass.length() >= 4) {
                            confirm = !confirm;
                            mInputTip.setText(R.string.confirm_password);
                        } else {
                            ShowToast.showToast(getApplicationContext(),
                                    getString(R.string.min_length_is_4));
                        }
                    } else {
                       String conf_pass = mPassInputShow.getText().toString().trim();
                        Log.d(TAG, "onClick: init_pass is " + init_pass);
                        Log.d(TAG, "onClick: conf_pass is " + conf_pass);
                        if (conf_pass.equals(init_pass)) {
                            try {
                                Log.d(TAG, "onClick: try to get md5");
                                MessageDigest digest = MessageDigest.getInstance("MD5");
                                digest.reset();
                                digest.update(conf_pass.getBytes());
                                byte[] bytes = digest.digest();
                                StringBuffer hexString = new StringBuffer();
                                for (byte b : bytes) {
                                    hexString.append(Integer.toHexString(0xFF & b));
                                }
                                password = hexString.toString();
                                Log.d(TAG, "onClick: password :" + password);
                                setPassword(password);
                                Intent intent = new Intent(LoginActivity.this, AppLockActivity.class);
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "onClick: " + e);
                            }
                        }
                    }

                } else {
                    String in_pass = mPassInputShow.getText().toString().trim();
                    try {
                        MessageDigest digest = MessageDigest.getInstance("MD5");
                        digest.reset();
                        digest.update(in_pass.getBytes());
                        byte[] bytes = digest.digest();
                        StringBuffer hexString = new StringBuffer();
                        for (byte b : bytes) {
                            hexString.append(Integer.toHexString(0xFF & b));
                        }
                        in_pass = hexString.toString();
                        if (in_pass.equals(password)) {
                            Intent intent = new Intent(LoginActivity.this, AppLockActivity.class);
                            startActivity(intent);
                        } else {
                            ShowToast.showToast(getApplicationContext(), R.string.wrong_pass);
                        }
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
        editor.putBoolean("isFirstOpen", false)
                .putString("password", pass)
                .apply();
        Log.d(TAG, "setPassword: set is first open value");
    }
}
