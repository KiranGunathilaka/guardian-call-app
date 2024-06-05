package com.techWizards.guardianCall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Main extends AppCompatActivity {

    private String deviceId;
    private FirebaseAuth auth;
    private DatabaseReference devicesDatabase;
    private Button buttonsRedirect;
    private TextView settings , setAlarm ;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms);

        settings = findViewById(R.id.settingsIcon);
        setAlarm = findViewById(R.id.setNewAlarmIcon);

        sharedPreferences = getSharedPreferences("loginDetails", MODE_PRIVATE);
        deviceId = sharedPreferences.getString("deviceId", "defaultStringValue");

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, Settings.class);
                startActivity(intent);
            }
        });
    }

}
