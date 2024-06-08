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
    private TextView backIcon;
    private TextView hh, mm;
    private EditText alarmMsg;
    private Button amPm, saveBtn;
    private Button[] days;
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


        hh.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

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
                if (!text.equals("")) {
                    num = Integer.parseInt(text);
                    if (!hasFocus && num < 10) {
                        hh.setText("0" + text);
                    } else if (hasFocus) {
                        hh.setText(null);
                    }

                }
            }
        });

        mm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

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
                if (!text.equals("")) {
                    num = Integer.parseInt(text);
                    if (!hasFocus && num < 10) {
                        mm.setText("0" + text);
                    } else if (hasFocus) {
                        mm.setText(null);
                    }
                }

            }
        });


        isAm = true;
        amPm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAm) {
                    isAm = false;
                    amPm.setText("pm");
                } else {
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

                if (!hour.isEmpty() && !minute.isEmpty() && !message.isEmpty()) {
                    int H = Integer.parseInt(hour);
                    if (!isAm) {
                        H += 12;
                    }

                    String time = H + minute;
                    System.out.println(time);
                    System.out.println(message);

                    DatabaseReference alarmDatabase = FirebaseDatabase.getInstance().getReference("Devices").child(deviceId).child("Alarms");
                    int i = 0;
                    Map<String, Object> alarms = new HashMap<>();
                    for (Button btn : days) {
                        if (dayStatesMap.get(btn)) {
                            String key = i + "/" + time;
                            alarms.put(key, message);
                        }
                        i++;
                    }

                    alarmDatabase.updateChildren(alarms)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Update was successful
                                    Toast.makeText(AlarmActivity.this, "Alarm set successful", Toast.LENGTH_SHORT).show();
                                    Log.d("Firebase", "Data written successfully.");
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Update failed
                                    Toast.makeText(AlarmActivity.this, "Databse error : " + e, Toast.LENGTH_SHORT).show();
                                    Log.d("Firebase", "Data write failed.", e);
                                }
                            });


                } else {

                }

            }
        });

    }
}