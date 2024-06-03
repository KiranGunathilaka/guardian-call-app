package com.techWizards.guardianCall;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmActivity extends AppCompatActivity {
    EditText timeHour;
    EditText timeMinute;
    Button setTime;
    Button setAlarm;
    TimePickerDialog timePickerDialog;
    Calendar calendar;
    int currentHour;
    int currentMinute;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        sp = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);

        timeHour = findViewById(R.id.etHour);
        timeMinute = findViewById(R.id.etMinute);
        setTime = findViewById(R.id.btnTime);
        setAlarm = findViewById(R.id.btnAlarm);

        Switch simpleSwitch1 = (Switch) findViewById(R.id.simpleSwitch1);
        Button cancelalarm = (Button) findViewById(R.id.cancelalarm);
        String statusSwitch1 = sp.getString("statusSwitch1", "1");

        SharedPreferences.Editor editor = sp.edit();

        if (statusSwitch1.equals("1")) {
            simpleSwitch1.setChecked(true);
        }
        if (statusSwitch1.equals("0")) {
            simpleSwitch1.setChecked(false);
        }

        simpleSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (simpleSwitch1.isChecked()) {
                    editor.putString("statusSwitch1", "1");
                    editor.apply();
                }
                if (!simpleSwitch1.isChecked()) {
                    editor.putString("statusSwitch1", "0");
                    editor.apply();
                }
            }
        });

        setTime.setOnClickListener((v) -> {
            calendar = Calendar.getInstance();
            currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            currentMinute = calendar.get(Calendar.MINUTE);
            timePickerDialog = new TimePickerDialog(AlarmActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                    timeHour.setText(String.format("%02d", hourOfDay));
                    timeMinute.setText(String.format("%02d", minutes));
                }
            }, currentHour, currentMinute, false);

            timePickerDialog.show();

        });

        setAlarm.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View v) {
                String statusSwitch1 = sp.getString("statusSwitch1", "1");
                if (statusSwitch1.equals("1")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("mm");
                    String currenttimeMinute = dateFormat.format(new Date());
                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh");
                    String currenttimeHour = dateFormat2.format(new Date());
                    int hrdiff = Integer.parseInt(timeHour.getText().toString()) - Integer.parseInt(currenttimeHour);
                    int hrinms = hrdiff * 3600000;
                    int mindiff = Integer.parseInt(timeMinute.getText().toString()) - Integer.parseInt(currenttimeMinute);
                    int mininms = mindiff * 60000;
                    int totalms = hrinms + mininms;
                    editor.putInt("totalms", totalms);
                    editor.apply();
                    startAlarm();
                }

                if (!timeHour.getText().toString().isEmpty() && !timeMinute.getText().toString().isEmpty()) {
                    Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                    intent.putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(timeHour.getText().toString()));
                    intent.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(timeMinute.getText().toString()));
                    intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Alarm Notifcation");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(AlarmActivity.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AlarmActivity.this, "Please choose a time", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelalarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });
    }

    @SuppressLint("ScheduleExactAlarm")
    private void startAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent,FLAG_IMMUTABLE);
        Integer totalms = sp.getInt("totalms", 0);
        Integer seconds = sp.getInt("totalms", 0)/1000;
        Toast.makeText(AlarmActivity.this, "Notifcation and Music will ring in " + seconds + "s" , Toast.LENGTH_SHORT).show();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+totalms, pendingIntent);
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(AlarmActivity.this, "Nofication and Music Alarm cancelled", Toast.LENGTH_SHORT).show();
    }
}
