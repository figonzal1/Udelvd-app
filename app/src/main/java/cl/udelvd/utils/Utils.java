package cl.udelvd.utils;

import android.text.TextUtils;
import android.util.Log;

public class Utils {

    /**
     * Funcion que verifica validez de Email
     *
     * @param target Email objetivo
     * @return True|False según sea el caso
     */
    public static boolean isValidEmail(CharSequence target) {
        boolean result =
                !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        Log.d("EMAIL_VALIDO", String.valueOf(result));
        return result;
    }
}
