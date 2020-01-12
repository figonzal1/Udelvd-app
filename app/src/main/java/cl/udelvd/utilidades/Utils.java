package cl.udelvd.utilidades;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.vistas.activities.LoginActivity;

public class Utils {

    /**
     * Funcion que verifica validez de Email
     *
     * @param target Email objetivo
     * @return True|False según sea el caso
     */
    public static boolean isInValidEmail(CharSequence target) {
        boolean result =
                !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        Log.d("EMAIL_INVALIDO", String.valueOf(!result));
        return !result;
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

    /**
     * Funcion para calcular la edad
     *
     * @param fecha_nac Fecha nacimiento de la persona
     * @return Edad
     */
    public static int calculateYearsOld(Date fecha_nac) {

        Date now = new Date();

        long diff = now.getTime() - fecha_nac.getTime();

        return (int) (diff / (24 * 60 * 60 * 1000)) / 365;
    }


    /**
     * Funcion encargada de hacer la configuracion de titulo, color e icono de toolbar
     *
     * @param activity                Actividad en uso
     * @param context                 Contexto de la actividad
     * @param id_custom_drawable_home Id del icono de home up
     * @param titulo                  Titulo del toolbar
     */
    public static void configurarToolbar(AppCompatActivity activity, Context context, int id_custom_drawable_home, String titulo) {

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(context.getResources().getColor(R.color.colorOnPrimary));
        activity.setSupportActionBar(toolbar);

        //Setear toolbar
        ActionBar actionBar = activity.getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (id_custom_drawable_home != 0) {
            actionBar.setHomeAsUpIndicator(id_custom_drawable_home);
        }
        actionBar.setTitle(titulo);
    }

    /**
     * Funcion que convierte una fecha date en un string
     *
     * @param context Contexto utilizado para el uso de strings
     * @param dFecha  Fecha que será convertida
     * @return String de la fecha
     */
    public static String dateToString(Context context, Date dFecha) {
        SimpleDateFormat mFormat =
                new SimpleDateFormat(context.getString(R.string.FORMATO_FECHA), Locale.US);

        return mFormat.format(dFecha);
    }


    /**
     * Funcion encargada de transformar un String a un Date
     *
     * @param sFecha Fecha en string que será convertida en date
     * @return dFecha Fecha en Date entregada por le funcion
     */
    public static Date stringToDate(Context context, String sFecha) {

        SimpleDateFormat mFormat =
                new SimpleDateFormat(context.getString(R.string.FORMATO_FECHA),
                        Locale.US);
        Date mDFecha = null;

        try {
            mDFecha = mFormat.parse(sFecha);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return mDFecha;
    }
}
