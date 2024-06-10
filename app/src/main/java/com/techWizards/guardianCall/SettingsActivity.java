package com.techWizards.guardianCall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class SettingsActivity extends AppCompatActivity {

    private String deviceId, loggedEmail;
    private DatabaseReference usersDatabase;
    private Button logoutBtn , removeBtn;
    private TextView backIcon, devIdField;
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

        linearLayout = findViewById(R.id.linearLayout);

        //Setting devID and current User email in the relevant TextViews
        devIdField.setText(deviceId);

        usersDatabase = FirebaseDatabase.getInstance().getReference("Users");
        Query query = usersDatabase.orderByChild("deviceId").equalTo(deviceId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            String email;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                linearLayout.removeAllViews();

                // This is to add the current user email at the top and bigger
                View view0 = getLayoutInflater().inflate(R.layout.email_card, null);
                TextView textView0 = view0.findViewById(R.id.emailsTextView);
                textView0.setText(loggedEmail);
                textView0.setPadding(30,15,0,15);
                textView0.setTextSize(21);

                LinearLayout.LayoutParams layoutParams0 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                layoutParams0.setMargins(0, 5, 0, 5);

                linearLayout.addView(view0 , layoutParams0);

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
                Toast.makeText(SettingsActivity.this, "User loading failed : " + databaseError, Toast.LENGTH_SHORT).show();
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

                stopService(new Intent(SettingsActivity.this , NotificationService.class));

                Intent intent = new Intent(SettingsActivity.this, SignInActivity.class);
                // Setting flags to clear the current task and start a new task with SignInActivity as the root
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

    }

}