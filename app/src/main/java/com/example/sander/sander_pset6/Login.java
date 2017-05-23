package com.example.sander.sander_pset6;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    String email;
    String password;

    EditText etEmail;
    EditText etPassword;
    EditText etUsername;

    /* FB stuff */
    private FirebaseAuth auth;
    private DatabaseReference dbref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // get views
        etEmail = (EditText) findViewById(R.id.loginEmail);
        etPassword = (EditText) findViewById(R.id.loginPass);
        etUsername = (EditText) findViewById(R.id.loginUsername);

        /* FB stuff */
        auth = FirebaseAuth.getInstance();
        dbref = FirebaseDatabase.getInstance().getReference();
    }

    public void createUser() {
        // create user in FB Auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d("log", "createUserWithEmail:onComplete:");

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Toast.makeText(Login.this, "authentication/creation failed",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Log.d("log", "createUserWithEmail:onComplete: Succesful");
                        // set username
                        putUserInDB(email);
                    }
                }
            });
    }

    private void putUserInDB(String email) {
        Log.d("log", "Login.putUserInDB: start");

        // gather userinfo
        String username = etUsername.getText().toString();
        String uid = auth.getCurrentUser().getUid();

        // create User object
        User user = new User(username, email);

        // write username to FB
        dbref.child("users").child(uid)
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("log", "Login.putUserInDB: completed");
                        }
                    }
                });

        // send user to chat activity
        sendToChat();
    }

    private void sendToChat() {
        Intent intentToChat = new Intent(this, Chat.class);
        startActivity(intentToChat);
        finish();
    }

    public void createOrLogin(View view) {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        // try to login
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("log", "signInWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("log", "User doesn't exist: create user", task.getException());
                            createUser();
                        }
                        else {
                            sendToChat();
                        }
                    }

                });
    }
}
