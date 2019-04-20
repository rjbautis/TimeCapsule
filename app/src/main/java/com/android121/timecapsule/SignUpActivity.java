package com.android121.timecapsule;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    private FirebaseAuth mAuth;

    private EditText mNameEditText;
    private EditText mNewEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase auth object
        mAuth = FirebaseAuth.getInstance();

        mNameEditText = findViewById(R.id.nameEditText);
        mNewEmailEditText = findViewById(R.id.newEmailEditText);
        mPasswordEditText = findViewById(R.id.newPasswordEditText);
        mConfirmPasswordEditText = findViewById(R.id.confirmPasswordField);
        mProgressBar = findViewById(R.id.progressBar);

        // Add focus change event listener for validation
        mNameEditText.setOnFocusChangeListener(this);
        mNewEmailEditText.setOnFocusChangeListener(this);
        mConfirmPasswordEditText.setOnFocusChangeListener(this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        String name = mNameEditText.getText().toString();
        String email = mNewEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String confirmPassword = mConfirmPasswordEditText.getText().toString();

        // Validate the edit text fields every time the focus is moved away from those fields
        switch (v.getId()) {
            case R.id.nameEditText:
                if (!hasFocus) {
                    if (name.equals("")) {
                        mNameEditText.setError("Your name is required.");
                    }
                }
                break;

            case R.id.newEmailEditText:
                if (!hasFocus) {
                    if (!isValidEmail(email)) {
                        mNewEmailEditText.setError("Please enter a valid email address.");
                    }
                }
                break;

            case R.id.confirmPasswordField:
                if (!hasFocus) {
                    if (password.equals("") || confirmPassword.equals("") || !password.equals(confirmPassword)) {
                        mConfirmPasswordEditText.setError("Passwords do not match.");
                    }
                }
                break;

            default:
                Log.e(TAG, "Fatal Error. Shouldn't be able to reach this.");
                break;
        }
    }

    private boolean isValidFields() {
        String name = mNameEditText.getText().toString();
        String email = mNewEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String confirmPassword = mConfirmPasswordEditText.getText().toString();

        boolean validName = mNameEditText.getError() == null && !name.equals("");
        boolean validEmail = mNewEmailEditText.getError() == null && !email.equals("");
        boolean validPassword = !password.equals("") && !confirmPassword.equals("") && password.equals(confirmPassword);

        return validName && validEmail && validPassword;
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Sign up by authenticating with Firebase Authentication table
     */
    public void onSignUp(View view) {

        if (!isValidFields()) {
            Log.d(TAG, "EditText fields are not valid.");

            Toast.makeText(SignUpActivity.this, "Please verify that all fields are correct or non-empty.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String email = mNewEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        mProgressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmailAndPassword: success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(SignUpActivity.this, "Successfully signed up!",
                                    Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d(TAG, "createUserWithEmailAndPassword: failed");

                            mProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(SignUpActivity.this, "Sign up failed. This email might have " +
                                    "an existing account signed up already.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
