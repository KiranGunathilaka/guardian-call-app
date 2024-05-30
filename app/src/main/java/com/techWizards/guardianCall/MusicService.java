package com.techWizards.guardianCall;

import static com.techWizards.guardianCall.LoginActivity.ID;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
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
    public static MediaPlayer mp;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        createNotificationChannel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(ID).child("Buttons").child("2").child("Status");


        mp = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);



        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    String statusFromDB = String.valueOf(snapshot.getValue(Integer.class));
                    Toast.makeText(MusicService.this, statusFromDB, Toast.LENGTH_LONG).show();

                    if (statusFromDB.equals("1")){
                        mp.setLooping(true);
                        mp.start();

                        Intent activityIntent = new Intent(MusicService.this, testActivity.class); // Replace with the activity you want to open
                        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(activityIntent);

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


        Intent notificationIntent = new Intent(this,MainActivity.class); // Change this to your main activity
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Guardian Call Service")
                .setContentText("Running background")
                .setSmallIcon(R.drawable.baseline_account_circle_24) // Replace with your app's icon
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX);

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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Music Service Channel";
            String description = "Channel for Music Service";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
