package com.jackdonaldson.majorwork2019.Notifications;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ButtonReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.println(Log.WARN,"JACKDEBUG","HASFHAUHSDFUHIEWQUIFHWEUIF");
        int notId = intent.getIntExtra("notID", 0);
        int buttonId = intent.getIntExtra("buttonID", 0);
        //Log.println(Log.WARN,"JACKDEBUG","A:"+notId+"B:"+buttonId);
        if(buttonId == 1) {
            //Log.println(Log.WARN,"JACKDEBUG","1111111111111111111111");
            //Message received notification and dismissed button pressed
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(notId);
    }
    }
}