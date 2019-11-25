package cl.udelvd.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

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


    public static boolean isJWTExpired(Context context) {

        //Obtener token shared pref
        SharedPreferences sharedPreferences = context.getSharedPreferences("udelvd",
                Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("TOKEN_LOGIN", "");
        int id_investigador = sharedPreferences.getInt("id_investigador", 0);

        //Si no es vacio
        if (!json.isEmpty()) {

            Log.d("JWT_STATUS", "NO VACIO");

            JWT jwt = new JWT(json);

            String issuer = jwt.getIssuer();
            String audience = Objects.requireNonNull(jwt.getAudience()).get(0);
            String jti = jwt.getId();
            Claim claim = jwt.getClaim("uid");

            assert jti != null;
            assert issuer != null;
            Log.d("JWT_ISSUER", issuer);
            Log.d("JWT_AUDIENCe", audience);
            Log.d("JWT_JTI", jti);
            Log.d("JWT_CLAIM_UID", Objects.requireNonNull(claim.asString()));

            if (jti.equals("4f1g23a12aa") && audience.equals("android") &&
                    issuer.equals("http://udelvd.cl") && Objects.equals(claim.asInt(),
                    id_investigador)) {

                Date date = jwt.getExpiresAt();

                SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.US);
                assert date != null;
                String date_s = format.format(date);

                Log.d("JWT_DATE_EXP", date_s);

                boolean expired = jwt.isExpired(10);
                Log.d("JWT_STATUS", "EXPIRED ->" + expired);
                return expired;

            } else {
                Log.d("JWT_STATUS", "DATOS NO CUADRAN");
                return true;
            }

        } else {
            Log.d("JWT_STATUS", "VACIO");
            return true;
        }
    }
}
