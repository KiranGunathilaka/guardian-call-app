package com.techWizards.guardianCall;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;




import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {
    public static String ID;
    EditText loginUserID;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        loginUserID = findViewById(R.id.login_userID);
        loginButton = findViewById(R.id.login_button);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUserID()) {

                } else {
                    checkUser();
                }
            }
        });


    }

    public Boolean validateUserID() {
        String val = loginUserID.getText().toString();
        if (val.isEmpty()) {
            loginUserID.setError("UserID cannot be empty");
            return false;
        } else {
            loginUserID.setError(null);
            return true;
        }
    }



    public void checkUser(){
        String userID = loginUserID.getText().toString().trim();

        ID = userID;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();



        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){


                    Toast.makeText(LoginActivity.this,"Login Success", Toast.LENGTH_SHORT).show();


                    Intent intent2 = new Intent(LoginActivity.this, MainActivity.class);

                    startActivity(intent2);

                } else {
                    loginUserID.setError("User does not exist");
                    loginUserID.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}