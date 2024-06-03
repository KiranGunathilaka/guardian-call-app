package com.techWizards.guardianCall;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {



    Button Alarm;

    Button start;

    Button stop;

    Button battery;

    Button stopService;



    public static  String deviceId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = getIntent();

        // Get the string extra from the intent
        deviceId = intent.getStringExtra("DeviceID");



        // Retrieve the saved DeviceID from SharedPreferences
        String savedDeviceId = MySharedPreferences.getString(this);

        // Check if the savedDeviceId is not empty, then use it
        if (!savedDeviceId.isEmpty()) {
            deviceId = savedDeviceId;
        }


        // Show the deviceId in a Toast message
        if (deviceId != null) {
            MySharedPreferences.saveString(this, deviceId);
            Toast.makeText(this, "DeviceID: " + deviceId, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No DeviceID found in the intent", Toast.LENGTH_LONG).show();
        }

        Alarm = findViewById(R.id.setAlarm);
        start = findViewById(R.id.StartAlert);
        stopService = findViewById(R.id.stopService);
        battery = findViewById(R.id.buttons);





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

        battery.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(MainActivity.this,ButtonActivity.class));

            }
        });



        stopService.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                stopService(new Intent(MainActivity.this,MusicService.class));
            }
        });



    }


}