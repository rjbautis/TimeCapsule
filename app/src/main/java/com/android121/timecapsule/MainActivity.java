package com.android121.timecapsule;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // initialize
    EditText noteText;
    Button submitNoteButton;
    CheckBox isNotePrivate;
    FirebaseFirestore db;
    EditText searchText;
    TextView searchView;

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
        searchText = (EditText) findViewById(R.id.noteSearchBar);
        searchView = (TextView) findViewById(R.id.noteSearchView);

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

    // Find the content of a note given a contribution ID and display it
    void findNote(View v){
        String searchID = "";
        if(searchText.getText() != null && !searchText.getText().toString().equals("")){
            searchID = searchText.getText().toString();
            DocumentReference docRef = db.collection("contributions").document(searchID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(MainActivity.class.getSimpleName(), "DocumentSnapshot data: " + document.getData());
                            Log.d(MainActivity.class.getSimpleName(), "CONTENT: "+ document.get("content"));
                            searchView.setText(document.get("content").toString());
                        } else {
                            Log.d(MainActivity.class.getSimpleName(), "No such document");
                            searchView.setText(R.string.invalidID);
                        }
                    } else {
                        Log.d(MainActivity.class.getSimpleName(), "get failed with ", task.getException());
                    }
                }
            });
        } else {
            searchView.setText(R.string.invalidID);
        }


    }
}
