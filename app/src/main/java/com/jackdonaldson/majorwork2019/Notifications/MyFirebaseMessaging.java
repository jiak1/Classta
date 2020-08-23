package com.jackdonaldson.majorwork2019.Notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jackdonaldson.majorwork2019.MessageActivity;
import com.jackdonaldson.majorwork2019.R;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.println(Log.WARN,"JACKDEBUG","FMS1");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        if(firebaseUser != null){
            updateToken(refreshToken);
        }
    }

    private void updateToken(String refreshToken) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(refreshToken);
        reference.child(firebaseUser.getUid()).setValue(token);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.println(Log.WARN,"JACKDEBUG","FMS2");
        String sented = remoteMessage.getData().get("sented");
        String user = remoteMessage.getData().get("user");

        SharedPreferences preferences = getSharedPreferences("PREFS",MODE_PRIVATE);
        String currentUser = preferences.getString("currentuser","none");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null && sented.equals(firebaseUser.getUid())){
            if(!currentUser.equals(user)) {
                sendNotification(remoteMessage);
            }
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]",""));
        int NOTIFICATION_ID = 0;
        if(j > 0){
            NOTIFICATION_ID = j;
        }

        Intent intent = new Intent(this, MessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent snoozeIntent = new Intent(this, ButtonReceiver.class);
        //Log.println(Log.WARN,"JACKDEBUG","HERERERERERE:"+NOTIFICATION_ID);
        snoozeIntent.putExtra("notID", NOTIFICATION_ID);
        snoozeIntent.putExtra("buttonID", 1);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(this, 0, snoozeIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Key for the string that's delivered in the action's intent.
        final String KEY_TEXT_REPLY = "MSGREPLY";

        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel("Reply")
                .build();

        // Build a PendingIntent for the reply action to trigger.
        PendingIntent replyPendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(),
                        0,
                        new Intent(this, ReplyReceiver.class)
                        .putExtra("notID", NOTIFICATION_ID)
                        .putExtra("sender", user),
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the reply action and add the remote input.
        NotificationCompat.Action replyaction =
                new NotificationCompat.Action.Builder(R.drawable.icon,
                        "Reply", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.icon,"Snooze",
                        snoozePendingIntent)
                .addAction(replyaction)
                .setColor(ContextCompat.getColor(this,R.color.colorPrimary));


        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build());
    }
}
