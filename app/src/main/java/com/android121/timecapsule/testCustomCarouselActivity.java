package com.android121.timecapsule;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class testCustomCarouselActivity extends AppCompatActivity {


    private static final String TAG = testCustomCarouselActivity.class.getSimpleName();
    CarouselView customCarouselView;
    int NUMBER_OF_PAGES = 5;
    private FirebaseFirestore db;

    String mCapsuleId;
    String content;
    List<ContributionItem> contributionList = new ArrayList<ContributionItem>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        //mCapsuleId = "dvaOmVVVbVaHXjcjn2AI";
        mCapsuleId = "PoiSYfSgGABQyuw4l9wH";
        //mCapsuleId = "3csUst4QZOjsrLDupeGT";

        final Context context = testCustomCarouselActivity.this;


        db = FirebaseFirestore.getInstance();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_custom_carousel);

        // Get capsule id from bundle
//        Bundle extras = getIntent().getExtras();
//
//        if (extras != null) {
//            mCapsuleId = extras.getString("capsuleId");
//            Log.d(TAG, "capsuleId received from bundle:" + mCapsuleId);
//        }

        Query findContributionsQuery = db.collection("contributions").whereEqualTo("capsuleId", mCapsuleId);
        findContributionsQuery.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                          @Override
                                          public void onSuccess(QuerySnapshot documentSnapshots) {
                                              // ...

                                              // Get the last visible document
                                              if (documentSnapshots.size() == 0) {
                                                  Log.d(TAG, "Query returned 0 results");
                                                  String contributionsNotFoundString = "No contributions in this capsule.";
                                                  Toast contributionsNotFoundToast = new Toast(testCustomCarouselActivity.this);
                                                  contributionsNotFoundToast.makeText(testCustomCarouselActivity.this, contributionsNotFoundString, Toast.LENGTH_SHORT).show();

                                                  return;
                                              }

                                              Log.d(TAG, "Query returned : " + documentSnapshots.size());
                                              for (int i = 0; i < documentSnapshots.size(); i++) {
                                                  DocumentSnapshot contributionDocument = documentSnapshots.getDocuments().get(i);
                                                  Log.d(TAG, "adding contribution: type: " + contributionDocument.getString("type") + ", content: " + contributionDocument.getString("content"));
                                                  ContributionItem contribution = new ContributionItem(contributionDocument.getString("type"), contributionDocument.getString("content"), contributionDocument.getString("userId"), contributionDocument.getString("name"));
                                                  contributionList.add(contribution);

                                              }


                                              customCarouselView = (CarouselView) findViewById(R.id.carouselView);

                                              // set ViewListener for custom view



                                              ViewListener viewListener = new ViewListener() {

                                                  @Override
                                                  public View setViewForPosition(int position) {
                                                      Log.d(TAG, "POSITION = " + position);

                                                      View customView = getLayoutInflater().inflate(R.layout.test_custom_carousel_text_view, null);
                                                      //set view attributes here

                                                      RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                                      //RelativeLayout relView = findViewById(R.id.carousel_relative_view);


                                                      TextView textView = customView.findViewById(R.id.carousel_text_view);
                                                      ImageView imageView = customView.findViewById(R.id.carousel_image_view);
                                                      VideoView videoView = customView.findViewById(R.id.carousel_video_view);
                                                      TextView donorView = customView.findViewById(R.id.carousel_donor_view);
                                                      if(position < contributionList.size()) {
                                                          ContributionItem currentContribution = contributionList.get(position);
                                                          if(currentContribution.type.equals("text")){
                                                              textView.setText(currentContribution.content);
                                                              textView.setVisibility(TextView.VISIBLE);

                                                              params.addRule(RelativeLayout.BELOW, R.id.carousel_text_view);
                                                          } else if (currentContribution.type.equals("photo")){
                                                              Glide.with(context).load(currentContribution.content).into(imageView);
                                                              imageView.setVisibility(TextView.VISIBLE);
                                                              params.addRule(RelativeLayout.BELOW, R.id.carousel_image_view);
                                                          } else if (currentContribution.type.equals("video")){
                                                              Uri uri=Uri.parse(currentContribution.content);
                                                              videoView.setVideoURI(uri);
                                                              videoView.setVisibility(VideoView.VISIBLE);
                                                              //videoView.start();

                                                              MediaController mediaController = new MediaController(context);
                                                              videoView.setMediaController(mediaController);
                                                              mediaController.setAnchorView(videoView);

                                                              params.addRule(RelativeLayout.BELOW, R.id.carousel_video_view);
                                                          }




                                                          String donorString = "Contributed by: " + currentContribution.userName;
                                                          donorView.setText(donorString);


                                                          //donorView.setLayoutParams(params);

                                                          //relView.setGravity(Gravity.CENTER);

                                                      }

                                                      return customView;
                                                  }


                                              };

                                              customCarouselView.setViewListener(viewListener);
                                              customCarouselView.setPageCount(contributionList.size());
                                          }
                                      }
                );
    }




}
