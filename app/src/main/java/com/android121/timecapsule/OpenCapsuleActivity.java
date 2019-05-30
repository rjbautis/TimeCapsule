package com.android121.timecapsule;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OpenCapsuleActivity extends AppCompatActivity {

    private static final String TAG = OpenCapsuleActivity.class.getSimpleName();
    String mCapsuleId;
    private FirebaseFirestore db;
    String capsuleName;
    List<ContributionItem> contributionItemList = new ArrayList<ContributionItem>();
    ContributionAdapter adapter;
    Context context;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_capsule);

        listView = findViewById(R.id.list_view_contributions);

        // Get capsule id from bundle
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mCapsuleId = extras.getString("capsuleId");
            Log.d(TAG, "capsuleId received from bundle:" + mCapsuleId);
        }

        //mCapsuleId = "bdWcQNkGbvMSSVpS1fad";

        db = FirebaseFirestore.getInstance();

        Query findContributionsQuery = db.collection("contributions").whereEqualTo("capsuleId", mCapsuleId);

        findContributionsQuery.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        // ...

                        // Get the last visible document
                        if(documentSnapshots.size() == 0){
                            Log.d(TAG, "Query returned 0 results");
                            String contributionsNotFoundString = "No contributions in this capsule.";
                            Toast contributionsNotFoundToast = new Toast(OpenCapsuleActivity.this);
                            contributionsNotFoundToast.makeText(OpenCapsuleActivity.this, contributionsNotFoundString, Toast.LENGTH_SHORT).show();

                            return;
                        }

                        Log.d(TAG, "Query returned : " + documentSnapshots.size());
                        for(int i = 0; i < documentSnapshots.size(); i++){
                            DocumentSnapshot contributionDocument = documentSnapshots.getDocuments().get(i);
                            Log.d(TAG, "adding contribution: type: " + contributionDocument.getString("type") + ", content: " + contributionDocument.getString("content"));
                            ContributionItem contribution = new ContributionItem(contributionDocument.getString("type"), contributionDocument.getString("content"), contributionDocument.getString("userId"), contributionDocument.getString("name"));
                            contributionItemList.add(contribution);

                        }

                        adapter = new ContributionAdapter(OpenCapsuleActivity.this, contributionItemList);
                        listView.setAdapter(adapter);



                    }
                });

    }
}
