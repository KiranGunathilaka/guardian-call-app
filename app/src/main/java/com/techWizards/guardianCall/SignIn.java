package com.techWizards.guardianCall;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signInEmail, signInPwd;
    private Button signInButton;
    private TextView registerRedirect;

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
                String user = signInEmail.getText().toString().trim();
                String pwd = signInPwd.getText().toString().trim();

                if (user.isEmpty()){
                    signInEmail.setError("Username can't be empty");
                }
                else if (pwd.isEmpty()){
                    signInPwd.setError("Password can't be empty");
                }else{

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
