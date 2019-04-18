package com.android121.timecapsule;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // initialize
    EditText noteText;
    Button submitNoteButton;
    CheckBox isNotePrivate;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.contribute);

        // Get references
        noteText = (EditText) findViewById(R.id.noteText);
        submitNoteButton = (Button) findViewById(R.id.submitNoteButton);
        isNotePrivate = (CheckBox) findViewById(R.id.isNotePrivate);
        db = FirebaseFirestore.getInstance();

    }

    // Toggles visibility of the note fields when the notes button is clicked
    void showNoteText(View v) {

        // Toggle visibility of the fields
        if(noteText.getVisibility() == EditText.VISIBLE){
            noteText.setVisibility(EditText.GONE);
            submitNoteButton.setVisibility(Button.GONE);
            isNotePrivate.setVisibility(CheckBox.GONE);
        } else {
            noteText.setVisibility(EditText.VISIBLE);
            submitNoteButton.setVisibility(Button.VISIBLE);
            isNotePrivate.setVisibility(CheckBox.VISIBLE);
        }
    }

    // Posts note to database and collapses fields
    void submitNote(View V){

        // Get text from editText
        String note = noteText.getText().toString();

        // Create Document to enter into database
        Map<String, Object> contribution = new HashMap<>();

        contribution.put("content", note);
        contribution.put("capsuleID", "");
        contribution.put("contributionID", "");
        contribution.put("isPublic", !isNotePrivate.isChecked());
        contribution.put("userID", "");

        // Insert document into contributions table
        db.collection("contributions")
                .add(contribution)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(MainActivity.class.getSimpleName(), "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(MainActivity.class.getSimpleName(), "Error adding document", e);
                    }
                });

        // Collapse fields
        noteText.setVisibility(EditText.GONE);
        submitNoteButton.setVisibility(Button.GONE);
        isNotePrivate.setVisibility(CheckBox.GONE);

        // Show toast message to confirm submission
        Toast noteSubmittedToast = new Toast(this);
        noteSubmittedToast.makeText(this, R.string.noteSubmitted, Toast.LENGTH_SHORT).show();

    }
}
