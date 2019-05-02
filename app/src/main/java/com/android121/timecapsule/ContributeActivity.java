package com.android121.timecapsule;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ContributeActivity extends AppCompatActivity {

    private static final String TAG = ContributeActivity.class.getSimpleName();

    private Button mLogOut;
    private EditText mNoteText;
    private Button mSubmitNoteButton;
    private CheckBox mIsNotePrivate;
    private EditText mSearchText;
    private TextView mSearchView;
    private String mCapsuleId;

    private FirebaseFirestore db;

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

        // Get references
        mNoteText = (EditText) findViewById(R.id.edit_text_note);
        mSubmitNoteButton = (Button) findViewById(R.id.button_submit_note);
        mIsNotePrivate = (CheckBox) findViewById(R.id.is_note_private);
        db = FirebaseFirestore.getInstance();
        mSearchText = (EditText) findViewById(R.id.edit_text_note_search_bar);
        mSearchView = (TextView) findViewById(R.id.text_note_search_view);

        mLogOut = (Button) findViewById(R.id.logOut);
        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContributeActivity.this, LoginActivity.class));
            }
        });

    }

    // Toggles visibility of the note fields when the notes button is clicked
    public void showNoteText(View v) {

        // Toggle visibility of the fields
        if(mNoteText.getVisibility() == EditText.VISIBLE){
            mNoteText.setVisibility(EditText.GONE);
            mSubmitNoteButton.setVisibility(Button.GONE);
            mIsNotePrivate.setVisibility(CheckBox.GONE);
        } else {
            mNoteText.setVisibility(EditText.VISIBLE);
            mSubmitNoteButton.setVisibility(Button.VISIBLE);
            mIsNotePrivate.setVisibility(CheckBox.VISIBLE);
        }
    }

    // Posts note to database and collapses fields
    public void submitNote(View V){

        // Get text from editText
        String note = mNoteText.getText().toString();

        // Create Document to enter into database
        Contribution contribution = new Contribution(note, "", !mIsNotePrivate.isChecked(), "");

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
        mSubmitNoteButton.setVisibility(Button.GONE);
        mIsNotePrivate.setVisibility(CheckBox.GONE);

        // Show toast message to confirm submission
        Toast noteSubmittedToast = new Toast(this);
        noteSubmittedToast.makeText(this, R.string.contribute_note_submitted, Toast.LENGTH_SHORT).show();

    }

    // Find the content of a note given a contribution ID and display it
    public void findNote(View v){
        String searchId = "";
        if(mSearchText.getText() != null && !mSearchText.getText().toString().equals("")){
            searchId = mSearchText.getText().toString();
            DocumentReference docRef = db.collection("contributions").document(searchId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            Log.d(TAG, "CONTENT: "+ document.get("content"));
                            mSearchView.setText(document.get("content").toString());
                        } else {
                            Log.d(TAG, "No such document");
                            mSearchView.setText(R.string.contribute_invalid_id);
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        } else {
            mSearchView.setText(R.string.contribute_invalid_id);
        }


    }
}
