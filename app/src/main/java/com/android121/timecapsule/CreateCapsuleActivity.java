package com.android121.timecapsule;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;

public class CreateCapsuleActivity extends AppCompatActivity {

    private static final String TAG = CreateCapsuleActivity.class.getSimpleName();

    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;

    private Button mInviteFriendsButton;
    private Button mCreateCapsuleButton;
    private EditText mRecipientEditText;
    private EditText mOpenDateEditText;
    private EditText mInviteFriendEditText;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_capsule);

        setTitle(R.string.create_create);

        //dateView = (TextView) findViewById(R.id.textView3);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        //showDate(year, month+1, day);

        mInviteFriendsButton = (Button) findViewById(R.id.button_invite_friends);
        mCreateCapsuleButton = (Button) findViewById(R.id.button_create_capsule);
        mRecipientEditText = (EditText) findViewById(R.id.edit_text_recipient);
        mOpenDateEditText = (EditText) findViewById(R.id.edit_text_open_date);
        mInviteFriendEditText = (EditText) findViewById(R.id.edit_text_invite_address);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

    }

    public void inviteFriends(View v){
        // Create Document to enter into database
        // TODO: Error check the email address
        //      Check for pre-existing invitation
        //      Make sure email address is a valid user.

        String userId = mInviteFriendEditText.getText().toString();
        String senderId;

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Log.d(TAG, "Firebase user authenticated already");

            senderId = currentUser.getUid();
        } else {
            Log.d(TAG, "User not logged in!");
            senderId = null;
        }

        Invitation invitation = new Invitation(null, userId, senderId);

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

        // TODO: Clear edit text field
        mInviteFriendEditText.setText("");
    }

    public void createCapsule(View v){
        // Go to contribute activity? Pass contribution ID
        // Get text from editText

        // TODO: check recipient address
        String recipientAddress = mRecipientEditText.getText().toString();

        Query validateRecipientQuery = db.collection("users").whereEqualTo("email", recipientAddress).limit(1);

        validateRecipientQuery.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        // ...

                        // Get the last visible document
                        DocumentSnapshot recipientDocument = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() -1);

                        String recipientId = recipientDocument.getString("userId");

                        // Create Document to enter into database
                        // TODO: get IDs and date created.
                        Date currentTime = Calendar.getInstance().getTime();

                        Capsule capsule = new Capsule(currentTime, mOpenDateEditText.getText().toString(), recipientId);

                        // Insert document into contributions table
                        db.collection("capsules")
                                .add(capsule)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());


                                        // TODO: Finish this intent to change screens
                                        //      Pass capsule ID
                                        // Create intent to go to contribute page
                                        Intent intent = new Intent(CreateCapsuleActivity.this, ContributeActivity.class);
                                        intent.putExtra("capsuleId", documentReference.getId());
                                        startActivity(intent);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });




                    }
                });



    }


    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    //showDate(arg1, arg2+1, arg3);
                }
            };
/*
    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }*/

}

