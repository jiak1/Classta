package com.jackdonaldson.majorwork2019.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jackdonaldson.majorwork2019.MessageActivity;
import com.jackdonaldson.majorwork2019.R;
import com.jackdonaldson.majorwork2019.util.SendMessage;

public class ReplyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.println(Log.WARN,"JACKDEBUG","TEXY:"+getMessageText(intent));

        new SendMessage().sendMessage(FirebaseAuth.getInstance().getUid(),intent.getStringExtra("sender"),getMessageText(intent).toString(),true,context);

        int notId = intent.getIntExtra("notID", 0);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notId);
    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence("MSGREPLY");
        }
        return null;
    }
}
