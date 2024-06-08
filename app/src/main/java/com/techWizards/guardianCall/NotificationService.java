package com.techWizards.guardianCall;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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

import java.io.IOException;
import java.util.ArrayList;


public class NotificationService extends Service {
    private static final String CHANNEL_ID = "MusicServiceChannel";
    private static final String POPUP_CHANNEL_ID = "PopupNotificationChannel";
    public static MediaPlayer mp;
    private static String deviceId;
    private DatabaseReference buttonsDatabase;
    private String[] btnArr;



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
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetails", MODE_PRIVATE);
        deviceId = sharedPreferences.getString("deviceId", "defaultStringValue");



        buttonsDatabase = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceId).child("Buttons");


        buttonsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> activatedBtnArr = new ArrayList<>();
                for (DataSnapshot btn : snapshot.getChildren()){
                        if (btn.child("Status").getValue(Integer.class) == 1){
                            activatedBtnArr.add(btn.getKey());
                        }
                }
                btnArr = new String[activatedBtnArr.size()];
                btnArr = activatedBtnArr.toArray(btnArr);

                if (activatedBtnArr.size() > 0){ //bug fix
                    if (mp != null){  // if not, multiple instances of mp will run and same ringintone will be heard twice.
                        mp.stop();
                        mp.release();
                        mp = null ;
                    }
                    mp = MediaPlayer.create(NotificationService.this, Settings.System.DEFAULT_RINGTONE_URI);
                    mp.setLooping(true);
                    mp.start();

                    showPopupNotification(btnArr);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NotificationService.this, "Database Error! Notification retrieve failed : "+ error, Toast.LENGTH_SHORT).show();
            }
        });



        long[] enablingServicePattern = {0, 500, 200, 300};

        Intent notificationIntent = new Intent(this, Main.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Guardian Call")
                .setContentText("Listening for emergencies of your loved ones")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setSound(null)
                .setVibrate(enablingServicePattern);

        startForeground(1, notificationBuilder.build());

        return START_STICKY;
    }


    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Music Service";
            String description = "Requires to ring a tone when receiving notifications";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            CharSequence popupName = "Popup Notification";
            String popupDescription = "Required for displaying Popup Notifications";
            int popupImportance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel popupChannel = new NotificationChannel(POPUP_CHANNEL_ID, popupName, popupImportance);
            popupChannel.setDescription(popupDescription);

            notificationManager.createNotificationChannel(popupChannel);
        }
    }

    private static int requestCode = 0; //requestCode have to be changed, other wise this pending intents will be cached and won't update.

    private void showPopupNotification(String[] btnArr) {
        Intent popupIntent = new Intent(NotificationService.this, BtnNotifyActivity.class);
        popupIntent.putExtra("Buttons", btnArr);
        PendingIntent popupPendingIntent = PendingIntent.getActivity(this, requestCode++, popupIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        SharedPreferences buttonNames = getSharedPreferences("ButtonNames", MODE_PRIVATE);


        StringBuilder contentTextBuilder = new StringBuilder();
        for (String i: btnArr){
            i = buttonNames.getString(i, i);
            contentTextBuilder.append(i).append(", ");
        }
        if (contentTextBuilder.length() > 0) {// Removed the last comma and space if the string is not empty
            contentTextBuilder.setLength(contentTextBuilder.length() - 2);
        }
        String contentText = contentTextBuilder.toString();


        if (btnArr.length>1){
            contentText += " are having an Emergency";
        } else {
            contentText += " is having an Emergency";
        }

        long[] notificationReceivedPattern = {0, 400, 300, 400, 300 , 400};

        NotificationCompat.Builder popupNotificationBuilder = new NotificationCompat.Builder(this, POPUP_CHANNEL_ID)
                .setContentTitle("Guardian Call Emergency Alert")
                .setContentText(contentText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(popupPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(null)
                .setVibrate(notificationReceivedPattern);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2, popupNotificationBuilder.build());
    }

}
