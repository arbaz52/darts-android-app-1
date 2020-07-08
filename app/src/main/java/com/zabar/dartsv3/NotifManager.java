package com.zabar.dartsv3;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotifManager {
    public static int totalNotifications = 0;
    public static final String CHANNEL_ID = "1";


    public static int createAlertNotification(Context context, String alertId, String suspectName, Location qrUnitLocation, Location alertLocation, String time){

        totalNotifications += 1;

        NotificationCompat.Builder mBuilder;
        Intent alertIntent=new Intent(context, AlertInfoActivity.class);
        alertIntent.putExtra("_id", alertId);
        alertIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, totalNotifications, alertIntent, 0);

        int distance = -1;
        if (qrUnitLocation != null &&
                alertLocation != null)
            distance = (int) qrUnitLocation.distanceTo(alertLocation);

        Date datetime = new Date(time);
        String diff = TimeManager.difference(new Timestamp(Calendar.getInstance().getTimeInMillis()), new Timestamp(datetime.getTime()));
        //add time too
        String d = suspectName + " is detected " + distance + "m away " + diff;

        mBuilder= new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentTitle("Alert! Suspect detected")
                .setContentText(d)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(d))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(totalNotifications, mBuilder.build());
        return totalNotifications;
    }

    public static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alerts";
            String description = "Shows alerts";
            int importance = android.app.NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
