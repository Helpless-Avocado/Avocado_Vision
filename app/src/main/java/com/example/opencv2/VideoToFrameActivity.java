package com.example.opencv2;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class VideoToFrameActivity extends AppCompatActivity {
    private android.net.Uri videoUri = null;
    ArrayList<Bitmap> rev = new ArrayList<Bitmap>();
    @Override
    protected void onCreate(Bundle SavedInstanceState) {

        super.onCreate(SavedInstanceState);

        //get videoUri from MainActivity
        videoUri = Uri.parse(getIntent().getExtras().getString("videoUri"));



        //create new object to retrieve data, set video to be data source
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, videoUri);



        //Create new mediaplayer

        MediaPlayer mp = MediaPlayer.create(getBaseContext(), videoUri);

        //get duration of video
        int millis = mp.getDuration();

        //for each time interval save bitmap to bitmap array
        for (int i = 0; i < millis*1000; i += 100000) {
            Bitmap bitmap = retriever.getFrameAtTime(i, MediaMetadataRetriever.OPTION_CLOSEST);
            if (bitmap != null) {
                rev.add(bitmap);


            }
        }


    }


}



