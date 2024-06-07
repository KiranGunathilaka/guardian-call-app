package com.techWizards.guardianCall;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AlarmRemove extends AppCompatActivity {
    private TextView backIcon , msgContainer;
    private Button removeBtn;
    private DatabaseReference alarmsDatabase;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_remove);

        Intent intent = getIntent();
        String description = intent.getStringExtra("msg");
        String time = intent.getStringExtra("time");
        String[] arr = intent.getStringArrayExtra("daysArr");
        String deviceId = intent.getStringExtra("deviceId");

        msgContainer = findViewById(R.id.msgContainer);
        msgContainer.setText(description);

        String path = "Devices/" + deviceId + "/Alarms";
        alarmsDatabase = FirebaseDatabase.getInstance().getReference(path);

        backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        removeBtn = findViewById(R.id.removeButton);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i =0 ; i<7 ; i++){
                    if (arr[i].equals("1")){
                        int finalI = i;
                        alarmsDatabase.child(String.valueOf(i)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.child(time).exists()) {
                                    if (snapshot.child(time).getValue().equals(description)) {
                                        alarmsDatabase.child(String.valueOf(finalI)).child(time).removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Node successfully deleted
                                                        Toast.makeText(AlarmRemove.this, "Alarm deleted", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Failed to delete node
                                                        Toast.makeText(AlarmRemove.this, "Failed to delete Alarm", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(AlarmRemove.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });

    }


}
