package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton sendMessageButton;
    private EditText userMessageInput;
    private TextView displayTextmessage;
    private ScrollView mScrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        initializeFields();
    }

    private void initializeFields() {
        mToolbar = findViewById(R.id.groupCharBarLayout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Group Name");

        sendMessageButton = findViewById(R.id.sendMessageGroupButton);
        userMessageInput = findViewById(R.id.inputGroupMessage);
        mScrollView = findViewById(R.id.myScrollView);
        displayTextmessage = findViewById(R.id.groupChatTextDisplay);

    }
}
