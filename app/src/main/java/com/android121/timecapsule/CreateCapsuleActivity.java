package com.android121.timecapsule;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateCapsuleActivity extends AppCompatActivity {

    private static final String TAG = CreateCapsuleActivity.class.getSimpleName();

    Button mInviteFriendsButton;
    Button mCreateCapsuleButton;
    EditText mRecipientEditText;
    EditText mOpenDateEditText;
    EditText mInviteFriendEditText;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_capsule);

        setTitle(R.string.create_create);

        mInviteFriendsButton = (Button) findViewById(R.id.button_invite_friends);
        mCreateCapsuleButton = (Button) findViewById(R.id.button_create_capsule);
        mRecipientEditText = (EditText) findViewById(R.id.edit_text_recipient);
        mOpenDateEditText = (EditText) findViewById(R.id.edit_text_open_date);
        mInviteFriendEditText = (EditText) findViewById(R.id.edit_text_invite_address);

        db = FirebaseFirestore.getInstance();

    }

    public void inviteFriends(View v){
        // Create Document to enter into database

        String userId = mInviteFriendEditText.getText().toString();

        Invitation invitation = new Invitation(null, userId);

        // Insert document into contributions table
        db.collection("invitations")
                .add(invitation)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        // Show toast message to confirm submission
        String inviteToastString = userId + " invited!";
        Toast noteSubmittedToast = new Toast(this);
        noteSubmittedToast.makeText(this, inviteToastString, Toast.LENGTH_SHORT).show();
    }

    public void createCapsule(View v){
        // Go to contribute activity? Pass contribution ID
        // Get text from editText

        // Create Document to enter into database
        Capsule capsule = new Capsule(null, null, null, null);

        // Insert document into contributions table
        db.collection("capsules")
                .add(capsule)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });


        // Create intent to go to contribute page
        Intent intent = new Intent(CreateCapsuleActivity.this, MainActivity.class);


    }

}

