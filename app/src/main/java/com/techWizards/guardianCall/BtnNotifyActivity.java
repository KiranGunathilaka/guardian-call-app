package com.techWizards.guardianCall;

import android.content.Intent;
import android.os.Bundle;
import android.text.NoCopySpan;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BtnNotifyActivity extends AppCompatActivity {

    private Button okBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btn_notify);

        okBtn = findViewById(R.id.okButton);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotificationService.mp != null && NotificationService.mp.isPlaying()) {
                    NotificationService.mp.stop();
                    NotificationService.mp.release();
                    NotificationService.mp = null;
                }

                finish();
            }
        });
    }
}
