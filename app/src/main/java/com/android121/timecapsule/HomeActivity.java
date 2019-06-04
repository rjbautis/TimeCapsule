package com.android121.timecapsule;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private ImageButton mGotoAdd;
    private ImageButton mGotoView;

    private ImageButton mGotoEdit;
    private ImageButton mGotoOpen;
    private ImageButton mGotoSettings;

    private Button mLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        mGotoAdd = (ImageButton) findViewById(R.id.capsuleAdd);
        mGotoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, CreateCapsuleActivity.class));
            }
        });

        mGotoView = findViewById(R.id.capsuleHistory);
        mGotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ViewEditCapsulesActivity.class));
            }
        });

//        mGoToOpen = findViewById(R.id.capsuleOpen);
//        mGoToOpen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(HomeActivity.this, ViewOpenCapsulesActivity.class));
//            }
//        });

        mGotoOpen = (ImageButton) findViewById(R.id.capsuleOpen);
        mGotoOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, OpenActivity.class));
            }
        });

        mLogOut = (Button) findViewById(R.id.logOut);
        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });
    }
}
