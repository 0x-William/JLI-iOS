package com.pt.music.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.pt.music.R;
import com.pt.music.activity.DashboardActivity;
import com.pt.music.activity.MainActivity;
import com.pt.music.activity.SplashActivity;

import io.intercom.android.sdk.Intercom;

/**
 * Created by pro on 3/17/16.
 */
public class MyGCMListenerService extends GcmListenerService {

    private static final String TAG = "MyGCMListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }


        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database..
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        long when = System.currentTimeMillis();
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int totalCount = Intercom.client().getUnreadConversationCount();
        String bMessage = message;
        if(totalCount > 1){
            bMessage += "\n\n" + "You have total " + totalCount + " unread messages";
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("JLI Notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bMessage))
                .setContentText(message)
                .setTicker(message)
                .setAutoCancel(true)
                .setVibrate(new long[]{100, 250, 100, 250, 100, 250})
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setWhen(when)
                .setPriority(Notification.PRIORITY_MAX);

//        int defaults = 0;
//        defaults = Notification.DEFAULT_ALL;
//        notificationBuilder.setDefaults(defaults);

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, notificationBuilder.build());

    }
}