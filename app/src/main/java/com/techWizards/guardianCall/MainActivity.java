package com.techWizards.guardianCall;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    Button Alarm;
    Button start;
    Button stop;
    Button stopService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Alarm = findViewById(R.id.setAlarm);
        start = findViewById(R.id.StartAlert);
        stop = findViewById(R.id.StopAlert);
        stopService = findViewById(R.id.stopService);

        Intent intent = getIntent();
        String devID = intent.getStringExtra("DeviceID");
        Toast.makeText(MainActivity.this, String.valueOf(devID) , Toast.LENGTH_SHORT).show();




        Alarm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(MainActivity.this,AlarmActivity.class));
            }
        });

        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startService(new Intent(MainActivity.this,MusicService.class));
            }
        });
        stopService.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                stopService(new Intent(MainActivity.this,MusicService.class));
            }
        });


        stop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

            }
        });

    }
}