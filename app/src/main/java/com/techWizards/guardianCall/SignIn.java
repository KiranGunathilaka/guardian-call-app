package com.techWizards.guardianCall;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signInEmail, signInPwd;
    private Button signInButton;
    private TextView registerRedirect;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        auth =FirebaseAuth.getInstance();
        signInEmail = findViewById(R.id.signInEmail);
        signInPwd = findViewById(R.id.signInPwd);
        signInButton = findViewById(R.id.logInButton);
        registerRedirect = findViewById(R.id.signInRegRedirect);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = signInEmail.getText().toString().trim();
                String pass = signInPwd.getText().toString().trim();

                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!pass.isEmpty()) {
                        auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(SignIn.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                        String userId = authResult.getUser().getUid();
                                        DatabaseReference usersDatabase = FirebaseDatabase.getInstance().getReference("Users");
                                        usersDatabase.child(userId).child("deviceId").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot snapshot) {
                                                if(snapshot.exists()){
                                                    deviceId = snapshot.child("deviceId").getValue(String.class);
                                            } else{
                                                    Toast.makeText(SignIn.this, "Something went wrong. Contact customer Service" , Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(SignIn.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        Intent intent = new Intent(SignIn.this, MainActivity.class);
                                        intent.putExtra("DeviceID", deviceId);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignIn.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        signInPwd.setError("Empty fields are not allowed");
                    }
                } else if (email.isEmpty()) {
                    signInEmail.setError("Empty fields are not allowed");
                } else {
                    signInEmail.setError("Please enter correct email");
                }
            }
        });

        registerRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, Register.class);
                startActivity(intent);
            }
        });
    }

}
