package com.example.opencv2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class VideoToFrameActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private android.net.Uri videoUri = null;
    ArrayList<Bitmap> framesVideo = new ArrayList<Bitmap>();
    ArrayList<Bitmap> filteredVideo = new ArrayList<Bitmap>();

    String[] filternames = {"Original", "Pixelate", "Brightness", "Erosion", "Dilate", "Blur", "Low Pass", "High Pass", "Rift", "Phase"};
    int filter_pos, filter_strength;
    TextView progresslabel;
    SeekBar strength;
    Bitmap finalImage = null;
    Bitmap inputimage = null;
    private VideoView myVideoView;



    //Code that controls the sliders
    SeekBar.OnSeekBarChangeListener strengthListener = new SeekBar.OnSeekBarChangeListener() {

        int nowval, lastval;

        @SuppressLint("SetTextI18n")
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            switch (filter_pos) {
                case 1: {
                    progresslabel.setText("Pixelation Factor: " + (progress + 1));
                    filter_strength = progress + 1;
                    break;
                }
                case 2: {
                    progresslabel.setText("Brightness: " + (progress - 100));
                    filter_strength = progress;
                    break;
                }
                case 3: {
                    progresslabel.setText("Erosion Factor: " + (progress + 1));
                    filter_strength = progress + 1;
                    break;
                }
                case 4: {
                    progresslabel.setText("Dilation Factor: " + (progress + 1));
                    filter_strength = progress + 1;
                    break;
                }
                case 5: {
                    progresslabel.setText("Blur Strength: " + (progress));
                    filter_strength = progress + 1;
                    break;
                }
                case 6: {
                    progresslabel.setText("Low Pass Strength: " + (progress + 10));
                    filter_strength = progress + 10;
                    break;
                }
                case 7: {
                    progresslabel.setText("High Pass Strength: " + (progress + 10));
                    filter_strength = progress + 10;
                    break;
                }
                case 8: {
                    progresslabel.setText("Rift: " + (progress + 1));
                    filter_strength = progress + 1;
                    break;
                }
                case 9: {
                    progresslabel.setText("Phase Factor: " + (progress));
                    filter_strength = progress;
                    break;
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
            lastval = filter_strength;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
            nowval = filter_strength;
            if (nowval != lastval) {
                switch (filter_pos) {
                    default:
                        break;
                    case 1: {
                        for ()
                            for(int i = 0; i<framesVideo.size(); i++) {
                                inputimage = framesVideo.get(i);
                                finalImage = pixelate(inputimage, filter_strength);
                                filteredVideo.add(finalImage);
                            }

                        reset = 1;
                        break;
                    }
                    case 2: {
                        //Brighten/Darken
//                        finalImage = brightness(inputimage, filter_strength);
//                        reset = 1;
                        break;
                    }
                    case 3: {
//                        if (reset == 0) {
//                            Image = reset(Image);
//                            reset = 1;
//                        }
//                        kernel = Mat.ones(filter_strength, filter_strength, CvType.CV_8UC1);
//                        Imgproc.erode(OpenCVFrame, ToScreen, kernel);
//                        Utils.matToBitmap(ToScreen, finalImage);
                        break;
                    }
                    case 4: {
//                        if (reset == 0) {
//                            Image = reset(Image);
//                            reset = 1;
//                        }
//                        kernel = Mat.ones(filter_strength, filter_strength, CvType.CV_8UC1);
//                        Imgproc.dilate(OpenCVFrame, ToScreen, kernel);
//                        Utils.matToBitmap(ToScreen, finalImage);
                        break;
                    }
                    case 5: {
//                        if (reset == 0) {
//                            Image = reset(Image);
//                            reset = 1;
//                        }
//                        Imgproc.blur(OpenCVFrame, ToScreen, new Size(filter_strength, filter_strength));
//                        Utils.matToBitmap(ToScreen, finalImage);
                        break;
                    }
                    case 6: {
//                        if (reset == 0) {
//                            Image = reset(Image);
//                            reset = 1;
//                        }
                        //Low Pass Filter. Input is OpenCVFrame and output should be ToScreen
                        Toast.makeText(getApplicationContext(), "Low Pass", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 7: {
//                        if (reset == 0) {
//                            Image = reset(Image);
//                            reset = 1;
//                        }
                        //High Pass Filter. Input is OpenCVFrame and output should be ToScreen
                        Toast.makeText(getApplicationContext(), "High Pass", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 8: {
//                        if (reset == 0) {
//                            Image = reset(Image);
//                            reset = 1;
//                        }
                        //Rift. Input is OpenCVFrame and output should be ToScreen
                        Toast.makeText(getApplicationContext(), "Rift", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 9: {
//                        if (reset == 0) {
//                            Image = reset(Image);
//                            reset = 1;
//                        }
                        //Phase. Input is OpenCVFrame and output should be ToScreen
                        Toast.makeText(getApplicationContext(), "Phase", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }


            }
        }
    };

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_play_video);
        myVideoView = findViewById(R.id.videoView1);

        android.net.Uri videoUri = android.net.Uri.parse(getIntent().getExtras().getString( "videoUri"));
        myVideoView.setVideoURI(videoUri);
        myVideoView.start();


        //Preliminary Code for the dropdown
        Spinner image_filters = findViewById(R.id.video_filters);
        ArrayAdapter<String> filtadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filternames);
        filtadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        image_filters.setAdapter(filtadapter);
        image_filters.setOnItemSelectedListener(this);


        // Setting up the progress bar and text message of it, hiding them all until ready
        strength = findViewById(R.id.strengthv);
        strength.setOnSeekBarChangeListener(strengthListener);
        strength.setVisibility(View.INVISIBLE);
        progresslabel = findViewById(R.id.progress_labelv);
        progresslabel.setVisibility(View.INVISIBLE);

        Button go_back = findViewById(R.id.buttonv);

        //Code that will return to the camera/ picture page
        go_back.setOnClickListener(v -> {
            Intent intent = new Intent(VideoToFrameActivity.this, MainActivity.class);
            startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });


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
                framesVideo.add(bitmap);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        inputimage = finalImage;
        filter_pos = position;
//        Utils.bitmapToMat(inputimage, OpenCVFrame);
        switch (filter_pos) {
            case 0: {
                //Reshows Original image, hides all bars and text
                progresslabel.setVisibility(View.INVISIBLE);
                strength.setVisibility(View.INVISIBLE);
//                finalImage = reset(Image);
//                reset = 1;
//                view1.setImageBitmap(finalImage);
                break;
            }
            case 1: {
                //Sets up for Pixelation
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
                progresslabel.setText(R.string.ipixel);
                strength.setMax(19);
                strength.setProgress(0);
                break;
            }
            case 2: {
                //Sets up for Brighten/Darkening
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
                progresslabel.setText(R.string.ibrightness);
                strength.setMax(200);
                strength.setProgress(100);
                break;
            }
            case 3: {
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
//                Utils.bitmapToMat(inputimage, OpenCVFrame);
                progresslabel.setText(R.string.iexpansion);
                strength.setMax(49);
                strength.setProgress(0);

                break;
            }
            case 4: {
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
//                Utils.bitmapToMat(inputimage, OpenCVFrame);
                progresslabel.setText(R.string.idilation);
                strength.setMax(49);
                strength.setProgress(0);
                break;
            }
            case 5: {
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
//                Utils.bitmapToMat(inputimage, OpenCVFrame);
                progresslabel.setText(R.string.iblur);
                strength.setMax(100);
                strength.setProgress(0);
                break;
            }
            case 6: {
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
//                Utils.bitmapToMat(inputimage, OpenCVFrame);
                progresslabel.setText(R.string.ilowpass);
                strength.setMax(90);
                strength.setProgress(0);
                break;
            }
            case 7: {
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
//                Utils.bitmapToMat(inputimage, OpenCVFrame);
                progresslabel.setText(R.string.ihighpass);
                strength.setMax(90);
                strength.setProgress(0);
                break;
            }
            case 8: {
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
//                Utils.bitmapToMat(inputimage, OpenCVFrame);
                progresslabel.setText(R.string.irift);
                strength.setMax(254);
                strength.setProgress(1);
                break;
            }
            case 9: {
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
//                Utils.bitmapToMat(inputimage, OpenCVFrame);
                progresslabel.setText(R.string.iphase);
                strength.setMax(90);
                strength.setProgress(0);
                break;
            }
        }
    }

    public static Bitmap pixelate(Bitmap orig, int input) {

        //Get Size of the bitmap that is being altered
        int width = orig.getWidth();
        int height = orig.getHeight();
        Bitmap newbit = Bitmap.createBitmap(width, height, orig.getConfig());


        //Go get center pixel for pixelation
        int pixvalue;

        for (int i = 0; i < width; i = i + input) {
            for (int j = 0; j < height; j = j + input) {

                //Get values of RGBA from bitmap pixel that will be expanded
                pixvalue = orig.getPixel(i, j);

                //Now loop that will fill out the pixel to extents
                for (int k = 0; k < input; k++) {
                    for (int l = 0; l < input; l++) {
                        if (!(((i + k) >= width) || ((j + l) >= height))) {
                            newbit.setPixel((i + k), (j + l), pixvalue);
                        }
                    }
                }
            }
        }
        return newbit;
    }







    new Thread(() -> {
        finalImage = brightness(inputimage, filter_strength);  //add for loop for video
        runOnUiThread(() -> {
            loading.setVisibility(View.INVISIBLE);
            screenview.setImageBitmap(finalImage);
            wait.setVisibility(View.INVISIBLE);
        });
        reset = 1;
    }).start();

}



