package edu.neu.madcourse.skilift;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessageReceiver extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            String icon = remoteMessage.getData().get("icon");
            assert icon != null;
            showNotification(title, body, icon);
        }
    }

    public RemoteViews getCustomDesign(String title, String message, int iconResourceID) {
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.notification_title, title);
        remoteViews.setTextViewText(R.id.notification_message, message);
        remoteViews.setImageViewResource(R.id.notification_icon, iconResourceID);
        return remoteViews;
    }

    public void showNotification(String title, String message, String sticker) {
        Intent intent = new Intent(this, MainActivity.class);
        String channel_id = "notification_channel";
        String fileName = sticker.toLowerCase();
        int resourceID = getResources().getIdentifier(fileName, "drawable", getPackageName());
        if (resourceID == 0) resourceID = R.drawable.snowy_mountains;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id).setSmallIcon(resourceID).setAutoCancel(true).setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}).setOnlyAlertOnce(true).setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder = builder.setContent(getCustomDesign(title, message, resourceID));
        }
        else {
            builder = builder.setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.snowy_mountains);
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "web_app", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(0, builder.build());
    }
}