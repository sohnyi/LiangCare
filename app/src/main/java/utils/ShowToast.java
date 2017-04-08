package utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Ziyi on 2016/7/28.
 */
public class ShowToast {
    private static Toast mToast;

    public static void showToast(Context context, String s) {
        if (mToast == null) {
            mToast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(s);
        }

        mToast.show();
    }

    public static void showToast(Context context, int id) {
        if (mToast == null) {
            mToast = Toast.makeText(context, id, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(id);
        }

        mToast.show();
    }
}
