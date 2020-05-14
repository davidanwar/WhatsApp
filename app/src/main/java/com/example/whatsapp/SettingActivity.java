package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private Button updateAccountSetting;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        
        initializeFields();
    }

    private void initializeFields() {
        updateAccountSetting = findViewById(R.id.updateSettingsButton);
        userName = findViewById(R.id.setUserName);
        userStatus = findViewById(R.id.setProfileStatus);
        userProfileImage = findViewById(R.id.setProfileImage);

    }
}
