package com.example.sander.sander_pset6;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Chat extends AppCompatActivity {

    private static final int AMOUNT_OF_POSTS = 100;

    ListView lvMessages;
    EditText etChatMessage;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<Message> arrayMessages;
    ArrayList<String> stringArrayMessages;
    String username;

    /* Firebase related stuff */
    private DatabaseReference dbref;
    private DatabaseReference messagesRef;
    private DatabaseReference usersRef;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ChildEventListener messagesListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // get views
        lvMessages = (ListView) findViewById(R.id.chatLvMessages);
        etChatMessage = (EditText) findViewById(R.id.chatEtMessage);

        // init FB auth
        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("log", "onAuthStateChanged:signed_in:" + user.getUid());
                    displayChat();

                } else {
                    // User is signed out
                    Log.d("log", "onAuthStateChanged:signed_out");
                    sendToLogin();
                }
            }
        };

    }

    private void displayChat() {
        dbref = FirebaseDatabase.getInstance().getReference();
        messagesRef = dbref.child("messages");
        usersRef = dbref.child("users");

        Log.d("log", "messagesRef: " + messagesRef.toString());

        /* read messages from FB */
        // set query for messages
        // Query latestMessagesQuery = messagesRef.orderByKey().limitToLast(AMOUNT_OF_POSTS);

        // define childeventlistener
        messagesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        // add listener
        // latestMessagesQuery.addChildEventListener()

        // get messages from FB


        // add value event listener to listen for new messages
        ValueEventListener messageListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get Message
                Message message = dataSnapshot.getValue(Message.class);
                stringArrayMessages.add(message.getText());
                Log.d("log", "Chat.messageListenerload message success");
                Toast.makeText(Chat.this, "message loaded",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // get message failed, so log it
                Log.d("log", "Chat.messageListenerload message failed");
                Toast.makeText(Chat.this, "Failed to load message.",
                        Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void sendToLogin() {
        // send user to login
        Intent intentToLogin = new Intent(this, Login.class);
        startActivity(intentToLogin);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    private ArrayList<Message> getMessages() {
        // init arraylist
        ArrayList<Message> messages = new ArrayList<>();

        // get uid
        String uid = auth.getCurrentUser().getUid();
        return messages;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onDestroy() {
        // close databasehelper
        super.onDestroy();
    }

    public void sendMessage(View view) {
        Log.d("log", "Chat.sendMessage(): start");
        if (messagesRef != null) {

            // get text from view
            String text = etChatMessage.getText().toString();

            // get user
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                usersRef.child(user.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // get username
                                String username = dataSnapshot.getValue(String.class);
                                Log.d("log", "username: ");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("log", databaseError.toString());
                            }
                        });
//                // write a message to FB
//                Map<String, Object> message = new HashMap<>();
//                message.put("text", text);
//                message.put("senderId", user.getUid());
//                message.put("username", username;
//                Task writingToFB = messagesRef.push().updateChildren(message);
//                writingToFB.addOnCompleteListener(new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        Log.d("log", "Chat.sendMessage: message sent");
//                    }
//                });
            }
        }
    }
}
