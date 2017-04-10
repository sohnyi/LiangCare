package com.sohnyi.liangcare;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by sohnyi on 2017/4/9.
 */

public class CreateSecCabActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "CreateSecCabActivity";

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
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.storage_location:
                Boolean isSDPresent = android.os.Environment.getExternalStorageState()
                        .equals(android.os.Environment.MEDIA_MOUNTED);
                Log.d(TAG, "onClick: isSDPresent:" + isSDPresent);
                if (isSDPresent) {
                    mDialog_selectLoca = createSelectDialog(R.array.location_set_items);
                } else {
                    mDialog_selectLoca = createSelectDialog(R.array.internal_storage);
                }
                mDialog_selectLoca.show();
                break;
            case R.id.done_action:
                Intent intent = new Intent(CreateSecCabActivity.this, SecCabActivity.class);
                startActivity(intent);
                /*String pass_set = mEditText_StorSetPass.getText().toString().trim();
                if (pass_set.length() >= 4) {
                    String pass_con = mEditText_StorConPass.getText().toString().trim();
                    if (pass_con.equals(pass_set)){
                        ShowToast.showToast(getApplicationContext(), R.string.pass_set_suss);
                        Intent intent = new Intent(CreateSecCabActivity.this, SecCabActivity.class);
                        startActivity(intent);
                        this.finish();
                    } else {
                        ShowToast.showToast(getApplicationContext(), R.string.pass_no_match);
                    }
                } else {
                    ShowToast.showToast(getApplicationContext(), R.string.min_length_is_4);
                }*/
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
