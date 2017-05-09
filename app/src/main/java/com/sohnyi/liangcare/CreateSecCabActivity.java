package com.sohnyi.liangcare;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ArrayRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sohnyi.liangcare.utils.LogUtil;
import com.sohnyi.liangcare.utils.SecCabActivity;
import com.sohnyi.liangcare.utils.ToastUtil;

import java.security.MessageDigest;

/**
 * Created by sohnyi on 2017/4/9.
 */

public class CreateSecCabActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "CreateSecCabActivity";

    private SharedPreferences pref;

    private TextView mTextView_StorLoca;
    private EditText mEditText_StorSetPass;
    private EditText mEditText_StorConPass;
    private Button mButton_done;
    private Button mButton_cancel;
    private AlertDialog mDialog_selectLoca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_seccab);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initViews();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.storage_location:
                Boolean isSDPresent = android.os.Environment.getExternalStorageState()
                        .equals(android.os.Environment.MEDIA_MOUNTED);
                LogUtil.d(TAG, "onClick: isSDPresent:" + isSDPresent);
                if (isSDPresent) {
                    mDialog_selectLoca = createSelectDialog(R.array.location_set_items);
                } else {
                    mDialog_selectLoca = createSelectDialog(R.array.internal_storage);
                }
                mDialog_selectLoca.show();
                break;
            case R.id.done_action:
                String pass_set = mEditText_StorSetPass.getText().toString().trim();
                if (pass_set.length() >= 4) {
                    String pass_con = mEditText_StorConPass.getText().toString().trim();
                    if (pass_con.equals(pass_set)){
                        try {
                            MessageDigest digest = MessageDigest.getInstance("MD5");
                            digest.reset();
                            digest.update(pass_con.getBytes());
                            byte[] bytes = digest.digest();
                            StringBuffer hexString = new StringBuffer();
                            for (byte b : bytes) {
                                hexString.append(Integer.toHexString(0xFF & b));
                            }
                            String password = hexString.toString();
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putBoolean("secCab_isFirstOpen", false)
                                    .putString("secCab_password", password)
                                    .apply();
                            ToastUtil.showToast(getApplicationContext(), R.string.pass_set_suss);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        Intent intent = new Intent(CreateSecCabActivity.this, SecCabActivity.class);
                        startActivity(intent);
                        this.finish();
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.pass_no_match);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.min_length_is_4);
                }
                break;
            case R.id.cancel_action:
                this.finish();
                break;
        }
    }

    public void initViews(){
        mTextView_StorLoca = (TextView) findViewById(R.id.storage_location);
        mTextView_StorLoca.setOnClickListener(this);
        mEditText_StorSetPass = (EditText) findViewById(R.id.secCab_passSet);
        mEditText_StorConPass = (EditText) findViewById(R.id.secCab_passConf);
        mButton_done = (Button) findViewById(R.id.done_action);
        mButton_done.setOnClickListener(this);
        mButton_cancel = (Button) findViewById(R.id.cancel_action);
        mButton_cancel.setOnClickListener(this);
    }


    public AlertDialog createSelectDialog(@ArrayRes int itemsId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateSecCabActivity.this);
        builder.setTitle(R.string.storage_location_select)
                .setItems(itemsId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }

}