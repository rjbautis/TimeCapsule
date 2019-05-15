package com.android121.timecapsule;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class OpenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);
    }

    public void onPresentClick(View v){
        Intent intent = new Intent(OpenActivity.this, ViewOpenCapsulesActivity.class);
        startActivity(intent);
    }
}
