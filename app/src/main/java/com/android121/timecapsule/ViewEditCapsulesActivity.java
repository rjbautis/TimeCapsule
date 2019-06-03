package com.android121.timecapsule;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ViewEditCapsulesActivity extends AppCompatActivity {

    private static final String TAG = ViewEditCapsulesActivity.class.getSimpleName();


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar mProgressBar;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private ArrayList<CapsuleItem> exampleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mRecyclerView = findViewById(R.id.recyclerView);
        mProgressBar = findViewById(R.id.progressBar);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Get all capsules (by ID) that a user has been invited
        final ArrayList<String> invitedCapsuleIdList = new ArrayList<>();

        // Get all invitations that you've been invited to
        db.collection("invitations")
                .whereEqualTo("inviteEmail", Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Invitation invitation = document.toObject(Invitation.class);
                                invitedCapsuleIdList.add(invitation.capsuleId);
                            }

                            Log.d(TAG, "size of invitations: " + invitedCapsuleIdList.size());

                            // Only show invited capsules where open date has not yet passed (i.e. are greater than or equal to current date)
                            Date today = Calendar.getInstance().getTime();

                            // Retrieve all capsules
                            // TODO: only want to retrieve capsules that user was invited to
                            db.collection("capsules")
                                    .whereGreaterThanOrEqualTo("openDate", today)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                    Capsule capsule = document.toObject(Capsule.class);

                                                    // Only retrieve capsules that user was invited to
                                                    if (invitedCapsuleIdList.contains(document.getId()) && capsule != null) {
                                                        CapsuleItem item = new CapsuleItem(R.drawable.download, capsule.capsuleName, capsule.openDate.toLocaleString());

                                                        // Set the id of the item for usage
                                                        item.setCapsuleId(document.getId());
                                                        exampleList.add(item);
                                                    }
                                                }

                                                mProgressBar.setVisibility(View.GONE);

                                                // Set adapter after all documents have been added to list
                                                mAdapter = new ViewEditCapsulesAdapter(exampleList, ViewEditCapsulesActivity.this);
                                                mRecyclerView.setAdapter(mAdapter);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ViewEditCapsulesActivity.this, "Error occurred while loading capsules.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                });
    }
}
