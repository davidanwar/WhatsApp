package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PhoneLoginActivity extends AppCompatActivity {

    private Button sendVerificationCodeButton, verifyButton;
    private EditText inputPhoneNumber, inputVerificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        sendVerificationCodeButton = findViewById(R.id.sendVerificationCodeButton);
        verifyButton = findViewById(R.id.verifyButton);
        inputPhoneNumber = findViewById(R.id.phoneNumberInput);
        inputVerificationCode = findViewById(R.id.verificationCodeInput);

        sendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCodeButton.setVisibility(View.INVISIBLE);
                inputPhoneNumber.setVisibility(View.INVISIBLE);

                verifyButton.setVisibility(View.VISIBLE);
                inputVerificationCode.setVisibility(View.VISIBLE);
            }
        });
    }
}
