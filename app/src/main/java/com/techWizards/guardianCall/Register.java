package com.techWizards.guardianCall;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {
    private FirebaseAuth auth;
    private DatabaseReference usersDatabase, devicesDatabase;
    private EditText email , pwd , confirmPwd , devId , secKey;
    private Button registerButton;
    private TextView loginRedirect;

    private boolean usernameExists , passwordsMatch , deviceIdExists , isSecretKeyMatched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        usersDatabase = FirebaseDatabase.getInstance().getReference("Users");
        devicesDatabase = FirebaseDatabase.getInstance().getReference("Devices");


        email = findViewById(R.id.registerEmail);
        pwd = findViewById(R.id.registerPwd);
        confirmPwd = findViewById(R.id.registerConfPwd);
        devId = findViewById(R.id.registerDeviceID);
        secKey = findViewById(R.id.registerSecretkey);
        loginRedirect = findViewById(R.id.signInRedirectText);
        registerButton = findViewById(R.id.registerButton);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String userName = email.getText().toString().trim();
                checkUsernameExists(userName);
            }
        });

        confirmPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (pwd.getText().toString().trim().equals(confirmPwd.getText().toString().trim())){
                    Snackbar.make(confirmPwd, "Password confirmed", Snackbar.LENGTH_SHORT).setBackgroundTint(Color.GREEN).show();
                    passwordsMatch = true;
                } else {
                    Snackbar.make(confirmPwd, "Passwords do not match", Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show();
                    passwordsMatch = false;
                }
            }
        });


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
                }
                if( password.isEmpty()){
                    pwd.setError("Password can't be empty");
                }
                if( confPassword.isEmpty() || confPassword.equals(password)){
                    confirmPwd.setError("Passwords do not match");
                }
                if( deviceId.isEmpty() || isInteger(deviceId)){
                    devId.setError("Device ID should be a Number");
                }
                if( secretKey.isEmpty()){
                    secKey.setError("Secret Key can't be empty");
                }
                if ( !user.isEmpty() && !password.isEmpty()){

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
        Query query = usersDatabase.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Snackbar.make(email, "Username already exists", Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show();
                    usernameExists = true;
                } else {
                    Snackbar.make(email, "Username is available", Snackbar.LENGTH_SHORT).setBackgroundTint(Color.GREEN).show();
                    usernameExists = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                Toast.makeText(Register.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkDeviceIdExists(String username) {
        Query query = devicesDatabase.child("").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Snackbar.make(email, "Username already exists", Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show();
                    usernameExists = true;
                } else {
                    Snackbar.make(email, "Username is available", Snackbar.LENGTH_SHORT).setBackgroundTint(Color.GREEN).show();
                    usernameExists = false;
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