package com.sohnyi.liangcare.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by sohnyi on 2017/4/26.
 */

public class Drawable2Bitmap {
    public static Bitmap bitmap2Drawable(Drawable drawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        return  bitmap;
    }
}
