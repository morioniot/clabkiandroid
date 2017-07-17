package com.morion.clabki;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * Created by morion on 28/06/17.
 */

public class NotificationFactory {

    public Notification buildBeaconDetectedNotification(Context context, String macAddress) {

        //Creating the notification and setting its content
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context);
        notificationBuilder.setSmallIcon(R.drawable.dog_notification);
        notificationBuilder.setContentTitle("Lost doggy close");
        notificationBuilder.setContentText("MAC: " + macAddress);

        //Setting sound, lights and vibration of notification
        Uri notificationSound =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder.setSound(notificationSound);
        notificationBuilder.setLights(Color.BLUE, 2500, 500);
        notificationBuilder.setVibrate(new long[] {100, 500, 100, 100, 100, 100, 100, 100});

        //Creating the pending intent for the main notification action
        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //Setting the notification action
        notificationBuilder.setContentIntent(resultPendingIntent);

        //Returning the notification object after building
        return notificationBuilder.build();
    }

    public Notification buildTurnOnBluetoothNotification(Context context) {

        //Creating the notification and setting its content
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context);
        notificationBuilder.setSmallIcon(R.drawable.dog_notification);
        notificationBuilder.setContentTitle("Help us!");
        notificationBuilder.setContentText("Click this notification to turn on your bluetooth " +
                "and help us to bring more dogs to home");

        //Setting sound, lights and vibration of notification
        Uri notificationSound =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder.setSound(notificationSound);
        notificationBuilder.setLights(Color.BLUE, 2500, 500);
        notificationBuilder.setVibrate(new long[] {200, 200, 200, 200, 200});

        //Creating the pending intent for the main notification action
        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //Setting the notification action
        notificationBuilder.setContentIntent(resultPendingIntent);

        //Returning the notification object after building
        return notificationBuilder.build();
    }
}
