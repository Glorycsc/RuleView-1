package com.zkk.activity;

import android.content.Context;
import android.util.TypedValue;

/**
 * Create by glorizz on 2019/2/20
 * Describe:
 */
public class DisplayUtil {

    public static float dp2px(float dpValue, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpValue, context.getResources().getDisplayMetrics());
    }

    public static float sp2px(float spValue, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spValue, context.getResources().getDisplayMetrics());
    }

}
