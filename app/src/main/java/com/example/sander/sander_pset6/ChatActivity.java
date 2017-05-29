/*
 * Created by sander on 18-5-17.
 */

package com.example.sander.sander_pset6;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Defines the ChatActivity. This activity provides all of the chat action.
 * Makes use of custom Message and User classes.
 *
 * N.B.: DB is short for database.
 */
public class ChatActivity extends AppCompatActivity {

    private static final int AMOUNT_OF_POSTS = 100;

    ListView lvMessages;
    EditText etChatMessage;
    User user;

    private DatabaseReference messagesRef;
    private DatabaseReference usersRef;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseListAdapter messagesAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        lvMessages = (ListView) findViewById(R.id.chatLvMessages);
        etChatMessage = (EditText) findViewById(R.id.chatEtMessage);

        auth = FirebaseAuth.getInstance();

        startAuthStateListener();
    }

    /**
     * Listens if user is still logged in.
     */
    private void startAuthStateListener() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("log", "onAuthStateChanged:signed_in:" + user.getUid());
                    displayChat(user);

                } else {
                    // User is signed out
                    Log.d("log", "onAuthStateChanged:signed_out");
                    sendToLogin();
                }
            }
        };
    }

    /**
     * Displays the actual chat messages by eventually populating lvMessages.
     */
    private void displayChat(FirebaseUser userFB) {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
        messagesRef = dbref.child("messages");
        usersRef = dbref.child("users");

        getUserInfo(userFB);
        getMessages();
    }

    /**
     * Gets user information for the given FirebaseUser and populates a User instance with it.
     */
    private void getUserInfo(FirebaseUser userFB) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // populate user
                user = dataSnapshot.getValue(User.class);
                user.setUid(dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, getString(R.string.db_error), Toast.LENGTH_SHORT).show();
            }
        };

        usersRef.child(userFB.getUid())
                .addListenerForSingleValueEvent(listener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    /**
     * Gets messages from DB and populates lvMessages with them.
     */
    private void getMessages() {
        messagesAdapter = new FirebaseListAdapter<Message>(
                this,
                Message.class,
                R.layout.list_messages_item,
                messagesRef.limitToLast(AMOUNT_OF_POSTS)) {
            @Override
            protected void populateView(View view, Message message, int position) {
                ((TextView) view.findViewById(R.id.text1)).setText(message.getUsername());
                ((TextView) view.findViewById(R.id.text2)).setText(message.getText());
            }
        };

        // set the adapter of the listview
        lvMessages.setAdapter(messagesAdapter);
    }

    /**
     * Sends user to LoginActivity.
     */
    private void sendToLogin() {
        Intent intentToLogin = new Intent(this, LoginActivity.class);
        startActivity(intentToLogin);
        finish();
    }

    /**
     * Sends a message to the database when send button is clicked.
     */
    public void sendMessage(View view) {
        // if user exists, send the message
        if (user != null) {
            String text = etChatMessage.getText().toString();
            Message message = new Message(user.getUsername(), text, user.getUid());
            messagesRef.push().setValue(message);
            etChatMessage.getText().clear();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // stop all the listeners
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
        if (messagesAdapter != null) {
            messagesAdapter.cleanup();
        }
    }
}
