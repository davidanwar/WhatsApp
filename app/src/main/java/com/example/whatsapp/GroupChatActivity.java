package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton sendMessageButton;
    private EditText userMessageInput;
    private TextView displayTextmessage;
    private ScrollView mScrollView;
    private String currentGroupName, currrentUserID, currentUserName, currentDate, currentTime;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, groupRef, groupMessageKeyRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        // posisi currentGroupName harus diatas karena informasinya dibutuhkan untuk aksi setelahnya
        currentGroupName = getIntent().getExtras().get("groupName").toString();

        mAuth = FirebaseAuth.getInstance();
        currrentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        initializeFields();

        getUserInfo();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessageInfoToDatabase();
                userMessageInput.setText("");
            }
        });


    }


    private void initializeFields() {
        mToolbar = findViewById(R.id.groupCharBarLayout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);

        sendMessageButton = findViewById(R.id.sendMessageGroupButton);
        userMessageInput = findViewById(R.id.inputGroupMessage);
        mScrollView = findViewById(R.id.myScrollView);
        displayTextmessage = findViewById(R.id.groupChatTextDisplay);

    }

    private void getUserInfo() {
        userRef.child(currrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveMessageInfoToDatabase() {
        String message = userMessageInput.getText().toString();

        // .puh untuk mendapatkan ID dari child
        String messageKey = groupRef.push().getKey();
        if (TextUtils.isEmpty(message)){
            Toast.makeText(this, "Please Write A Message", Toast.LENGTH_SHORT).show();
        } else {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a"); // a = AM atau PM
            currentTime = currentTimeFormat.format(calForTime.getTime());

            // mengupdate group dengan tambahan child ID
            HashMap<String, Object> groupMessageKey = new HashMap<>();
            groupRef.updateChildren(groupMessageKey);

            groupMessageKeyRef = groupRef.child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
                messageInfoMap.put("name", currentUserName);
                messageInfoMap.put("message", message);
                messageInfoMap.put("date", currentDate);
                messageInfoMap.put("time", currentTime);
            groupMessageKeyRef.updateChildren(messageInfoMap);
        }
    }
}
