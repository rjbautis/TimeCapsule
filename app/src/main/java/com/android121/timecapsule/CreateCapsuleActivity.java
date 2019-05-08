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

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private EditText mCapsuleNameEditText;
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

        dateView = (TextView) findViewById(R.id.text_open_date_field);
        calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);

        mInviteFriendsButton = (Button) findViewById(R.id.button_invite_friends);
        mCreateCapsuleButton = (Button) findViewById(R.id.button_create_capsule);
        mRecipientEditText = (EditText) findViewById(R.id.edit_text_recipient);
        mCapsuleNameEditText = (EditText) findViewById(R.id.edit_text_capsule_name);
        mInviteFriendEditText = (EditText) findViewById(R.id.edit_text_invite_address);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

    }

    public void inviteFriends(View v){
        // Create Document to enter into database
        // TODO: Error check the email address
        //      Check for pre-existing invitation
        //      Make sure email address is a valid user.

        // TODO: FIX BUG WHERE EMPTY RECIPIENT CRASHES

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
        Log.d(TAG, "querying for address:"+recipientAddress);

        Query validateRecipientQuery = db.collection("users").whereEqualTo("email", recipientAddress).limit(1);

        validateRecipientQuery.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        // ...

                        // Get the last visible document
                        if(documentSnapshots.size() == 0){
                            Log.d(TAG, "Query returned 0 results");
                            return;
                        }
                        DocumentSnapshot recipientDocument = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() -1);

                        final String recipientId = recipientDocument.getId();

                        // Create Document to enter into database
                        Date currentTime = Calendar.getInstance().getTime();
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                        Date openDate = Calendar.getInstance().getTime();
                        try {
                            openDate = sdf.parse(dateView.getText().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        String capsuleName = mCapsuleNameEditText.getText().toString();

                        Capsule capsule = new Capsule(currentTime, openDate, recipientId, capsuleName);

                        // Insert document into capsules table
                        db.collection("capsules")
                                .add(capsule)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "Created capsule. DocumentSnapshot added with ID: " + documentReference.getId());

                                        // invite creator to contribute
                                        String senderId;

                                        FirebaseUser currentUser = mAuth.getCurrentUser();

                                        if (currentUser != null) {
                                            Log.d(TAG, "Firebase user authenticated already");

                                            senderId = currentUser.getUid();
                                        } else {
                                            Log.d(TAG, "User not logged in!");
                                            senderId = null;
                                        }

                                        Invitation invitation = new Invitation(documentReference.getId(), senderId, recipientId);

                                        db.collection("invitations")
                                                .add(invitation)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Log.d(TAG, "Invited creator. DocumentSnapshot added with ID: " + documentReference.getId());
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error adding document trying to invite creator", e);
                                                    }
                                                });



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
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(month).append("/")
                .append(day).append("/").append(year));
    }

}

