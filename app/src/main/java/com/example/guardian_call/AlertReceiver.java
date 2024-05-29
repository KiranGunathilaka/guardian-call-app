package com.example.guardian_call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import java.util.Timer;
import java.util.TimerTask;


public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(1, nb.build());

        Uri url = Uri.parse("android.resource://" + context.getPackageName() + "/" + Settings.System.DEFAULT_RINGTONE_URI);

        final Ringtone[] _ringtone = {RingtoneManager.getRingtone(context, url)};


        Timer _start_ringtone = new Timer();
        //*Note: The phone needs to be set to ringing mode for it to work.
        _ringtone[0].play();

        _start_ringtone.schedule(new TimerTask() {
            @Override
            public void run() {
                _ringtone[0].stop();
                _ringtone[0] =null;
            }
            //Music plays for 10 seconds. 1s = 1000ms
        }, 10000);

    }
}
