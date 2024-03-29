package com.android121.timecapsule;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.VideoView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

public class ContributeActivity extends AppCompatActivity {

    private class HttpDownloadTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... strings) {
            try {
                // Establish connection
                URL url = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // Set timeout for connection
                conn.setConnectTimeout(1000);
                conn.setRequestMethod("GET");

                conn.connect();

                // Read response as string
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String input;
                StringBuilder response = new StringBuilder();

                while ((input = bufferedReader.readLine()) != null) {
                    response.append(input);
                }

                bufferedReader.close();

                String[] args = new String[2];
                args[0] = strings[1];
                args[1] = response.toString();

                return args;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);

            if (s == null || s[1] == null) {
                Toast.makeText(ContributeActivity.this, "There was a problem with this link", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(s[1]);

                // Spotify-specific tasks
                String imgUrl = jsonObject.getString("thumbnail_url");
                String title = jsonObject.getString("title");

                // Add delimiter between url, imgUrl and title for content
                String content = s[0] + "|" + imgUrl + "|" + title;

                FirebaseUser currentUser = mAuth.getCurrentUser();

                Contribution contribution = new Contribution(content, mCapsuleId, false, currentUser.getUid(), "spotify", uName);

                // Insert video document into contributions table
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

                toggleSpotifyFields();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static final int PAYPAL_REQUEST_CODE = 7171;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(paypal_config.PAYPAL_CLIENT_ID);

    private static final String TAG = ContributeActivity.class.getSimpleName();

    private static final int RC_IMAGE_PICKER = 101;
    private static final int RC_VIDEO_PICKER = 102;
    static  String uName = "";
    static String recipientEmail;

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

    // Video
    private TextView mVideoText;
    private VideoView mVideo;
    private Button mSelectVideoBtn;
    private Button mVideoSubmitBtn;

    // Youtube Links
    private EditText mYoutubeLink;
    private Button mYoutubeSubmitButton;

    // Spotify Links
    private EditText mSpotifyLink;
    private Button mSpotifySubmitButton;

    // Paypal Links
    private EditText mPaypalAmount;
    private Button mPaypalSubmitButton;
    static String amount;

    // Invite Friends
    private Button mInviteFriendButton;
    private EditText mInviteFriendsEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute);


        // Get capsule id from bundle
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mCapsuleId = extras.getString("capsuleId");
            Log.d(TAG, "capsuleId received from bundle:" + mCapsuleId);
        }

        mAuth = FirebaseAuth.getInstance();
        firebaseUtil = new FirebaseUtil(this);

        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String senderId;

        if(currentUser != null) {
            Log.d(TAG, "Firebase user authenticated already");

            senderId = currentUser.getUid();
        } else {
            Log.d(TAG, "User not logged in!");
            senderId = "";
        }

        DocumentReference docRef = db.collection("capsules").document(mCapsuleId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        setTitle(document.get("capsuleName").toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        DocumentReference userDocRef = db.collection("users").document(senderId);
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        uName = document.get("name").toString();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        // Note References
        mNoteText = (EditText) findViewById(R.id.edit_text_note);
        mNoteSubmitButton = (Button) findViewById(R.id.button_submit_note);
        mNoteIsPrivate = (CheckBox) findViewById(R.id.is_note_private);

        // Picture References
        mPictureText = findViewById(R.id.choose_picture_hint_text);
        mPicture = findViewById(R.id.picture_view);
        mPictureSubmitButton = findViewById(R.id.picture_submit_btn);

        // Video references
        mVideoText = findViewById(R.id.choose_video_hint_text);
        mVideo = findViewById(R.id.video_view);
        mSelectVideoBtn = findViewById(R.id.select_video_btn);
        mVideoSubmitBtn = findViewById(R.id.video_submit_btn);

        // Youtube Link References
        mYoutubeLink = (EditText) findViewById(R.id.edit_youtube_link);
        mYoutubeSubmitButton = (Button) findViewById(R.id.button_submit_youtube_link);

        // Spotify Link References
        mSpotifyLink = findViewById(R.id.edit_spotify_link);
        mSpotifySubmitButton = findViewById(R.id.button_submit_spotify_link);

        // Paypal Amount References
        mPaypalAmount = findViewById(R.id.edit_paypal_amount);
        mPaypalSubmitButton = findViewById(R.id.button_submit_paypal_amount);
        amount = "";
        recipientEmail = "";

        mInviteFriendButton = findViewById(R.id.button_invite_friend);
        mInviteFriendsEditText = findViewById(R.id.edit_text_invite_friend);

        // start PayPal Service
        Intent intent = new Intent (this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == RC_IMAGE_PICKER && data != null) {
            file = data.getData();
            Glide.with(this).load(file).into(mPicture);
        }
        // If video picked
        if (resultCode == RESULT_OK && requestCode == RC_VIDEO_PICKER && data != null) {
            file = data.getData();

            mVideo.setVideoURI(file);

            MediaController mediaController = new MediaController(this);
            mVideo.setMediaController(mediaController);
            mediaController.setAnchorView(mVideo);
        }
        if(requestCode == PAYPAL_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject((paymentDetails));
                        String transactionID = jsonObject.getJSONObject("response").getString("id");
                        String paymentToastString = "Payment succeeded! Transaction ID: " + transactionID;
//                        startActivity(new Intent(this, PaymentDetails.class)
//                                .putExtra("PaymentDetails", paymentDetails)
//                                .putExtra("PaymentAmount", amount)
//                        );

                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        String senderId;

                        if(currentUser != null) {
                            Log.d(TAG, "Firebase user authenticated already");

                            senderId = currentUser.getUid();
                        } else {
                            Log.d(TAG, "User not logged in!");
                            senderId = null;
                        }

                        // Get PayPal Amount from editText
                        String amount = (mPaypalAmount.getText().toString()) + "|" + transactionID;

                        Contribution contribution = new Contribution(amount, mCapsuleId, !mNoteIsPrivate.isChecked(), senderId, "paypal_amount", uName);

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
                        mPaypalAmount.setVisibility(EditText.GONE);
                        mPaypalSubmitButton.setVisibility(Button.GONE);



                        Toast.makeText(this, paymentToastString, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
    }

    public void processPayment(View v) {
        amount = mPaypalAmount.getText().toString();
        String useremail;


        DocumentReference docRef = db.collection("capsules").document(mCapsuleId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        DocumentReference docRef = db.collection("users").document(document.getString("recipientId"));
                        docRef.get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        Log.d(TAG, "Query returned : ");
                                        DocumentSnapshot userDocument = task.getResult();
                                        recipientEmail = userDocument.getString("email");


                                        String donateString = "Donate to " + recipientEmail;
                                        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "USD",
                                                // NOTE: REPLACE TIMECAPSULE@test.com with the recipient email

                                                donateString, PayPalPayment.PAYMENT_INTENT_SALE);
                                        Intent intent = new Intent(ContributeActivity.this, PaymentActivity.class);
                                        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                                        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                                        startActivityForResult(intent,PAYPAL_REQUEST_CODE);
                                    }

                                });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });





    }

    // Toggles visibility of the note fields when the notes button is clicked
    public void showNoteText(View v) {

        // Toggle visibility of the fields
        if(mNoteText.getVisibility() == EditText.VISIBLE){
            mNoteText.setVisibility(EditText.GONE);
            mNoteSubmitButton.setVisibility(Button.GONE);
//            mNoteIsPrivate.setVisibility(CheckBox.GONE);
        } else {
            mNoteText.setVisibility(EditText.VISIBLE);
            mNoteSubmitButton.setVisibility(Button.VISIBLE);
//            mNoteIsPrivate.setVisibility(CheckBox.VISIBLE);
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

    public void showSpotifyLinks(View view) {
        toggleSpotifyFields();
    }
    private void toggleSpotifyFields() {
        if (mSpotifyLink.getVisibility() == View.VISIBLE) {
            mSpotifyLink.setVisibility(View.GONE);
            mSpotifySubmitButton.setVisibility(View.GONE);
        } else {
            mSpotifyLink.setVisibility(View.VISIBLE);
            mSpotifySubmitButton.setVisibility(View.VISIBLE);
        }
    }

    public void showPayPalAmount(View view) {
        togglePayPalFields();
    }
    private void togglePayPalFields() {
        if (mPaypalAmount.getVisibility() == View.VISIBLE) {
            mPaypalAmount.setVisibility(View.GONE);
            mPaypalSubmitButton.setVisibility(View.GONE);
        } else {
            mPaypalAmount.setVisibility(View.VISIBLE);
            mPaypalSubmitButton.setVisibility(View.VISIBLE);
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
    public void submitNote(View V, String userName){

        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String senderId;

        if(currentUser != null) {
            Log.d(TAG, "Firebase user authenticated already");

            senderId = currentUser.getUid();
        } else {
            Log.d(TAG, "User not logged in!");
            senderId = "";
        }


        // Get text from editText
        String note = mNoteText.getText().toString();


        // Create Document to enter into database
        Contribution contribution = new Contribution(note, mCapsuleId, !mNoteIsPrivate.isChecked(), senderId, "text", userName);

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

    public void showVideoFields(View view) {
        toggleVideoFields();
    }

    private void toggleVideoFields() {
        if (mVideo.getVisibility() == View.GONE) {
            mVideoText.setVisibility(View.VISIBLE);
            mVideo.setVisibility(View.VISIBLE);
            mSelectVideoBtn.setVisibility(View.VISIBLE);
            mVideoSubmitBtn.setVisibility(View.VISIBLE);
        } else {
            mVideoText.setVisibility(View.GONE);
            mVideo.setVisibility(View.GONE);
            mSelectVideoBtn.setVisibility(View.GONE);
            mVideoSubmitBtn.setVisibility(View.GONE);
        }
    }

    public void chooseVideo(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");

        startActivityForResult(intent, RC_VIDEO_PICKER);
    }

    public void submitVideo(View view) {
        if (file != null) {
            firebaseUtil.uploadStorage(file, mCapsuleId, new FirebaseUtil.OnStorageTaskCompleteListener() {
                @Override
                public void onSuccess(String downloadUrl) {
                    Toast.makeText(ContributeActivity.this, "Successfully uploaded video!" ,Toast.LENGTH_LONG).show();

                    // Write video to firebase document
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    // Create video document to enter to database
                    Contribution contribution = new Contribution(downloadUrl, mCapsuleId, false, currentUser.getUid(), "video", "");

                    // Insert video document into contributions table
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
                    toggleVideoFields();

                }
                @Override
                public void onFailure() {
                    Toast.makeText(ContributeActivity.this, "Failed to upload video." ,Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void preSubmitYoutubeLink(View view){

        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String senderId;

        if(currentUser != null) {
            Log.d(TAG, "Firebase user authenticated already");

            senderId = currentUser.getUid();
        } else {
            Log.d(TAG, "User not logged in!");
            senderId = "";
        }

        final View view2 = view;

        DocumentReference docRef = db.collection("users").document(senderId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        String userName = document.get("name").toString();
                        submitYoutubeLink(view2, userName);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    // Posts note to database and collapses fields
    public void submitYoutubeLink(View V, String userName){

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
        // TODO: PASS NAME in preSubmitYoutubeLink
        Contribution contribution = new Contribution(link, mCapsuleId, !mNoteIsPrivate.isChecked(), senderId, "yt_video", userName);

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

    public void submitSpotifyLink(View view) {
        if (mSpotifyLink.getText().length() == 0) {
            Toast.makeText(this, "Enter a valid URL", Toast.LENGTH_LONG).show();
            return;
        }

        // Get Spotify link from editText
        String link = mSpotifyLink.getText().toString();

        // Extract track ID from link by splitting on ?
        String newLink = link.split("\\?")[0];
        String[] tmp = newLink.split("/");
        String id = tmp[tmp.length - 1];

        // Execute async task in background thread
        HttpDownloadTask downloadTask = new HttpDownloadTask();
        downloadTask.execute("https://embed.spotify.com/oembed?url=spotify:track:" + id, link);
    }

    public void showPayPalFields(View view) {
        if (mPaypalAmount.getVisibility() == View.GONE) {
            mPaypalAmount.setVisibility(View.VISIBLE);
            mPaypalSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mPaypalAmount.setVisibility(View.GONE);
            mPaypalSubmitButton.setVisibility(View.GONE);
        }
    }

    public void submitPayPalAmount(View view) {
        //if the user tries to enter a negative money value
        if (Integer.parseInt(mPaypalAmount.getText().toString()) <= 0) {
            Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_LONG).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String senderId;

        if(currentUser != null) {
            Log.d(TAG, "Firebase user authenticated already");

            senderId = currentUser.getUid();
        } else {
            Log.d(TAG, "User not logged in!");
            senderId = null;
        }

        // Get PayPal Amount from editText
        String amount = (mPaypalAmount.getText().toString());

        Contribution contribution = new Contribution(amount, mCapsuleId, !mNoteIsPrivate.isChecked(), senderId, "paypal_amount", uName);

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
        mPaypalAmount.setVisibility(EditText.GONE);
        mPaypalSubmitButton.setVisibility(Button.GONE);

        // Show toast message to confirm submission
        Toast noteSubmittedToast = new Toast(this);
        noteSubmittedToast.makeText(this, "Money from PayPal sent successfully!", Toast.LENGTH_SHORT).show();

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

    public void preSubmitNote(View view){

        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String senderId;

        if(currentUser != null) {
            Log.d(TAG, "Firebase user authenticated already");

            senderId = currentUser.getUid();
        } else {
            Log.d(TAG, "User not logged in!");
            senderId = "";
        }

        final View view2 = view;

        DocumentReference docRef = db.collection("users").document(senderId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        String userName = document.get("name").toString();
                        submitNote(view2, userName);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    public void preSubmitPicture(View view){

        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String senderId;

        if(currentUser != null) {
            Log.d(TAG, "Firebase user authenticated already");

            senderId = currentUser.getUid();
        } else {
            Log.d(TAG, "User not logged in!");
            senderId = "";
        }

        final View view2 = view;

        DocumentReference docRef = db.collection("users").document(senderId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        String userName = document.get("name").toString();
                        submitPicture(view2, userName);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    public void submitPicture(View view, String userName) {

        final String uName = userName;

        if (file != null) {
            firebaseUtil.uploadStorage(file, mCapsuleId, new FirebaseUtil.OnStorageTaskCompleteListener() {
                @Override
                public void onSuccess(String downloadUrl) {
                    Toast.makeText(ContributeActivity.this, "Successfully uploaded picture!" ,Toast.LENGTH_LONG).show();

                    // Write picture to firebase document
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    // Create photo document to enter to database
                    Contribution contribution = new Contribution(downloadUrl, mCapsuleId, false, currentUser.getUid(), "photo", uName);

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
