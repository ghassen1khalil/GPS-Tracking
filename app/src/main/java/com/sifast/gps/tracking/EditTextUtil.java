package com.sifast.gps.tracking;

import android.widget.EditText;

/**
 * Created by ghassen.ati on 25/03/2016.
 */
public final class EditTextUtil {

    public static boolean isNull(EditText editText) {
        return editText.getText().toString().matches("");
    }
}
