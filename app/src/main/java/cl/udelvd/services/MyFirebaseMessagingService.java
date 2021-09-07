package cl.udelvd.services;

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

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;
import java.util.Random;

import cl.udelvd.R;
import cl.udelvd.utils.Utils;
import cl.udelvd.views.activities.LoginActivity;
import cl.udelvd.views.activities.ResearcherListActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createChannel(Context context) {

        //Defining channel notification attributes
        String name = context.getString(R.string.CANAL_NOMBRE);
        String description = context.getString(R.string.CANAL_DESCRIPCION);
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel notificationChannel = new NotificationChannel("1", name, importance);
        notificationChannel.setDescription(description);
        notificationChannel.setImportance(importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(R.color.colorPrimary);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

        if (notificationManager != null) {

            notificationManager.createNotificationChannel(notificationChannel);

            Log.d(context.getString(R.string.TAG_FIREBASE_CHANNEL), context.getString(R.string.FIREBASE_CHANNEL_CREATED));
            FirebaseCrashlytics.getInstance().log(context.getString(R.string.TAG_FIREBASE_CHANNEL) + context.getString(R.string.FIREBASE_CHANNEL_CREATED));

        } else {
            Log.d(context.getString(R.string.TAG_FIREBASE_CHANNEL), context.getString(R.string.FIREBASE_CHANNEL_NOT_CREATED));
            FirebaseCrashlytics.getInstance().log(context.getString(R.string.TAG_FIREBASE_CHANNEL) + context.getString(R.string.FIREBASE_CHANNEL_NOT_CREATED));
        }
    }

    public static void suscriptionTheme(final Context context) {

        //Subscribte to notifications
        FirebaseMessaging.getInstance().subscribeToTopic(context.getString(R.string.TEMA_NOTIFICACION_REGISTRO))
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Log.d(context.getString(R.string.TAG_FIREBASE_SUSCRIPCION), context.getString(R.string.SUSCRIPCION_OK));
                        FirebaseCrashlytics.getInstance().log(context.getString(R.string.TAG_FIREBASE_SUSCRIPCION) + context.getString(R.string.SUSCRIPCION_OK));
                    }
                });
    }

    public static void deleteSuscriptionTheme(final Context context) {

        FirebaseMessaging.getInstance().unsubscribeFromTopic(context.getString(R.string.TEMA_NOTIFICACION_REGISTRO))
                .addOnCompleteListener(task -> {
                    //LOG ZONE
                    Log.d(context.getString(R.string.TAG_FIREBASE_SUSCRIPCION), context.getString(R.string.SUSCRIPCION_ELIMINADA));
                    FirebaseCrashlytics.getInstance().log(context.getString(R.string.TAG_FIREBASE_SUSCRIPCION) + context.getString(R.string.SUSCRIPCION_ELIMINADA));
                })
                .addOnFailureListener(e -> {

                    Log.d(context.getString(R.string.TAG_FIREBASE_SUSCRIPCION), context.getString(R.string.SUSCRIPCION_ERRONEA));
                    FirebaseCrashlytics.getInstance().log(context.getString(R.string.TAG_FIREBASE_SUSCRIPCION) + context.getString(R.string.SUSCRIPCION_ERRONEA));
                });
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(getString(R.string.FIREBASE_MESSAGE), "From: " + remoteMessage.getFrom());
        FirebaseCrashlytics.getInstance().log(getString(R.string.FIREBASE_MESSAGE) + "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload. (Notification from API SERVER)
        if (remoteMessage.getData().size() > 0) {

            Log.d(getString(R.string.FIREBASE_MESSAGE_DATA), "Message data payload: " + remoteMessage.getData());

            //Obtener datos y mostrar notificacion
            showNotificationResearchers(remoteMessage);

        }

        //Notification comming from FCM
        if (remoteMessage.getNotification() != null) {

            Log.d(getString(R.string.FIREBASE_MESSAGE_NOTIFICATION), "Message notification: " + remoteMessage.getNotification().getTitle() + " - " + remoteMessage.getNotification().getBody());

            showNotificationGeneric(remoteMessage);
        }
    }

    private void showNotificationGeneric(RemoteMessage remoteMessage) {

        String title = Objects.requireNonNull(remoteMessage.getNotification()).getTitle();
        String description = remoteMessage.getNotification().getBody();

        if (title != null && description != null) {

            //Maneja la notificacion cuando esta en foreground
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "1")
                    .setContentTitle(title)
                    .setContentText(description)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                    .setSmallIcon(R.drawable.ic_logo_notification_1200)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            int notificationId = new Random().nextInt(60000);

            //Notificar a sistema
            notificationManager.notify(notificationId, mBuilder.build());
        }
    }

    /**
     * Function that process notification coming from FCM
     *
     * @param remoteMessage fcm msg
     */
    private void showNotificationResearchers(RemoteMessage remoteMessage) {

        Map<String, String> mParams = remoteMessage.getData();

        String title, description;

        if (Utils.getLanguage(getApplicationContext()).equals(getString(R.string.ESPANOL))) {

            title = mParams.get(getString(R.string.NOTIFICACION_TITULO_ES));
            description = mParams.get(getString(R.string.NOTIFICACION_DESCRIPCION_ES));

        } else {
            title = mParams.get(getString(R.string.NOTIFICACION_TITULO_EN));
            description = mParams.get(getString(R.string.NOTIFICACION_DESCRIPCION_EN));
        }

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);

        boolean expired = Utils.jwtStatus(getApplicationContext(), sharedPreferences);
        Intent intent;

        if (expired) {

            intent = new Intent(this, LoginActivity.class);
            intent.putExtra(getString(R.string.NOTIFICACION_INTENT_ACTIVADO), true);

        } else {
            intent = new Intent(this, ResearcherListActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent mPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //Maneja la notificacion cuando esta en foreground
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "1")
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(R.drawable.ic_logo_notification_1200)
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
        FirebaseCrashlytics.getInstance().setUserId(s);
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
