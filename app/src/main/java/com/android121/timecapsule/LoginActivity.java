package com.android121.timecapsule;

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

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mEmailEditText = findViewById(R.id.email_field);
        mPasswordEditText = findViewById(R.id.password_field);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        // FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void onLogin(View view) {
        Log.d(LoginActivity.class.getSimpleName(), "Clicked Login button");
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        if (!isValidEmail(email)) {
            mEmailEditText.setError("Please enter a valid email address.");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(LoginActivity.class.getSimpleName(), "signInWithEmailAndPassword: success");

                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(LoginActivity.this, "Logged in!", Toast.LENGTH_LONG).show();
                    } else {

                        Log.d(LoginActivity.class.getSimpleName(), "signInwithEmailAndPassword: failed");
                        Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    public void onSignUp(View view) {
        Log.d(LoginActivity.class.getSimpleName(), "Clicked Sign Up button");
    }

    public void onGoogleSignIn(View view) {
        Log.d(LoginActivity.class.getSimpleName(), "Clicked Sign In With Google button");
    }
}
