package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private View privateChatView;
    private RecyclerView chatList;
    private String currentUserID;

    private FirebaseAuth mAuth;
    private DatabaseReference chatsRef, userRef;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        privateChatView = inflater.inflate(R.layout.fragment_chat, container, false);

        chatList = privateChatView.findViewById(R.id.privateChatListFragment);
        chatList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        chatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");




        return privateChatView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatsRef, Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts, ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model) {


                        // mendapatkan seluruh key dari cabang child "Contacts"
                        final String usersID = getRef(position).getKey();
                        // agar ketika membuka chat activity gambar yang ditampilkan bukan gambar yang terakhir diambil dari database
                        final String[] retImage = {"defaultImage"};
                        userRef.child(usersID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()){

                                    if (dataSnapshot.hasChild("image")){
                                        retImage[0] = dataSnapshot.child("image").getValue().toString();

                                        Picasso.get().load(retImage[0]).placeholder(R.drawable.profile_image).into(holder.profileImage);
                                    }

                                    final String retName = dataSnapshot.child("name").getValue().toString();

                                    holder.userName.setText(retName);
                                    holder.userStatus.setText("Terkahir Dilihat" + "\n" + "Data" + " Time");

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent chatsIntent = new Intent(getContext(), ChatActivity.class);
                                            chatsIntent.putExtra("visitUserID", usersID);
                                            chatsIntent.putExtra("visitUserName", retName);
                                            chatsIntent.putExtra("visitUserImage", retImage[0]);
                                            startActivity(chatsIntent);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout, parent, false);

                        return new ChatsViewHolder(view);
                    }
                };
        chatList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class ChatsViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        TextView userName, userStatus;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.userProfileImage);
            userName = itemView.findViewById(R.id.userProfileName);
            userStatus = itemView.findViewById(R.id.userStatus);
        }
    }
}
