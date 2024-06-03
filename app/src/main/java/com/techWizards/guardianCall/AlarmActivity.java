package com.techWizards.guardianCall;


import static com.techWizards.guardianCall.MainActivity.deviceId;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {

    public static String ID = deviceId;
    private ArrayList<Alarm> alarmList;
    private AlarmAdapter adapter;
    private int hourOfDay, minute;

    private String message;

    private boolean[] daysOfWeek = new boolean[7]; // For Monday to Sunday

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);


        alarmList = new ArrayList<>();
        adapter = new AlarmAdapter(this, alarmList);



        ListView alarmListView = findViewById(R.id.alarmListView);
        alarmListView.setAdapter(adapter);

        Button setAlarmButton = findViewById(R.id.setAlarmButton);
        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        saveAlarms();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAlarms();
    }

    private void saveAlarms() {
        SharedPreferences sharedPreferences = getSharedPreferences("alarms", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String alarmsJson = gson.toJson(alarmList);
        editor.putString("alarms", alarmsJson);
        editor.apply();
    }

    private void loadAlarms() {
        SharedPreferences sharedPreferences = getSharedPreferences("alarms", MODE_PRIVATE);
        String alarmsJson = sharedPreferences.getString("alarms", null);
        if (alarmsJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Alarm>>() {}.getType();
            ArrayList<Alarm> savedAlarms = gson.fromJson(alarmsJson, type);
            if (savedAlarms != null) {
                alarmList.clear();
                alarmList.addAll(savedAlarms);
                adapter.notifyDataSetChanged();
            }
        }
    }



    private void showTimePickerDialog() {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setOnTimeSetListener(new TimePickerFragment.OnTimeSetListener() {
            @Override
            public void onTimeSet(int hourOfDay, int minute) {
                AlarmActivity.this.hourOfDay = hourOfDay;
                AlarmActivity.this.minute = minute;
                showDayPickerDialog();
            }
        });
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void showDayPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_day_picker, null);
        builder.setView(dialogView);

        CheckBox[] checkBoxes = new CheckBox[7];
        checkBoxes[0] = dialogView.findViewById(R.id.checkBoxMonday);
        checkBoxes[1] = dialogView.findViewById(R.id.checkBoxTuesday);
        checkBoxes[2] = dialogView.findViewById(R.id.checkBoxWednesday);
        checkBoxes[3] = dialogView.findViewById(R.id.checkBoxThursday);
        checkBoxes[4] = dialogView.findViewById(R.id.checkBoxFriday);
        checkBoxes[5] = dialogView.findViewById(R.id.checkBoxSaturday);
        checkBoxes[6] = dialogView.findViewById(R.id.checkBoxSunday);

        Button confirmButton = dialogView.findViewById(R.id.buttonConfirmDays);
        AlertDialog dialog = builder.create();
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 7; i++) {
                    daysOfWeek[i] = checkBoxes[i].isChecked();
                }
                dialog.dismiss();

                showMessageDialog();
            }
        });

        dialog.show();

    }

    private void showMessageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_alarm_message, null);
        builder.setView(dialogView);



        Button confirmMessageButton = dialogView.findViewById(R.id.buttonConfirmMessage);
        AlertDialog dialog = builder.create();

        EditText editText = dialogView.findViewById(R.id.alarmMessageEditText);
        confirmMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = editText.getText().toString();
                setRecurringAlarms(hourOfDay, minute, daysOfWeek,message);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setRecurringAlarms(int hourOfDay, int minute, boolean[] daysOfWeek, String message) {
        for (int i = 0; i < 7; i++) {
            if (daysOfWeek[i]) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.DAY_OF_WEEK, i + 2); // Calendar.SUNDAY is 1

                if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                    calendar.add(Calendar.WEEK_OF_YEAR, 1);
                }

                int requestCode = (int) System.currentTimeMillis() + i;

                Alarm alarm = new Alarm(hourOfDay, minute, i, requestCode,message);
                alarmList.add(alarm);
                adapter.notifyDataSetChanged();
            }
        }
    }



    private void stopAlarm(Alarm alarm) {
        Toast.makeText(this, (getDayName(alarm.getDayOfWeek()) + alarm.getHourOfDay() + alarm.getMinute() + alarm.getMessage()), Toast.LENGTH_LONG).show();

        String path;

        if (alarm.getMinute()<10){
            path = "0" + String.valueOf(alarm.getMinute());
        }else{
            path = String.valueOf(alarm.getMinute());
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Devices").child(ID).child("Alarms").child(String.valueOf(alarm.getDayOfWeek()));
        reference.child(String.valueOf(alarm.getHourOfDay()) +path).removeValue();

        alarmList.remove(alarm);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }

    private class AlarmAdapter extends ArrayAdapter<Alarm> {

        public AlarmAdapter(Context context, ArrayList<Alarm> alarms) {
            super(context, 0, alarms);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.alarm_list_item, parent, false);
            }

            Alarm alarm = getItem(position);

            TextView alarmTimeTextView = convertView.findViewById(R.id.alarmTimeTextView);
            TextView alarmMessageTextView = convertView.findViewById(R.id.alarmMessageTextView);
            Button deleteAlarmButton = convertView.findViewById(R.id.deleteAlarmButton);

            String dayName = getDayName(alarm.getDayOfWeek());
            String alarmTime = dayName + " at " + alarm.getHourOfDay() + ":" + (alarm.getMinute() < 10 ? "0" + alarm.getMinute() : alarm.getMinute());
            alarmTimeTextView.setText(alarmTime);
            alarmMessageTextView.setText(message);

            String path;

            if (alarm.getMinute()<10){
                path = "0" + String.valueOf(alarm.getMinute());
            }else{
                path = String.valueOf(alarm.getMinute());
            }

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Devices").child(ID).child("Alarms").child(String.valueOf(alarm.getDayOfWeek()));
            reference.child(String.valueOf(alarm.getHourOfDay()) +path).setValue(alarm.getMessage());

            deleteAlarmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopAlarm(alarm);
                }
            });

            return convertView;
        }
    }

    private String getDayName(int dayIndex) {
        switch (dayIndex) {
            case 0:
                return "Monday";
            case 1:
                return "Tuesday";
            case 2:
                return "Wednesday";
            case 3:
                return "Thursday";
            case 4:
                return "Friday";
            case 5:
                return "Saturday";
            case 6:
                return "Sunday";
            default:
                return "";
        }
    }
}
