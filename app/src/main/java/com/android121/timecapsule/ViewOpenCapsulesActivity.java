package com.android121.timecapsule;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class ViewOpenCapsulesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar mProgressBar;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private ArrayList<EditItem> exampleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_open_capsules);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mRecyclerView = findViewById(R.id.recyclerView);
        mProgressBar = findViewById(R.id.progressBar);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Get all capsules (by ID) that a user is the recipient of

        final FirebaseUser currentUser = mAuth.getCurrentUser();

        db.collection("capsules")
                .whereEqualTo("recipientId", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Capsule capsule = document.toObject(Capsule.class);

                                // Only retrieve capsules that user was invited to
                                EditItem item = new EditItem(R.drawable.download, capsule.capsuleName, capsule.openDate.toLocaleString());

                                // Set the id of the item for usage
                                item.setCapsuleId(document.getId());
                                exampleList.add(item);
                            }

                            mProgressBar.setVisibility(View.GONE);

                            // Set adapter after all documents have been added to list
                            mAdapter = new ViewOpenCapsulesAdapter(exampleList, ViewOpenCapsulesActivity.this);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewOpenCapsulesActivity.this, "Error occurred while loading capsules.", Toast.LENGTH_LONG).show();
                    }
                });

    }
}
