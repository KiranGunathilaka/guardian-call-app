package com.techWizards.guardianCall;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private EditText email , pwd , confirmPwd , devId , secKey;
    private Button registerButton;
    private TextView loginRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        email = findViewById(R.id.registerEmail);
        pwd = findViewById(R.id.registerPwd);
        confirmPwd = findViewById(R.id.registerConfPwd);
        devId = findViewById(R.id.registerDeviceID);
        secKey = findViewById(R.id.registerSecretkey);
        loginRedirect = findViewById(R.id.signInRedirectText);
        registerButton = findViewById(R.id.registerButton);



        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user =email.getText().toString().trim();
                String password =pwd.getText().toString().trim();
                String confPassword = confirmPwd.getText().toString().trim();
                String deviceId = devId.getText().toString().trim();
                String secretKey = devId.getText().toString().trim();

                if ( user.isEmpty()){
                    email.setError("Username can't be empty");
                } else if( password.isEmpty()){
                    pwd.setError("Password can't be empty");
                } else if( confPassword.isEmpty() || confPassword.equals(password)){
                    confirmPwd.setError("Passwords do not match");
                } else if( deviceId.isEmpty() || isInteger(deviceId)){
                    devId.setError("Device ID should be a Number");
                } else if( secretKey.isEmpty()){
                    secKey.setError("Secret Key can't be empty");
                }else{

                }

            }
        });

        loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, SignIn.class);
                startActivity(intent);
            }
        });

    }

    private boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void checkUsernameExists(String username) {
        Query query = mDatabase.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Username exists
                    Toast.makeText(Register.this, "Username already exists!", Toast.LENGTH_SHORT).show();
                } else {
                    // Username does not exist
                    Toast.makeText(Register.this, "Username is available!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                Toast.makeText(Register.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}