package com.android121.timecapsule;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    // Request code (unique integer) that identifies request for google sign in
    private static final int RC_GOOGLE_SIGN_IN = 100;

    private FirebaseAuth mAuth;

    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase auth object
        mAuth = FirebaseAuth.getInstance();

        mEmailEditText = findViewById(R.id.email_field);
        mPasswordEditText = findViewById(R.id.password_field);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                            .requestIdToken(getString(R.string.default_web_client_id))
                                                            .requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        // FirebaseUser currentUser = mAuth.getCurrentUser();
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
            }
        }
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void onClick(View view) {
        Log.d(TAG, "Clicked Login button");
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        if (!isValidEmail(email) && view.getId() != R.id.google_sign_in_btn) {
            mEmailEditText.setError("Please enter a valid email address.");
            return;
        }

        switch (view.getId()) {
            case R.id.login_btn:
                login(email, password);
                break;
            case R.id.sign_up_btn:
                signUp(email, password);
                break;
            case R.id.google_sign_in_btn:
                signInWithGoogle();
                break;
            default:
                Log.e(LoginActivity.class.getSimpleName(), "Fatal Error. Shouldn't be able to reach this");
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
                            Log.d(LoginActivity.class.getSimpleName(), "signInWithEmailAndPassword: success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Successfully logged in!",
                                    Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {

                            Log.d(LoginActivity.class.getSimpleName(), "signInwithEmailAndPassword: failed");
                            Toast.makeText(LoginActivity.this, "Authentication failed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Sign up by authenticating with Firebase Authentication table
     */
    private void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(LoginActivity.class.getSimpleName(), "createUserWithEmailAndPassword: success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Successfully signed up!",
                                    Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {

                            Log.d(LoginActivity.class.getSimpleName(), "createUserWithEmailAndPassword: failed");
                            Toast.makeText(LoginActivity.this, "Sign up failed. This email might have " +
                                    "an existing account signed up already.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
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
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {

                            Log.d(TAG, "signInWithCredential: failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Sign in attempt with Google failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
