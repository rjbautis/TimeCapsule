package com.android121.timecapsule;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    // Request code (unique integer) that identifies request for google sign in
    private static final int RC_GOOGLE_SIGN_IN = 100;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private ProgressBar mProgressBar;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase auth object and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mEmailEditText = findViewById(R.id.emailEditText);
        mPasswordEditText = findViewById(R.id.passwordEditText);
        mProgressBar = findViewById(R.id.progressBar);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                            .requestIdToken(getString(R.string.default_web_client_id))
                                                            .requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Users that are already logged in previously do not need to login again
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Log.d(TAG, "Firebase user authenticated already");

            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching Google Sign In Intent
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                Log.d(TAG, "Google sign in successful. Trying to firebaseGoogleAuth with Firebase");

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseGoogleAuth(account);
            } catch (ApiException e) {
                Log.d(TAG, "Google sign in failed.");

                mProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, "Sign in attempt with Google failed.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void onClick(View view) {
        Log.d(TAG, "Clicked button");
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        // Determine which function to call based on the button clicked
        switch (view.getId()) {
            case R.id.loginBtn:

                if (!isValidEmail(email)) {
                    mEmailEditText.setError("Please enter a valid email address.");
                    return;
                }

                mProgressBar.setVisibility(View.VISIBLE);

                login(email, password);
                break;
            case R.id.signUpBtn:
                // Launch the sign up activity
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.googleSignInBtn:
                mProgressBar.setVisibility(View.VISIBLE);

                signInWithGoogle();
                break;
            default:
                Log.e(TAG, "Fatal Error. Shouldn't be able to reach this");
                break;
        }
    }

    /**
     * Login by authenticating with Firebase Authentication table
     */
    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmailAndPassword: success");

                            Toast.makeText(LoginActivity.this, "Successfully logged in!",
                                    Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d(TAG, "signInWithEmailAndPassword: failed");

                            mProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Authentication failed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        // Start Google's Sign In activity and wait for a result
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    /**
     * Authenticate the GoogleSignInAccount object in the Firebase Authentication table
     */
    private void firebaseGoogleAuth(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential: success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            if (firebaseUser != null) {
                                User user = new User(
                                        firebaseUser.getDisplayName(), firebaseUser.getEmail(), firebaseUser.getUid());

                                // Add or replace user in the users collection in the database
                                db.collection("users").document(user.userId)
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

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d(TAG, "signInWithCredential: failure", task.getException());

                            mProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Sign in attempt with Google failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
