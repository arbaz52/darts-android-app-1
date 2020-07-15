package com.zabar.dartsv3;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotifManager {


    public static int totalNotifications = 0;
    public static int callNotificationID=65535;
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





    public static int createMessageNotification(Context context, String messageId, QRUnit sender, String message, Timestamp time){

        totalNotifications += 1;

        NotificationCompat.Builder mBuilder;
        Intent messageIntent=new Intent(context, MessageActivity.class);
        messageIntent.putExtra(MessageActivity.KEY_QRUNIT_ID, sender.ID);
        messageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, totalNotifications, messageIntent, 0);

        String diff = TimeManager.difference(
                new Timestamp(Calendar.getInstance().getTimeInMillis()),
                time);

        //add time too
        String d = message;

        mBuilder= new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentTitle("Message from " + sender.name + " " + diff)
                .setContentText(d)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(d))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(totalNotifications, mBuilder.build());
        return totalNotifications;
    }


    public static int createCallNotification(Context context, String callID){

        NotificationCompat.Builder mBuilder;
        Intent callIntent=new Intent(context.getApplicationContext(), CurrentCallActivity.class);
        callIntent.putExtra("callId",callID);
        callIntent.putExtra("status", "ongoing");
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, callNotificationID, callIntent, 0);


        mBuilder= new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentTitle("Ongoing Call")
                .setContentText("Click to return to call")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Click to return to call"))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setAutoCancel(false);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(callNotificationID, mBuilder.build());
        return callNotificationID;
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

    public static void cancel(Context context){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(callNotificationID);
    }

}
