package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverID, messageReceiverName, messageReceiverImage, messageSenderID;
    private TextView userName, userLastSeen;
    private CircleImageView userImage;
    private Toolbar chatToolbar;
    private ImageButton sendMessageButton;
    private EditText messageInputText;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        messageReceiverID = getIntent().getExtras().get("visitUserID").toString();
        messageReceiverName = getIntent().getExtras().get("visitUserName").toString();
        messageReceiverImage = getIntent().getExtras().get("visitUserImage").toString();

        initializeControllers();

        userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).into(userImage);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }


    private void sendMessage() {

        String message = messageInputText.getText().toString();

        if (TextUtils.isEmpty(message)){
            Toast.makeText(this, "Tuliskan pesan Anda", Toast.LENGTH_SHORT).show();
        } else {
            String messageSenderRef = "Message/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef = "Message/" + messageReceiverID + "/"+ messageSenderID;

            DatabaseReference userMessageKeyRef = rootRef.child("Messages")
                    .child(messageSenderID).child(messageReceiverID).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", message);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);

            Map messageBodyDetail = new HashMap();
            messageBodyDetail.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetail.put(messageReceiverRef+ "/" + messagePushID, messageTextBody);

            rootRef.updateChildren(messageBodyDetail).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    
                    if (task.isSuccessful()){
                        Toast.makeText(ChatActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    messageInputText.setText("");
                }
            });


        }

    }

    private void initializeControllers() {

        chatToolbar = findViewById(R.id.chatToolbar);
        setSupportActionBar(chatToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        // memasukan custom layout ke action bar
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);

        // harus diinisialisasi setelah menerapkan layout
        userName = findViewById(R.id.customProfileName);
        userLastSeen = findViewById(R.id.customUserLastSeen);
        userImage = findViewById(R.id.customProfileImage);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        messageInputText = findViewById(R.id.inputMessage);
    }
}
