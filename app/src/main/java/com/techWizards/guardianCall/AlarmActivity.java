package com.techWizards.guardianCall;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AlarmActivity extends AppCompatActivity {
    private String deviceId;
    private AlarmAdapter adapter;
    private int hourOfDay, minute;
    private String message ;
    private TextView backIcon;
    private TextView hh , mm ;
    private EditText alarmMsg;
    private Button amPm , saveBtn;
    private Button[] days ;
    private boolean isAm;
    Map<Button, Boolean> dayStatesMap;

    private boolean[] daysOfWeek = new boolean[7]; // For Monday to Sunday

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetails", MODE_PRIVATE);
        deviceId = sharedPreferences.getString("deviceId", "defaultStringValue");

        backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        hh = findViewById(R.id.hh);
        mm = findViewById(R.id.mm);
        alarmMsg = findViewById(R.id.alarmMsg);
        amPm = findViewById(R.id.amPm);
        saveBtn = findViewById(R.id.saveButton);
        days = new Button[]{
                findViewById(R.id.sundayBtn),
                findViewById(R.id.mondayBtn),
                findViewById(R.id.tuesdayBtn),
                findViewById(R.id.wednesdayBtn),
                findViewById(R.id.thursdayBtn),
                findViewById(R.id.fridayBtn),
                findViewById(R.id.saturdayBtn),
        };


        //this will make lose focus from hh , mm ediTexts if touch down event occurs outside the text field
//        View rootView = findViewById(android.R.id.content);
//        rootView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    View currentFocus = getCurrentFocus();
//                    if (currentFocus instanceof EditText) {
//                        Rect outRect = new Rect();
//                        currentFocus.getGlobalVisibleRect(outRect);
//                        if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
//                            if (currentFocus == hh || currentFocus == mm) {
//                                currentFocus.clearFocus();
//                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
//                            }
//                        }
//                    }
//                }
//                return true;
//            }
//        });

        hh.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString();
                if (!input.isEmpty()) {
                    int value = Integer.parseInt(input);
                    if (value > 13) {
                        Snackbar.make(hh, "Maximum Hour value is 12", Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show();
                        hh.setText(null);
                    }
                }
            }
        });

        hh.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                String text = hh.getText().toString();
                int num;
                if (!text.equals("")){
                    num = Integer.parseInt(text);
                    if (!hasFocus &&  num< 10 ) {
                        hh.setText("0" + text);
                    }else if(hasFocus){
                        hh.setText(null);
                    }

//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        mm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString();
                if (!input.isEmpty()) {
                    int value = Integer.parseInt(input);
                    if (value > 59) {
                        Snackbar.make(mm, "Maximum Minute value is 59", Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show();
                        mm.setText(null);
                    }
                }
            }
        });

        mm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String text = mm.getText().toString();
                int num;
                if (!text.equals("")){
                    num = Integer.parseInt(text);
                    if (!hasFocus &&  num< 10 ) {
                        mm.setText("0" + text);
                    }else if(hasFocus){
                        mm.setText(null);
                    }
                }

            }
        });



        isAm = true;
        amPm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAm){
                    isAm = false;
                    amPm.setText("pm");
                }else{
                    isAm = true;
                    amPm.setText("am");
                }
            }
        });

        dayStatesMap = new HashMap<>();
        for (Button day : days) {
            dayStatesMap.put(day, false);
        }

        for (Button day : days) {
            day.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean currentState = dayStatesMap.get(day);
                    dayStatesMap.put(day, !currentState);

                    if (!currentState) {
                        day.setBackgroundResource(R.drawable.textbox_border_rounded_selected);
                    } else {
                        day.setBackgroundResource(R.drawable.text_box_border_rounded);
                    }
                }
            });
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hour = hh.getText().toString().trim();
                String minute = mm.getText().toString().trim();
                String message = alarmMsg.getText().toString().trim();

                if (!hour.isEmpty() && !minute.isEmpty() && !message.isEmpty()){
                    int H = Integer.parseInt(hour);
                    if (!isAm){
                        H +=12;
                    }

                    String time = H + minute;
                    System.out.println(time);
                    System.out.println(message);

                    DatabaseReference alarmDatabase = FirebaseDatabase.getInstance().getReference("Devices").child(deviceId).child("Alarms");
                    int i =0;
                    Map <String, Object> alarms = new HashMap<>();
                    for (Button btn: days){
                        if( dayStatesMap.get(btn)){
                            String key = i + "/" + time;
                            alarms.put(key , message);
                        }
                        i++;
                    }

                    alarmDatabase.updateChildren(alarms)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Update was successful
                                    Toast.makeText(AlarmActivity.this , "Alarm set successful", Toast.LENGTH_SHORT).show();
                                    Log.d("Firebase", "Data written successfully.");
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Update failed
                                    Toast.makeText(AlarmActivity.this , "Databse error : " + e , Toast.LENGTH_SHORT).show();
                                    Log.d("Firebase", "Data write failed.", e);
                                }
                            });


                }else{

                }

            }
        });

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
                //alarmList.add(alarm);
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceId).child("Alarms").child(String.valueOf(alarm.getDayOfWeek()));
        reference.child(String.valueOf(alarm.getHourOfDay()) +path).removeValue();

        //alarmList.remove(alarm);
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

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Devices").child(deviceId).child("Alarms").child(String.valueOf(alarm.getDayOfWeek()));
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
