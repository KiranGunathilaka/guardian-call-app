package com.techWizards.guardianCall;

import static com.techWizards.guardianCall.MainActivity.deviceId;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;




public class MusicService extends Service {
    private static final String CHANNEL_ID = "MusicServiceChannel";
    private static final String POPUP_CHANNEL_ID = "PopupNotificationChannel";
    public static MediaPlayer mp;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        createNotificationChannels();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceId).child("Buttons").child("1").child("Status");
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceId).child("Buttons").child("2").child("Status");


        //referenceAlarm = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceId).child("Alarms");





        mp = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String statusFromDB = String.valueOf(snapshot.getValue(Integer.class));


                    if (statusFromDB.equals("1")) {
                        mp.setLooping(true);
                        mp.start();
                        showPopupNotification();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (mp != null) {
                    mp.stop();
                    mp.release();
                    mp = null;
                }
            }
        });

        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String statusFromDB = String.valueOf(snapshot.getValue(Integer.class));


                    if (statusFromDB.equals("1")) {
                        mp.setLooping(true);
                        mp.start();
                        showPopupNotification();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (mp != null) {
                    mp.stop();
                    mp.release();
                    mp = null;
                }
            }
        });




        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("DeviceID", deviceId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Guardian Call Service")
                .setContentText("Running background")
                .setSmallIcon(R.drawable.ic_launcher_background) // Replace with your app's icon
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        startForeground(1, notificationBuilder.build());

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
        super.onDestroy();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Music Service Channel";
            String description = "Channel for Music Service";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            CharSequence popupName = "Popup Notification Channel";
            String popupDescription = "Channel for Popup Notifications";
            int popupImportance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel popupChannel = new NotificationChannel(POPUP_CHANNEL_ID, popupName, popupImportance);
            popupChannel.setDescription(popupDescription);

            notificationManager.createNotificationChannel(popupChannel);
        }
    }

    private void showPopupNotification() {
        Intent popupIntent = new Intent(this, MainActivity.class);

        popupIntent.putExtra("DeviceID", deviceId);

        PendingIntent popupPendingIntent = PendingIntent.getActivity(this,
                0, popupIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder popupNotificationBuilder = new NotificationCompat.Builder(this, POPUP_CHANNEL_ID)
                .setContentTitle("Guardian Call Alert")
                .setContentText("")
                .setSmallIcon(R.drawable.ic_launcher_background) // Replace with your app's icon
                .setContentIntent(popupPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2, popupNotificationBuilder.build());
    }
}
