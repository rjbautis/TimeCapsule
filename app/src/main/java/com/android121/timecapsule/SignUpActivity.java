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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText mNameEditText;
    private EditText mNewEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase auth object and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mNameEditText = findViewById(R.id.nameEditText);
        mNewEmailEditText = findViewById(R.id.newEmailEditText);
        mPasswordEditText = findViewById(R.id.newPasswordEditText);
        mConfirmPasswordEditText = findViewById(R.id.confirmPasswordField);
        mProgressBar = findViewById(R.id.progressBar);

        // Add focus change event listener for validation
        mNameEditText.setOnFocusChangeListener(this);
        mNewEmailEditText.setOnFocusChangeListener(this);
        mPasswordEditText.setOnFocusChangeListener(this);
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
            case R.id.newPasswordEditText:
                if (!hasFocus) {
                    if (password.length() < 6) {
                        mPasswordEditText.setError("Password is too weak. Pick a longer password");
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

        final String name = mNameEditText.getText().toString();
        final String email = mNewEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        mProgressBar.setVisibility(View.VISIBLE);

        // Add user to the Firebase Authentication table
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmailAndPassword: success");

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            if (firebaseUser != null) {
                                User user = new User(name, email);

                                // Add or replace user in the users collection in the database
                                db.collection("users").document(firebaseUser.getUid())
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "User successfully added to database!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "Error writing document", e);
                                            }
                                        });
                            }

                            Toast.makeText(SignUpActivity.this, "Successfully signed up!",
                                    Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(SignUpActivity.this, ContributeActivity.class);
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
}
