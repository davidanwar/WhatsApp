package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private Button updateAccountSetting;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        
        initializeFields();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        userName.setVisibility(View.INVISIBLE);

        updateAccountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSettings();
            }
        });

        retrieveUserInfo();
    }


    private void initializeFields() {
        updateAccountSetting = findViewById(R.id.updateSettingsButton);
        userName = findViewById(R.id.setUserName);
        userStatus = findViewById(R.id.setProfileStatus);
        userProfileImage = findViewById(R.id.setProfileImage);
    }

    private void updateSettings() {
        String setUserName = userName.getText().toString();
        String setUserStatus = userStatus.getText().toString();
        if (TextUtils.isEmpty(setUserName)){
            Toast.makeText(this, "Please Write Name...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setUserStatus)){
            Toast.makeText(this, "Please Write Status", Toast.LENGTH_SHORT).show();
        }
        else {
            final HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("name", setUserName);
            profileMap.put("status", setUserStatus);
            rootRef.child("Users").child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                sendUserToMainActivity();
                                Toast.makeText(SettingActivity.this, "Profile Update Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                String errorMessage = task.getException().toString();
                                Toast.makeText(SettingActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private void retrieveUserInfo() {
        rootRef.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))
                                && (dataSnapshot.hasChild("image"))){
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                            String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();
                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveStatus);

                        } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveStatus);

                        } else {
                            userName.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingActivity.this, "Please set & update profile information...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingActivity.this, MainActivity.class);
        // agar tidak bisa menekan tombol back
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
