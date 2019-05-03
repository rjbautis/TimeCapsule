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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ContributeActivity extends AppCompatActivity {

    private static final String TAG = ContributeActivity.class.getSimpleName();

    private static final int RC_IMAGE_PICKER = 101;

    private String mCapsuleId;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private FirebaseUtil firebaseUtil;

    private Button mLogOut;

    // Note
    private EditText mNoteText;
    private Button mNoteSubmitButton;
    private CheckBox mNoteIsPrivate;
    private EditText mNoteSearchText;
    private TextView mNoteSearchView;

    // Picture
    private TextView mPictureText;
    private ImageView mPicture;
    private Button mPictureSubmitButton;
    private Uri file;

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
        mLogOut = (Button) findViewById(R.id.logOut);

        // Note References
        mNoteText = (EditText) findViewById(R.id.edit_text_note);
        mNoteSubmitButton = (Button) findViewById(R.id.button_submit_note);
        mNoteIsPrivate = (CheckBox) findViewById(R.id.is_note_private);
        mNoteSearchText = (EditText) findViewById(R.id.edit_text_note_search_bar);
        mNoteSearchView = (TextView) findViewById(R.id.text_note_search_view);

        // Picture References
        mPictureText = findViewById(R.id.choose_picture_hint_text);
        mPicture = findViewById(R.id.picture_view);
        mPictureSubmitButton = findViewById(R.id.test123);

        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(ContributeActivity.this, LoginActivity.class));
            }
        });

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

    // Posts note to database and collapses fields
    public void submitNote(View V){

        // Get text from editText
        String note = mNoteText.getText().toString();

        // Create Document to enter into database
        Contribution contribution = new Contribution(note, "", !mNoteIsPrivate.isChecked(), "");

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

    // Find the content of a note given a contribution ID and display it
    public void findNote(View v){
        String searchId = "";
        if(mNoteSearchText.getText() != null && !mNoteSearchText.getText().toString().equals("")){
            searchId = mNoteSearchText.getText().toString();
            DocumentReference docRef = db.collection("contributions").document(searchId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            Log.d(TAG, "CONTENT: "+ document.get("content"));
                            mNoteSearchView.setText(document.get("content").toString());
                        } else {
                            Log.d(TAG, "No such document");
                            mNoteSearchView.setText(R.string.contribute_invalid_id);
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        } else {
            mNoteSearchView.setText(R.string.contribute_invalid_id);
        }
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
            firebaseUtil.uploadStorage(file, "nice");
        }
    }
}
