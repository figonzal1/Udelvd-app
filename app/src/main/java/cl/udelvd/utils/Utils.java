package cl.udelvd.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import cl.udelvd.R;
import cl.udelvd.models.Interviewee;
import cl.udelvd.views.activities.LoginActivity;

public class Utils {

    /**
     * Function in charge of initiating FIREBASE services
     */
    public static void checkFirebaseServices(final Activity activity) {

        //FIREBASE SECTION
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(activity,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String token = instanceIdResult.getToken();
                        Log.d(activity.getString(R.string.TAG_TOKEN_FIREBASE), token);

                        //CRASH ANALYTICS LOG
                        //Crashlytics.log(Log.DEBUG, getString(R.string.TAG_FIREBASE_TOKEN), token);
                        //Crashlytics.setUserIdentifier(token);
                    }
                });
    }

    /**
     * Function that checks if the device has updated GooglePlayServices
     */
    public static void checkPlayServices(Activity activity) {

        int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        GoogleApiAvailability mGoogleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = mGoogleApiAvailability.isGooglePlayServicesAvailable(activity);

        //If there is a problem with google play
        if (resultCode != ConnectionResult.SUCCESS) {

            //If the error can be solved by the user
            if (mGoogleApiAvailability.isUserResolvableError(resultCode)) {

                Dialog dialog = mGoogleApiAvailability.getErrorDialog(activity, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            } else {

                //The error cannot be solved by the user and the app closes
                Log.d(activity.getString(R.string.TAG_GOOGLE_PLAY), activity.getString(R.string.GOOGLE_PLAY_NO_SOPORTADO));
                //Crashlytics.log(Log.DEBUG, activity.getString(R.string.TAG_GOOGLE_PLAY),activity.getString(R.string.GOOGLE_PLAY_NOSOPORTADO));
                activity.finish();
            }
        }
        //The app can be used, google play is updated
        else {

            Log.d(activity.getString(R.string.TAG_GOOGLE_PLAY), activity.getString(R.string.GOOGLE_PLAY_ACTUALIZADO));
            //Crashlytics.log(Log.DEBUG, activity.getString(R.string.TAG_GOOGLE_PLAY),activity.getString(R.string.GOOGLE_PLAY_ACTUALIZADO));
        }
    }

    /**
     * Function that checks email validity
     *
     * @param target Objective email
     * @return True|False
     */
    public static boolean isInvalidEmail(CharSequence target) {
        boolean result =
                !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        Log.d("EMAIL_INVALIDO", String.valueOf(!result));
        return !result;
    }


    /**
     * Function responsible for verifying if the JWT is expired
     *
     * @param sharedPreferences Used to search for saved JWT
     * @return True | False
     */
    public static boolean jwtStatus(Context context, SharedPreferences sharedPreferences) {

        //Obtener token shared pref
        String json = sharedPreferences.getString(context.getString(R.string.SHARED_PREF_TOKEN_LOGIN), "");
        int idResearcher = sharedPreferences.getInt(context.getString(R.string.KEY_INVES_ID_LARGO), 0);

        //Si no es vacio
        if (!json.isEmpty()) {

            Log.d(context.getString(R.string.TAG_JWT_STATUS), context.getString(R.string.JWT_STATUS_NO_VACIO));

            JWT jwt = new JWT(json);

            String issuer = jwt.getIssuer();
            String audience = Objects.requireNonNull(jwt.getAudience()).get(0);
            String jti = jwt.getId();
            Claim claim = jwt.getClaim(context.getString(R.string.JWT_UID));

            assert jti != null;
            assert issuer != null;
            Log.d(context.getString(R.string.TAG_JWT_ISSUER), issuer);
            Log.d(context.getString(R.string.TAG_JWT_AUDIENCE), audience);
            Log.d(context.getString(R.string.TAG_JWT_JTI), jti);
            Log.d(context.getString(R.string.TAG_JWT_CLAIM_UID), Objects.requireNonNull(claim.asString()));

            if (jti.equals(context.getString(R.string.JWT_JTI_CODE)) && audience.equals(context.getString(R.string.JWT_AUDIENCE_ANDROID)) &&
                    issuer.equals(context.getString(R.string.JWT_ISSUER_UDELVD)) && Objects.equals(claim.asInt(),
                    idResearcher)) {

                Date date = jwt.getExpiresAt();

                SimpleDateFormat format = new SimpleDateFormat(context.getString(R.string.FORMATO_FECHA_HORA), Locale.US);
                assert date != null;
                String date_s = format.format(date);

                Log.d(context.getString(R.string.TAG_JWT_FECHA_EXP), date_s);

                boolean expired = jwt.isExpired(10);
                Log.d(context.getString(R.string.TAG_JWT_STATUS), String.format("%s %s", context.getString(R.string.JWT_STATUS_EXPIRADO), expired));
                return expired;

            } else {
                Log.d(context.getString(R.string.TAG_JWT_STATUS), context.getString(R.string.JWT_STATUS_INCOHERENTE));
                return true;
            }

        } else {
            Log.d(context.getString(R.string.TAG_JWT_STATUS), context.getString(R.string.JWT_STATUS_VACIO));
            return true;
        }
    }

    /**
     * Function responsible for diverting the current activity to login activity
     *
     * @param sharedPreferences Necessary to search TOKEN in cellphone data
     * @param context           Activity to be closed
     */
    public static void handleJWT(SharedPreferences sharedPreferences, Context context) {

        boolean expired = Utils.jwtStatus(context.getApplicationContext(), sharedPreferences);
        if (expired) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            ((Activity) context).finish();
        }
    }

    /**
     * Function to calculate age
     *
     * @param birthDate Date of birth of the person
     * @return Age
     */
    public static int calculateYearsOld(Date birthDate) {

        Date now = new Date();

        long diff = now.getTime() - birthDate.getTime();

        return (int) (diff / (24 * 60 * 60 * 1000)) / 365;
    }


    /**
     * Function responsible for configuring the title, color and icon of the toolbar
     *
     * @param activity                Activity in use
     * @param context                 Context of the activity
     * @param id_custom_drawable_home Id of home up icon
     * @param titulo                  Toolbar title
     */
    public static void configToolbar(AppCompatActivity activity, Context context, int id_custom_drawable_home, String titulo) {

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(context.getResources().getColor(R.color.colorOnPrimary, context.getTheme()));
        activity.setSupportActionBar(toolbar);

        //Setear toolbar
        ActionBar actionBar = activity.getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (id_custom_drawable_home != 0) {
            actionBar.setHomeAsUpIndicator(id_custom_drawable_home);
        }
        if (titulo != null) {
            actionBar.setTitle(titulo);
        }
    }

    /**
     * Function that converts a date date into a string
     *
     * @param context Context used for the use of strings
     * @param date    Date to be converted
     * @return Date string
     */
    public static String dateToString(Context context, boolean is_hora, Date date) {
        SimpleDateFormat mFormat;

        if (is_hora) {
            mFormat = new SimpleDateFormat(context.getString(R.string.FORMATO_HORA), Locale.US);
        } else {
            mFormat = new SimpleDateFormat(context.getString(R.string.FORMATO_FECHA), Locale.getDefault());
        }

        return mFormat.format(date);
    }


    /**
     * Function in charge of transforming a String to a Date
     *
     * @param sFecha Date in string to be converted into date
     * @return dFecha Date on Date delivered by the function
     */
    public static Date stringToDate(Context context, boolean idHour, String sFecha) {

        SimpleDateFormat mFormat;

        if (idHour) {
            mFormat = new SimpleDateFormat(context.getString(R.string.FORMATO_HORA), Locale.US);
        } else {
            mFormat = new SimpleDateFormat(context.getString(R.string.FORMATO_FECHA), Locale.getDefault());
        }
        Date mDFecha = null;

        try {
            mDFecha = mFormat.parse(sFecha);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return mDFecha;
    }

    /**
     * Function responsible for opening the DatePicker to choose date
     */
    public static void iniciarDatePicker(final EditText editText, Context context) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        if (Objects.requireNonNull(editText.getText()).length() > 0) {

            String fecha = editText.getText().toString();
            String[] fecha_split = fecha.split(context.getString(R.string.REGEX_FECHA));

            year = Integer.parseInt(fecha_split[0]);
            month = Integer.parseInt(fecha_split[1]);
            day = Integer.parseInt(fecha_split[2]);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                editText.setText(String.format(Locale.US, "%d-%d-%d", year, month + 1, dayOfMonth));
            }
        }, year, month - 1, day);

        datePickerDialog.show();
    }

    /**
     * Function in charge of configuring the picker to select Time
     */
    public static void initHourPicker(final EditText editText, Context context) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);

        if (Objects.requireNonNull(editText.getText()).length() > 0) {

            String fecha = editText.getText().toString();
            String[] fecha_split = fecha.split(context.getString(R.string.REGEX_HORA));

            hour = Integer.parseInt(fecha_split[0]);
            minute = Integer.parseInt(fecha_split[1]);
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (minute <= 9) {
                    editText.setText(String.format(Locale.US, "%d:0%d", hourOfDay, minute));
                } else {
                    editText.setText(String.format(Locale.US, "%d:%d", hourOfDay, minute));
                }
            }
        }, hour, minute, true);

        timePickerDialog.show();
    }

    /**
     * Get Device Language
     *
     * @param context Context of the application
     * @return String with the language symbol
     */
    public static String getLanguage(Context context) {

        String language = Locale.getDefault().getLanguage();
        if (language.equals(context.getString(R.string.LANGUAJE_ES)) || language.equals(context.getString(R.string.LANGUAJE_EN))) {
            return language;
        } else {
            return context.getString(R.string.LANGUAJE_EN);
        }
    }

    public static void configIconInterviewee(Interviewee interviewee, int annos, ImageView ivPerson, Context context) {
        if (interviewee.getGenre().equals(context.getString(R.string.SEXO_FEMENINO)) ||
                interviewee.getGenre().equals(context.getString(R.string.SEXO_FEMENINO_MASTER_KEY))) {

            if (annos < 18) {
                ivPerson.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_girl, context.getTheme()));
            } else if (annos < 65) {
                ivPerson.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_adult_woman, context.getTheme()));
            } else {
                ivPerson.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_grand_mother, context.getTheme()));
            }
        } else if (interviewee.getGenre().equals(context.getString(R.string.SEXO_MASCULINO)) ||
                interviewee.getGenre().equals(context.getString(R.string.SEXO_MASCULINO_MASTER_KEY))) {
            if (annos < 18) {
                ivPerson.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_boy, context.getTheme()));
            } else if (annos < 65) {
                ivPerson.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_adult_man, context.getTheme()));
            } else {
                ivPerson.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_grand_father, context.getTheme()));
            }
        }
    }
}