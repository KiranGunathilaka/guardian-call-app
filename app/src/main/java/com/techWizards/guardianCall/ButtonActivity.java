package com.techWizards.guardianCall;

import static com.techWizards.guardianCall.MainActivity.deviceId;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ButtonActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private TextView percentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Reference to your data in Firebase
        DatabaseReference myDataRef = mDatabase.child("Devices").child(deviceId).child("Buttons").child("1").child("Battery");;

        percentage = findViewById(R.id.percentage);

        // Read data from Firebase
        myDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get the value from Firebase
                float firebaseData = dataSnapshot.getValue(float.class);

                double num = 123 - 123/Math.pow((1+Math.pow((firebaseData/3.7),80)),0.165);

                int num_int = (int) num;

                // Set the value to EditText
                percentage.setText("Button Battery Percentage : " + String.valueOf(num_int));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors
            }
        });
    }


}