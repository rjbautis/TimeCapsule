package com.android121.timecapsule;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private int mtotalCapsules = 0;

    /*
    private TextView mCapsuleName;
    private TextView mDateCreated;
    private TextView mOpened;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        mCapsuleName = (TextView) findViewById(R.id.CapsuleSlot1);
        mCapsuleName.setText("TEST");
    }

    //public void createView(View v){

    }
    */

}
