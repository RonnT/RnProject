package ru.pichesky.rosneft.gcm;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import ru.pichesky.rosneft.R;
import ru.pichesky.rosneft.activity.EventItemActivity;
import ru.pichesky.rosneft.activity.MainActivity;
import ru.pichesky.rosneft.activity.ProductItemActivity;
import ru.pichesky.rosneft.activity.SplashActivity;
import ru.pichesky.rosneft.utils.CONST;

/**
 * Created by Andrew Vasilev on 22.06.2015.
 */
public class GcmIntentService extends IntentService {

    private static final String
            PUSH_TYPE = "type",
            PUSH_ID = "id",
            BODY = "body";

    public GcmIntentService() {
        super("GcmMessageHandler");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty())
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
                runPushAction(extras);
        GcmReceiver.completeWakefulIntent(intent);
    }

    private void runPushAction(Bundle pBundle) {
        int type = Integer.parseInt(pBundle.getString(PUSH_TYPE, "0"));
        int id = Integer.parseInt(pBundle.getString(PUSH_ID, "0"));
        String message = pBundle.getString(BODY, "");

        if (isAppOnTop() && isScreenOn()) {
            showSnackbar(id, type, message);
            return;
        }
        buildStackAndShowNotification(message, type, id);
    }

    private void buildStackAndShowNotification(String message, int type, int id) {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addNextIntent(getSpashActivityIntent());
        stackBuilder.addNextIntent(getMainActivityIntent(type, message).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        if(type == CONST.PUSH_TYPE_EVENT)
            stackBuilder.addNextIntent(getEventItemActivity(id));
        if(type == CONST.PUSH_TYPE_PRODUCT)
            stackBuilder.addNextIntent(getProductItemActivity(id));

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        showNotification(getResources().getString(R.string.push_title), message, pendingIntent);
    }

    private Intent getSpashActivityIntent(){
        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra(CONST.FROM_PUSH, true);
        return intent;
    }

    private Intent getMainActivityIntent(int pType, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(CONST.SELECTED_ITEM, pType);
        intent.putExtra(CONST.PUSH_MESSAGE, message);
        return intent;
    }

    private Intent getEventItemActivity(int pId){
        Intent intent = new Intent(this, EventItemActivity.class);
        intent.putExtra(CONST.PUSH_ITEM_ID, pId);
        return intent;
    }

    private Intent getProductItemActivity(int pId){
        Intent intent = new Intent(this, ProductItemActivity.class);
        intent.putExtra(CONST.PUSH_ITEM_ID, pId);
        return intent;
    }

    private void showNotification(String pTitle, String pBigText, PendingIntent pPendingIntent) {

        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.view_custom_notification);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(getNotificationIcon())
                        .setColor(getResources().getColor(R.color.color_primary))
                        .setAutoCancel(true)
                        .setVibrate(new long[]{CONST.VIBRATE_TIME, CONST.VIBRATE_TIME,
                                CONST.VIBRATE_TIME, CONST.VIBRATE_TIME, CONST.VIBRATE_TIME})
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setLights(Color.YELLOW, CONST.FLASHING_TIME, CONST.FLASHING_TIME)
                        .setContentIntent(pPendingIntent)
                        .setContent(notificationView);

        notificationView.setImageViewResource(R.id.ic_image, R.drawable.ic_launcher);
        notificationView.setTextViewText(R.id.title, pTitle);
        notificationView.setTextViewText(R.id.message, pBigText);
        notificationView.setTextViewText(R.id.time, new SimpleDateFormat(CONST.TIME_FORMAT).format(Calendar.getInstance().getTime()));

        mNotificationManager.notify(0, mBuilder.build());
    }

    private void showSnackbar(int id, int type, String message) {
        Intent intent = new Intent(CONST.INTENT_SHOW_SNACK_NOTIF);
        intent.putExtra(CONST.PUSH_ITEM_ID, id);
        intent.putExtra(CONST.PUSH_TYPE, type);
        intent.putExtra(CONST.PUSH_MESSAGE, message);
        sendBroadcast(intent);
    }

    private int getNotificationIcon() {
        return R.mipmap.ic_launcher;
    }

    private boolean isAppOnTop() {
        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);

        return services.get(CONST.FIRST_ITEM).topActivity.getPackageName()
                .equalsIgnoreCase(getPackageName());
    }

    private boolean isScreenOn(){
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }
}
