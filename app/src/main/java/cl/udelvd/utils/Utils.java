package cl.udelvd.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import cl.udelvd.views.activities.LoginActivity;

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


    /**
     * Funcion encargada de verificar si el JWT esta expirado
     *
     * @param sharedPreferences Usado para buscar JWT guardado
     * @return True | False segun sea el caso
     */
    private static boolean isJWTExpired(SharedPreferences sharedPreferences) {

        //Obtener token shared pref
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
            Log.d("JWT_AUDIENCE", audience);
            Log.d("JWT_JTI", jti);
            Log.d("JWT_CLAIM_UID", Objects.requireNonNull(claim.asString()));

            if (jti.equals("4f1g23a12aa") && audience.equals("android") &&
                    issuer.equals("http://udelvd.cl") && Objects.equals(claim.asInt(),
                    id_investigador)) {

                Date date = jwt.getExpiresAt();

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                assert date != null;
                String date_s = format.format(date);

                Log.d("JWT_DATE_EXP", date_s);

                boolean expired = jwt.isExpired(10);
                Log.d("JWT_STATUS", "EXPIRADO -> " + expired);
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

    /**
     * Funcion encargada de desviar la actividad actual hacia login activity
     *
     * @param sharedPreferences Necesario para buscar TOKEN en datos del celular
     * @param activity          Activity que debe ser cerrada
     */
    public static void checkJWT(SharedPreferences sharedPreferences, Activity activity) {

        boolean expired = Utils.isJWTExpired(sharedPreferences);
        if (expired) {

            Log.d("INTENT", "DESVIANDO A LOGIN ACTIVITY");
            Intent intent = new Intent(activity.getApplication(), LoginActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }
    }
}
