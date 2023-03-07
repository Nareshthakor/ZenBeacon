package com.qsmp.Cardviewpager_extra;

import android.content.Context;
import android.util.TypedValue;

public class UiUtils {
    public static int getThemePrimaryColor(final Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, value, true);
        return value.data;
    }
}