package com.android121.timecapsule;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class HomeActivity extends AppCompatActivity {

    public static ImageButton gotoAdd;
    public static ImageButton gotoView;
    public static ImageButton gotoEdit;
    public static ImageButton gotoSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Building
        gotoAdd = (ImageButton) findViewById(R.id.capsuleAdd);
        gotoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
            }
        });
    }
}
