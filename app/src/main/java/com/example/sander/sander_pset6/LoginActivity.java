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
 *
 * N.B.: DB is short for database.
 */
public class LoginActivity extends AppCompatActivity {

    // minimum password length
    private static final int MIN_PASS_LENGTH = 6;

    // user strings
    private String email;
    private String password;

    // views
    private EditText etEmail;
    private EditText etPassword;
    private EditText etUsername;

    // FB related stuff
    private FirebaseAuth auth;
    private DatabaseReference dbref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // get views
        etEmail = (EditText) findViewById(R.id.loginEmail);
        etPassword = (EditText) findViewById(R.id.loginPass);
        etUsername = (EditText) findViewById(R.id.loginUsername);

        // get Firebase instances
        auth = FirebaseAuth.getInstance();
        dbref = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Creates the user when login credentials don't exist yet.
     */
    public void createUser() {
        // define onCompleteListener listener
        OnCompleteListener listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("log", "createUserWithEmail:onComplete:");

                // if account creation failed, inform user
                if (!task.isSuccessful()) {
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.creation_failed,
                            Toast.LENGTH_SHORT).show();
                }

                // otherwise, set username in DB
                else {
                    Log.d("log", "createUserWithEmail:onComplete: Succesful");
                    putUserInDB(auth.getCurrentUser());
                }
            }
        };

        // create user in FB Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, listener);
    }

    /**
     * Puts user also in DB.
     * This circumvents bug with FirebaseUser.setDisplayname. It is also needed to comply with Maxim's
     * implementation standards
     */
    private void putUserInDB(FirebaseUser FBuser) {
        Log.d("log", "LoginActivity.putUserInDB: start");

        // collect username from view
        String username = etUsername.getText().toString();

        // define default username
        if (username.length() == 0) {
            username = getResources().getString(R.string.default_username);
        }

        // create User object
        User user = new User();
        user.setUsername(username);
        user.setUid(FBuser.getUid());
        user.setEmail(FBuser.getEmail());

        // define onCompleteListener listener
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
                }

                // send user to chat activity
                else {
                    Log.d("log", "LoginActivity.putUserInDB: completed");
                    sendToChat();
                }
            }
        };

        // write user to DB and add listener
        dbref.child("users").child(user.getUid())
                // actually put user object in DB
                .setValue(user)

                // listen for completion of this task
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
     * Tries to login or create user when Create/Login button is clicked
     */
    public void createOrLogin(View view) {
        // get text from view
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        // try to login
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("log", "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                                Log.d("log", "User doesn't exist: create user");
                                createUser();
                        } else { sendToChat(); }
                    }
                });
        } catch (IllegalArgumentException e) {
            Log.w("log", "invalid input", e);
            invalidInput(email, password);
        }
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
     *
     * Based on snippit found at: https://stackoverflow.com/a/15808057
     */
    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
