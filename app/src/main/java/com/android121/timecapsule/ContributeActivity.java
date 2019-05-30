package com.android121.timecapsule;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ContributeActivity extends AppCompatActivity {

    private static final String TAG = ContributeActivity.class.getSimpleName();

    private static final int RC_IMAGE_PICKER = 101;

    private String mCapsuleId;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private FirebaseUtil firebaseUtil;

    // Note
    private EditText mNoteText;
    private Button mNoteSubmitButton;
    private CheckBox mNoteIsPrivate;

    // Picture
    private TextView mPictureText;
    private ImageView mPicture;
    private Button mPictureSubmitButton;
    private Uri file;

    // Youtube Links
    private EditText mYoutubeLink;
    private Button mYoutubeSubmitButton;

    // Invite Friends
    private Button mInviteFriendButton;
    private EditText mInviteFriendsEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute);

        setTitle(R.string.contribute_contribute);

        // Get capsule id from bundle
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mCapsuleId = extras.getString("capsuleId");
            Log.d(TAG, "capsuleId received from bundle:" + mCapsuleId);
        }

        mAuth = FirebaseAuth.getInstance();
        firebaseUtil = new FirebaseUtil(this);

        db = FirebaseFirestore.getInstance();

        // Note References
        mNoteText = (EditText) findViewById(R.id.edit_text_note);
        mNoteSubmitButton = (Button) findViewById(R.id.button_submit_note);
        mNoteIsPrivate = (CheckBox) findViewById(R.id.is_note_private);

        // Picture References
        mPictureText = findViewById(R.id.choose_picture_hint_text);
        mPicture = findViewById(R.id.picture_view);
        mPictureSubmitButton = findViewById(R.id.picture_submit_btn);

        // Youtube Link References
        mYoutubeLink = (EditText) findViewById(R.id.edit_youtube_link);
        mYoutubeSubmitButton = (Button) findViewById(R.id.button_submit_youtube_link);


        mInviteFriendButton = findViewById(R.id.button_invite_friend);
        mInviteFriendsEditText = findViewById(R.id.edit_text_invite_friend);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == RC_IMAGE_PICKER && data != null) {
            file = data.getData();
            Glide.with(this).load(file).into(mPicture);
        }
    }

    // Toggles visibility of the note fields when the notes button is clicked
    public void showNoteText(View v) {

        // Toggle visibility of the fields
        if(mNoteText.getVisibility() == EditText.VISIBLE){
            mNoteText.setVisibility(EditText.GONE);
            mNoteSubmitButton.setVisibility(Button.GONE);
            mNoteIsPrivate.setVisibility(CheckBox.GONE);
        } else {
            mNoteText.setVisibility(EditText.VISIBLE);
            mNoteSubmitButton.setVisibility(Button.VISIBLE);
            mNoteIsPrivate.setVisibility(CheckBox.VISIBLE);
        }
    }

    // Toggles visibility of the youtube links fields when the youtube button is clicked
    public void showYoutubeLinks(View v) {
        // Toggle visibility of the fields
        if(mYoutubeLink.getVisibility() == EditText.VISIBLE){
            mYoutubeLink.setVisibility(EditText.GONE);
            mYoutubeSubmitButton.setVisibility(Button.GONE);
        } else {
            mYoutubeLink.setVisibility(EditText.VISIBLE);
            mYoutubeSubmitButton.setVisibility(Button.VISIBLE);
        }
    }



    public void showInviteFriends(View v) {

        // Toggle visibility of the fields
        if(mInviteFriendsEditText.getVisibility() == EditText.VISIBLE){
            mInviteFriendsEditText.setVisibility(EditText.GONE);
            mInviteFriendButton.setVisibility(Button.GONE);
        } else {
            mInviteFriendsEditText.setVisibility(EditText.VISIBLE);
            mInviteFriendButton.setVisibility(Button.VISIBLE);
        }
    }

    public void inviteFriend(View v){
        String inviteEmail = mInviteFriendsEditText.getText().toString();
        String senderId;

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Log.d(TAG, "Firebase user authenticated already");

            senderId = currentUser.getUid();
        } else {
            Log.d(TAG, "User not logged in!");
            senderId = null;
        }

        Invitation invitation = new Invitation(mCapsuleId, inviteEmail, senderId);

        // Insert document into invitations table
        db.collection("invitations")
                .add(invitation)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Inviting friend. DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document while trying to invite friend", e);
                    }
                });

        // Show toast message to confirm submission
        String inviteToastString = inviteEmail + " invited!";
        Toast noteSubmittedToast = new Toast(this);
        noteSubmittedToast.makeText(this, inviteToastString, Toast.LENGTH_SHORT).show();

        mInviteFriendsEditText.setText("");

        if(mInviteFriendsEditText.getVisibility() == EditText.VISIBLE){
            mInviteFriendsEditText.setVisibility(EditText.GONE);
            mInviteFriendButton.setVisibility(Button.GONE);
        } else {
            mInviteFriendsEditText.setVisibility(EditText.VISIBLE);
            mInviteFriendButton.setVisibility(Button.VISIBLE);
        }
    }

    // Posts note to database and collapses fields
    public void submitNote(View V){

        // Get text from editText
        String note = mNoteText.getText().toString();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String senderId;

        if(currentUser != null) {
            Log.d(TAG, "Firebase user authenticated already");

            senderId = currentUser.getUid();
        } else {
            Log.d(TAG, "User not logged in!");
            senderId = null;
        }

        // Create Document to enter into database
        Contribution contribution = new Contribution(note, mCapsuleId, !mNoteIsPrivate.isChecked(), senderId, "text");

        // Insert document into contributions table
        db.collection("contributions")
                .add(contribution)
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

        // Collapse fields
        mNoteText.setVisibility(EditText.GONE);
        mNoteSubmitButton.setVisibility(Button.GONE);
        mNoteIsPrivate.setVisibility(CheckBox.GONE);

        // Show toast message to confirm submission
        Toast noteSubmittedToast = new Toast(this);
        noteSubmittedToast.makeText(this, R.string.contribute_note_submitted, Toast.LENGTH_SHORT).show();

    }


    // Posts note to database and collapses fields
    public void submitYoutubeLink(View V){

        // Get link from editText
        String link = mYoutubeLink.getText().toString();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String senderId;

        if(currentUser != null) {
            Log.d(TAG, "Firebase user authenticated already");

            senderId = currentUser.getUid();
        } else {
            Log.d(TAG, "User not logged in!");
            senderId = null;
        }

        // Create Document to enter into database
        Contribution contribution = new Contribution(link, mCapsuleId, !mNoteIsPrivate.isChecked(), senderId, "yt_video");

        // Insert document into contributions table
        db.collection("contributions")
                .add(contribution)
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

        // Collapse fields
        mYoutubeLink.setVisibility(EditText.GONE);
        mYoutubeSubmitButton.setVisibility(Button.GONE);

        // Show toast message to confirm submission
        Toast noteSubmittedToast = new Toast(this);
        noteSubmittedToast.makeText(this, "YOUTUBE link submitted!", Toast.LENGTH_SHORT).show();

    }

    public void showPictureFields(View view) {
        if (mPicture.getVisibility() == View.GONE) {
            mPictureText.setVisibility(View.VISIBLE);
            mPicture.setVisibility(View.VISIBLE);
            mPictureSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mPictureText.setVisibility(View.GONE);
            mPicture.setVisibility(View.GONE);
            mPictureSubmitButton.setVisibility(View.GONE);
        }
    }

    public void choosePicture(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(intent, RC_IMAGE_PICKER);
    }

    public void submitPicture(View view) {
        if (file != null) {
            firebaseUtil.uploadStorage(file, mCapsuleId, new FirebaseUtil.OnStorageTaskCompleteListener() {
                @Override
                public void onSuccess(String downloadUrl) {
                    Toast.makeText(ContributeActivity.this, "Successfully uploaded picture!" ,Toast.LENGTH_LONG).show();

                    // Write picture to firebase document
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    // Create photo document to enter to database
                    Contribution contribution = new Contribution(downloadUrl, mCapsuleId, false, currentUser.getUid(), "photo");

                    // Insert photo document into contributions table
                    db.collection("contributions")
                            .add(contribution)
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

                    if (mPicture.getVisibility() == View.GONE) {
                        mPictureText.setVisibility(View.VISIBLE);
                        mPicture.setVisibility(View.VISIBLE);
                        mPictureSubmitButton.setVisibility(View.VISIBLE);
                    } else {
                        mPictureText.setVisibility(View.GONE);
                        mPicture.setVisibility(View.GONE);
                        mPictureSubmitButton.setVisibility(View.GONE);
                    }

                }
                @Override
                public void onFailure() {
                    Toast.makeText(ContributeActivity.this, "Failed to upload picture." ,Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void finishContributing(View view){
        Intent intent = new Intent(ContributeActivity.this, HomeActivity.class);
        // Finish all activities that were on the stack before going back to HomeActivity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
