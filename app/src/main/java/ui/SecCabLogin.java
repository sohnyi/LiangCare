package ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sohnyi.liangcare.FindPassActivity;
import com.sohnyi.liangcare.R;

/**
 * Created by sohnyi on 2017/4/8.
 */

public class SecCabLogin extends AppCompatActivity implements View.OnClickListener{
    private EditText mEditText;
    private TextView mTextView;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seccab_login);

        intiView();

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
