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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    String email;
    String password;

    EditText etEmail;
    EditText etPass;
    EditText etUsername;

    /* FB stuff */
    private FirebaseAuth auth;
    private DatabaseReference ref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // get views
        etEmail = (EditText) findViewById(R.id.loginEmail);
        etPass = (EditText) findViewById(R.id.loginPass);
        etUsername = (EditText) findViewById(R.id.loginUsername);

        /* FB stuff */
        auth = FirebaseAuth.getInstance();
    }

    public void createUser() {
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
                        // set username
                        setUsername();
                        sendToChat();
                    }
                    // send user to chat actvitiy
                }
            });
    }

    private void setUsername() {
        // get uid and username
        String uid = auth.getCurrentUser().getUid();
        String username = etUsername.getText().toString();

        // update username
        ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(uid).child("username").setValue(username);
    }

    private void sendToChat() {
        Intent intentToChat = new Intent(this, Chat.class);
        startActivity(intentToChat);
        finish();
    }

    public void createOrLogin(View view) {
        email = etEmail.getText().toString();
        password = etPass.getText().toString();

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
                            setUsername();
                        }
                    }
                });
    }
}
