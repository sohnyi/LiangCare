package com.sohnyi.liangcare.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sohnyi.liangcare.R;
import com.sohnyi.liangcare.SecCabActivity;
import com.sohnyi.liangcare.utils.FindPassActivity;

import java.security.MessageDigest;

/**
 * Created by sohnyi on 2017/4/8.
 */

public class SecCabLogin extends AppCompatActivity implements View.OnClickListener{

    private SharedPreferences pref;

    private EditText mEditText;
    private TextView mTextView;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seccab_login);

        intiView();
        pref = PreferenceManager.getDefaultSharedPreferences(this);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                                        |WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.forget_password:
                intent = new Intent(getApplicationContext(), FindPassActivity.class);
                startActivity(intent);
                break;
            case R.id.done:
                String password = mEditText.getText().toString().trim();
                try {
                    MessageDigest digest = MessageDigest.getInstance("MD5");
                    digest.reset();
                    digest.update(password.getBytes());
                    byte[] bytes = digest.digest();
                    StringBuffer hexString = new StringBuffer();
                    for (byte b : bytes) {
                        hexString.append(Integer.toHexString(0xFF & b));
                    }
                    password = hexString.toString();
                    String pass_saved = pref.getString("secCab_password", "");
                    if (password.equals(pass_saved)) {
                        intent = new Intent(SecCabLogin.this, SecCabActivity.class);
                        startActivity(intent);
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    public void intiView() {
        mEditText = (EditText) findViewById(R.id.secCab_passInput);
        mTextView = (TextView) findViewById(R.id.forget_password);
        mButton = (Button) findViewById(R.id.done);

        mTextView.setOnClickListener(this);
        mButton.setOnClickListener(this);

    }
}
