package com.sohnyi.liangcare.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.sohnyi.liangcare.R;

/**
 * Created by sohnyi on 2017/5/2.
 */

public class UsageAccessDialogFragment extends DialogFragment {
    private static final int MY_DIALOG_THEME = R.style.myAlertDialog;


    public static UsageAccessDialogFragment newInstance() {
         return new UsageAccessDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), MY_DIALOG_THEME);

        builder.setTitle(getText(R.string.permission_request))
                .setMessage(getText(R.string.fun_need_app_usage))
                .setCancelable(false);
        builder.setPositiveButton(getText(R.string.settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppsUsage.startActionUsageAccessSettings(getActivity());
            }
        });
        builder.setNegativeButton(R.string.cancel, null);

         return builder.create();
    }

}
