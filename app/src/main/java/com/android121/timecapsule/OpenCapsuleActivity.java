package com.android121.timecapsule;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenCapsuleActivity extends YouTubeBaseActivity {


    private static final String TAG = OpenCapsuleActivity.class.getSimpleName();
    CarouselView customCarouselView;
    int NUMBER_OF_PAGES = 5;
    private FirebaseFirestore db;

    String mCapsuleId;
    String content;
    List<ContributionItem> contributionList = new ArrayList<ContributionItem>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        //mCapsuleId = "dvaOmVVVbVaHXjcjn2AI";
        //mCapsuleId = "PoiSYfSgGABQyuw4l9wH";
        //mCapsuleId = "3csUst4QZOjsrLDupeGT";
        mCapsuleId = "a9nvHEgsdBAdO4R21YBI";

        final Context context = OpenCapsuleActivity.this;


        db = FirebaseFirestore.getInstance();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_capsule);

        // Get capsule id from bundle
        Bundle extras = getIntent().getExtras();

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
                                                  Toast contributionsNotFoundToast = new Toast(OpenCapsuleActivity.this);
                                                  contributionsNotFoundToast.makeText(OpenCapsuleActivity.this, contributionsNotFoundString, Toast.LENGTH_SHORT).show();

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
                                                      YouTubePlayerView youtubeView = customView.findViewById(R.id.carousel_youtube_view);
                                                      ImageView spotifyAlbumImageView = customView.findViewById(R.id.carousel_spotify_art_view);
                                                      TextView spotifyTitleTextView = customView.findViewById(R.id.carousel_spotify_song_title_view);
                                                      LinearLayout spotifyView = customView.findViewById(R.id.carousel_spotify_view);
                                                      if(position < contributionList.size()) {
                                                          final ContributionItem currentContribution = contributionList.get(position);
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
                                                          } else if (currentContribution.type.equals("yt_video")){

                                                              String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|watch\\?v%3D|%2Fvideos%2F|embed%2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

                                                              Pattern compiledPattern = Pattern.compile(pattern);
                                                              Matcher matcher = compiledPattern.matcher(currentContribution.content);

                                                              final String yt_link;
                                                              if(matcher.find()){
                                                                  yt_link =  matcher.group();
                                                              } else {
                                                                  yt_link = "";
                                                              }

                                                              YouTubePlayer.OnInitializedListener ytListener = new YouTubePlayer.OnInitializedListener() {
                                                                  @Override
                                                                  public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                                                                      Log.d(TAG, "Youtube initialization success!");
                                                                      Log.d(TAG, "yt_link = " + yt_link);
                                                                      youTubePlayer.cueVideo(yt_link);
                                                                  }

                                                                  @Override
                                                                  public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                                                                      Log.d(TAG, "Youtube initialization failure!");
                                                                  }
                                                              };
                                                              youtubeView.initialize(youtube_config.YOUTUBE_API_KEY, ytListener);
                                                              youtubeView.setVisibility(View.VISIBLE);

                                                          } else if (currentContribution.type.equals("spotify")){
                                                              //get title and art from SpotifyGet(content)

                                                              String[] separated = currentContribution.content.split("\\|");

                                                              final String spotifyUrl = separated[0];
                                                              Log.d(TAG, spotifyUrl);
                                                              String albumArtUrl = separated[1];
                                                              Log.d(TAG, albumArtUrl);
                                                              String songName = separated[2];
                                                              Log.d(TAG, songName);
                                                              //String artistName = "";
                                                              //String spotifyText = songName + " - " + artistName;

                                                              // set spotifyAlbumImageView
                                                              Glide.with(context).load(albumArtUrl).into(spotifyAlbumImageView);
                                                              spotifyAlbumImageView.setVisibility(ImageView.VISIBLE);
                                                              spotifyAlbumImageView.setOnClickListener(new View.OnClickListener() {
                                                                  @Override
                                                                  public void onClick(View v) {

                                                                          Uri spotifyWebpage = Uri.parse(spotifyUrl);
                                                                          Intent intent = new Intent(Intent.ACTION_VIEW, spotifyWebpage);
                                                                          if (intent.resolveActivity(getPackageManager()) != null) {
                                                                              startActivity(intent);
                                                                          }


                                                                  }
                                                              });

                                                              // set spotifyTitleTextView
                                                              spotifyTitleTextView.setText(songName);
                                                              spotifyTitleTextView.setVisibility(TextView.VISIBLE);
                                                              spotifyView.setVisibility(LinearLayout.VISIBLE);


                                                              // set touch listener to link to spotify
                                                          } else if(currentContribution.type.equals("paypal_amount")) {
                                                              String moneyString = "$" + currentContribution.content;
                                                              textView.setText(moneyString);
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

