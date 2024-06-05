package com.techWizards.guardianCall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Settings extends AppCompatActivity {

    private String deviceId, loggedEmail;
    private DatabaseReference usersDatabase;
    private Button logoutBtn , removeBtn;
    private TextView backIcon, devIdField, currentUserField;
    private SharedPreferences sharedPreferences;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("loginDetails", MODE_PRIVATE);
        deviceId = sharedPreferences.getString("deviceId", "defaultStringValue");
        loggedEmail = sharedPreferences.getString("userEmail", "defaultStringValue");

        backIcon = findViewById(R.id.backIcon);
        logoutBtn = findViewById(R.id.logOutButton);
        devIdField = findViewById(R.id.deviceId);
        currentUserField = findViewById(R.id.thisUserField);
        linearLayout = findViewById(R.id.linearLayout);

        //Setting devID and current User email in the relevant TextViews
        devIdField.setText(deviceId);
        currentUserField.setText(loggedEmail);

        usersDatabase = FirebaseDatabase.getInstance().getReference("Users");
        Query query = usersDatabase.orderByChild("deviceId").equalTo(deviceId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            String email;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    //this is why defined another user class. See DataSnapShot documentation
                    User user = snap.getValue(User.class);
                    email = user.getEmail().toString();

                    if (!email.equals(loggedEmail)){
                        //assigning the custom made textView component to view object to add it to the linearLayout container
                        View view = getLayoutInflater().inflate(R.layout.email_card, null);
                        TextView textView = view.findViewById(R.id.emailsTextView);
                        textView.setText(email);
                        textView.setPadding(30,16,0,16);

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        layoutParams.setMargins(0, 5, 0, 5);

                        linearLayout.addView(view , layoutParams);
                    }

                }
            }

            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(Settings.this, "User loading failed : " + databaseError, Toast.LENGTH_SHORT).show();
                Log.w("FirebaseQuery", "loadPost:onCancelled", databaseError.toException());
            }
        });

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("loginDetails", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(Settings.this, SignIn.class);
                // Setting flags to clear the current task and start a new task with SignIn as the root
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

    }

}