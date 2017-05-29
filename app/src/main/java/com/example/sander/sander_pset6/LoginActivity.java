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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Defines the LoginActivity. This activity helps the user login or create an account.
 * N.B.: DB is short for database.
 */
public class LoginActivity extends AppCompatActivity {

    private static final int MIN_PASS_LENGTH = 6;

    private String email;
    private String password;

    private EditText etEmail;
    private EditText etPassword;
    private EditText etUsername;

    private FirebaseAuth auth;
    private DatabaseReference dbref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText) findViewById(R.id.loginEmail);
        etPassword = (EditText) findViewById(R.id.loginPass);
        etUsername = (EditText) findViewById(R.id.loginUsername);

        auth = FirebaseAuth.getInstance();
        dbref = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Tries to login or create user when Create/Login button is clicked.
     */
    public void createOrLogin(View view) {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        // try to login
        try {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                // user doesn't exist
                                createUser();
                            } else {
                                // user exists
                                sendToChat();
                            }
                        }
                    });
        } catch (IllegalArgumentException e) {
            invalidInput(email, password);
        }
    }


    /**
     * Creates the user when login credentials don't exist yet.
     */
    public void createUser() {
        OnCompleteListener listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // if account creation failed, inform user
                if (!task.isSuccessful()) {
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.creation_failed,
                            Toast.LENGTH_SHORT).show();
                } else {
                    putUserInDb(auth.getCurrentUser());
                }
            }
        };

        // create user in FB Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, listener);
    }

    /**
     * Puts user also in DB.
     * This circumvents bug with FirebaseUser.setDisplayname which still seems to exist: https://github.com/firebase/FirebaseUI-Android/issues/409
     * It is also needed to comply with Maxim's implementation standards.
     */
    private void putUserInDb(FirebaseUser FBuser) {
        String username = etUsername.getText().toString();

        // define default username
        if (username.length() == 0) {
            username = getResources().getString(R.string.default_username);
        }

        User user = new User();
        user.setUsername(username);
        user.setUid(FBuser.getUid());
        user.setEmail(FBuser.getEmail());

        OnCompleteListener listener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // inform user if account creation failed
                if (!task.isSuccessful()) {
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.db_error,
                            Toast.LENGTH_SHORT)
                            .show();
                } else { sendToChat(); }
            }
        };

        // write user to DB and add listener
        dbref.child("users").child(user.getUid())
                .setValue(user)
                .addOnCompleteListener(listener);
    }

    /**
     * Sends user to ChatActivity
     */
    private void sendToChat() {
        Intent intentToChat = new Intent(this, ChatActivity.class);
        startActivity(intentToChat);
        finish();
    }


    /**
     * Informs user about what is wrong with their input
     */
    private void invalidInput(String email, String password) {
        if (!isValidEmail(email)){
            Toast.makeText(getApplicationContext(), R.string.invalid_email, Toast.LENGTH_SHORT).show();
        }
        else if (!isValidPassword(password)){
            Toast.makeText(getApplicationContext(), R.string.invalid_password, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), R.string.unknown_error, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Validates password input
     */
    private boolean isValidPassword(String password) {
        return password != null && password.length() > MIN_PASS_LENGTH;
    }

    /**
     * Validates email input.
     * Based on snippet found at: https://stackoverflow.com/a/15808057
     */
    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
