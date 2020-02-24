package cl.udelvd.servicios;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import cl.udelvd.R;
import cl.udelvd.utilidades.Utils;
import cl.udelvd.vistas.activities.InvestigadorListActivity;
import cl.udelvd.vistas.activities.LoginActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createChannel(Context context) {

        //Definicion de atributos de canal de notificacion
        String name = context.getString(R.string.CANAL_NOMBRE);
        String description = context.getString(R.string.CANAL_DESCRIPCION);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel mNotificationChannel = new NotificationChannel("1", name, importance);
        mNotificationChannel.setDescription(description);
        mNotificationChannel.setImportance(importance);
        mNotificationChannel.enableLights(true);
        mNotificationChannel.setLightColor(R.color.colorPrimary);

        NotificationManager mNotificationManager = context.getSystemService(NotificationManager.class);

        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mNotificationChannel);
        }

        Log.d(context.getString(R.string.TAG_FIREBASE_CHANNEL), context.getString(R.string.FIREBASE_CHANNEL_CREATED));
    }

    public static void suscribirTema(final Context context) {
        FirebaseMessaging.getInstance().subscribeToTopic(context.getString(R.string.TEMA_NOTIFICACION_REGISTRO))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Log.d(context.getString(R.string.TAG_FIREBASE_SUSCRIPCION), context.getString(R.string.SUSCRIPCION_OK));

                            //CRASH ANALYTIC LOG
                                /*Crashlytics.setBool(activity.getString(R.string.FIREBASE_PREF_KEY)
                                        , true);
                                Crashlytics.log(Log.DEBUG,
                                        activity.getString(R.string.TAG_FIREBASE_SUSCRIPTION),
                                        activity.getString(R.string.TAG_FIREBASE_SUSCRIPTION_OK));*/
                        }
                    }
                });
    }

    public static void eliminarSuscripcionTema(final Context context) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(context.getString(R.string.TEMA_NOTIFICACION_REGISTRO))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //LOG ZONE
                        Log.d(context.getString(R.string.TAG_FIREBASE_SUSCRIPCION), context.getString(R.string.SUSCRIPCION_ELIMINADA));
                        /*Crashlytics.log(Log.DEBUG,
                                activity.getString(R.string.TAG_FIREBASE_SUSCRIPTION),
                                activity.getString(R.string.TAG_FIREBASE_SUSCRIPTION_DELETE));
                        Crashlytics.setBool(activity.getString(R.string.FIREBASE_PREF_KEY)
                                , false);*/
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(context.getString(R.string.TAG_FIREBASE_SUSCRIPCION), context.getString(R.string.SUSCRIPCION_ERRONEA));
                        /*Crashlytics.log(Log.DEBUG,
                                activity.getString(R.string.TAG_FIREBASE_SUSCRIPTION),
                                activity.getString(R.string.TAG_FIREBASE_SUSCRIPTION_ALREADY));*/
                    }
                });
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(getString(R.string.FIREBASE_MESSAGE), "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(getString(R.string.FIREBASE_MESSAGE_DATA), "Message data payload: " + remoteMessage.getData());

            /*Crashlytics.log(Log.DEBUG, getString(R.string.TAG_FIREBASE_MESSAGE),
                    getString(R.string.TAG_FIREBASE_MESSAGE_DATA_INCOMING));
            Crashlytics.setBool(getString(R.string.FIREBASE_MESSAGE_DATA_STATUS), true);*/

            //Obtener datos y mostrar notificacion
            showNotification(remoteMessage);

        }
    }

    /**
     * Funcion que procesa notificaciones provenientes de FCM
     *
     * @param remoteMessage mensaje fcm
     */
    private void showNotification(RemoteMessage remoteMessage) {

        Map<String, String> mParams = remoteMessage.getData();

        String titulo = mParams.get(getString(R.string.NOTIFICACION_TITULO));
        String descripcion = mParams.get(getString(R.string.NOTIFICACION_DESCRIPCION));

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);

        boolean expired = Utils.jwtStatus(getApplicationContext(), sharedPreferences);
        Intent intent;
        if (expired) {
            intent = new Intent(this, LoginActivity.class);
            intent.putExtra(getString(R.string.NOTIFICACION_INTENT_ACTIVADO), true);
        } else {
            intent = new Intent(this, InvestigadorListActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent mPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //Maneja la notificacion cuando esta en foreground
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "1")
                .setContentTitle(titulo)
                .setContentText(descripcion)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(descripcion))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_logo_1200)
                .setAutoCancel(true)
                .setContentIntent(mPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int notificationId = new Random().nextInt(60000);
        if (notificationManager != null) {
            notificationManager.notify(notificationId, mBuilder.build());
        }

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(getString(R.string.TAG_FIREBASE_TOKEN), "Refreshed Token:" + s);
        //Crashlytics.setUserIdentifier(s);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
