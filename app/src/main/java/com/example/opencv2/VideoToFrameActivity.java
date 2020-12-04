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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import static org.opencv.core.Core.BORDER_CONSTANT;
import static org.opencv.core.Core.DFT_INVERSE;
import static org.opencv.core.Core.NORM_MINMAX;
import static org.opencv.core.Core.copyMakeBorder;
import static org.opencv.core.Core.dft;
import static org.opencv.core.Core.getOptimalDFTSize;
import static org.opencv.core.Core.magnitude;
import static org.opencv.core.Core.merge;
import static org.opencv.core.Core.normalize;
import static org.opencv.core.Core.split;


public class VideoToFrameActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final android.net.Uri videoUri = null;
    String[] filternames = {"Original", "Erosion", "Dilate", "Blur", "Low Pass", "High Pass", "Rift", "Phase"};
    int filter_pos, filter_strength;
    ProgressBar loading;
    TextView progresslabel, wait;
    SeekBar strength;
    ImageView imageView;
    private VideoView myVideoView;
    ArrayList<Bitmap> framesVideo = new ArrayList<>();
    ArrayList<Bitmap> filteredVideo = new ArrayList<>();
    Bitmap frame, filtframe;
    Mat framemat;
    Mat filtmat;


    //Code that controls the sliders
    SeekBar.OnSeekBarChangeListener strengthListener = new SeekBar.OnSeekBarChangeListener() {
        int nowval, lastval;

        @SuppressLint("SetTextI18n")
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            switch (filter_pos) {
                case 1: {
                    progresslabel.setText("Erosion Factor: " + (progress + 1));
                    filter_strength = progress + 1;
                    break;
                }
                case 2: {
                    progresslabel.setText("Dilation Factor: " + (progress + 1));
                    filter_strength = progress + 1;
                    break;
                }
                case 3: {
                    progresslabel.setText("Blur Strength: " + (progress));
                    filter_strength = progress + 1;
                    break;
                }
                case 4: {
                    progresslabel.setText("Low Pass Strength: " + (progress + 10));
                    filter_strength = progress + 10;
                    break;
                }
                case 5: {
                    progresslabel.setText("High Pass Strength: " + (progress + 10));
                    filter_strength = progress + 10;
                    break;
                }
                case 6: {
                    progresslabel.setText("Rift Magnitude: " + (progress + 1));
                    filter_strength = progress + 1;
                    break;
                }
                case 7: {
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
                loading.setVisibility(View.VISIBLE);
                wait.setVisibility(View.VISIBLE);
                switch (filter_pos) {
                    //Sets up the loading / progress bar, creates threads to run specific filters
                    //Then once done running progress bar dissappears and video ready
                    case 1: {
                        new Thread(() -> {
                            Mat kernel = Mat.ones(filter_strength, filter_strength, CvType.CV_8UC1);
                            for (int i = 0; i < framesVideo.size(); i++) {
                                frame = framesVideo.get(i);
                                Utils.bitmapToMat(frame, framemat);
                                Imgproc.erode(framemat, filtmat, kernel);
                                Utils.matToBitmap(filtmat, filtframe);
                                filteredVideo.add(filtframe);
                            }
                            runOnUiThread(() -> {
                                loading.setVisibility(View.INVISIBLE);
                                wait.setVisibility(View.INVISIBLE);
                                myVideoView.setVisibility(View.INVISIBLE);
                                imageView.setVisibility((View.VISIBLE));
                                imageView.setImageBitmap(filteredVideo.get(1));
                            });
                        }).start();
                        break;
                    }
                    case 2: {
                        new Thread(() -> {
                            Mat kernel = Mat.ones(filter_strength, filter_strength, CvType.CV_8UC1);
                            for (int i = 0; i < framesVideo.size(); i++) {
                                frame = framesVideo.get(i);
                                Utils.bitmapToMat(frame, framemat);
                                Imgproc.dilate(framemat, filtmat, kernel);
                                Utils.matToBitmap(filtmat, filtframe);
                                filteredVideo.add(filtframe);
                            }
                            runOnUiThread(() -> {
                                loading.setVisibility(View.INVISIBLE);
                                wait.setVisibility(View.INVISIBLE);
                                myVideoView.setVisibility(View.INVISIBLE);
                                imageView.setVisibility((View.VISIBLE));
                                imageView.setImageBitmap(filteredVideo.get(1));
                            });
                        }).start();
                        break;
                    }
                    case 3: {
                        new Thread(() -> {
                            Size size = new Size(filter_strength, filter_strength);
                            for (int i = 0; i < framesVideo.size(); i++) {
                                frame = framesVideo.get(i);
                                Utils.bitmapToMat(frame, framemat);
                                Imgproc.blur(framemat, filtmat, size);
                                Utils.matToBitmap(filtmat, filtframe);
                                filteredVideo.add(filtframe);
                            }
                            runOnUiThread(() -> {
                                loading.setVisibility(View.INVISIBLE);
                                wait.setVisibility(View.INVISIBLE);
                                myVideoView.setVisibility(View.INVISIBLE);
                                imageView.setVisibility((View.VISIBLE));
                                imageView.setImageBitmap(filteredVideo.get(1));
                            });
                        }).start();
                    }
                    case 4: {
                        Toast.makeText(getApplicationContext(), "Low Pass", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 5: {
                        Toast.makeText(getApplicationContext(), "High Pass", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 6: {
                        new Thread(() -> {
                            for (int i = 0; i < framesVideo.size(); i++) {
                                frame = framesVideo.get(i);
                                Utils.bitmapToMat(frame, framemat);
                                filtmat = rift(framemat, filter_strength);
                                Utils.matToBitmap(filtmat, filtframe);
                                filteredVideo.add(filtframe);
                            }
                            runOnUiThread(() -> {
                                loading.setVisibility(View.INVISIBLE);
                                wait.setVisibility(View.INVISIBLE);
                                myVideoView.setVisibility(View.INVISIBLE);
                                imageView.setVisibility((View.VISIBLE));
                                imageView.setImageBitmap(filteredVideo.get(1));
                            });
                        }).start();
                        break;
                    }
                    case 7: {
                        Toast.makeText(getApplicationContext(), "Phase", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }


            }
        }
    };

    //Rift Function Written by Aidan
    public static Mat rift(Mat img, int mag) {

        List<Mat> chans = new ArrayList<>();

        //split the channels in order to manipulate them
        split(img, chans);

        //Creating Mats to hold various transforms
        Mat curim;
        Mat curdft = new Mat(img.size(), CvType.CV_32FC4);
        Mat padded = new Mat(img.size(), CvType.CV_32FC4);
        Mat curidft = new Mat(img.size(), CvType.CV_32FC4);

        //Creating a list to hold the transformed image channels
        List<Mat> newchans = new ArrayList<>();

        //Getting size for DFT
        int m = getOptimalDFTSize(img.rows());
        int n = getOptimalDFTSize(img.cols());

        //Looping through the 3 color channels of the image
        for (int i = 0; i < 3; i++) {
            curim = chans.get(i);
            // on the border add zero values
            copyMakeBorder(curim, padded, 0, m - curim.rows(), 0, n - curim.cols(), BORDER_CONSTANT, Scalar.all(0));

            //Create the channels needed to perform DFT
            List<Mat> planes = new ArrayList<>();
            planes.add(padded);
            planes.add(Mat.zeros(padded.size(), padded.type()));

            //Add to the expanded another plane with zeros
            merge(planes, curdft);

            //Convert to type as safeguard
            curdft.convertTo(curdft, CvType.CV_32FC4);

            // this way the result may fit in the source matrix
            dft(curdft, curdft);

            // planes[0] = Re(DFT(I), planes[1] = Im(DFT(I))
            split(curdft, planes);

            // set the real part to the magnitude input
            planes.get(0).setTo(new Scalar(mag));

            //Merging the planes together into one new channel
            merge(planes, curdft);
            dft(curdft, curidft, DFT_INVERSE);
            split(curidft, planes);
            magnitude(planes.get(0), planes.get(1), curidft);
            normalize(curidft, curidft, 0, 1, NORM_MINMAX);

            //Adding this new channel to the list
            newchans.add(curidft);
        }

        //Creating a mat that has original input type and size
        Mat rifted = new Mat(img.size(), img.type());

        //then merge them back
        merge(newchans, rifted);

        //Convert to type for display then return
        rifted = convert4return(rifted);
        return rifted;
    }

    //Function to convert specific filters into values for bitmap display
    public static Mat convert4return(Mat m) {
        m.convertTo(m, CvType.CV_8UC4, 255);
        return m;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_play_video);

        //Video Code to initialize Video
        myVideoView = findViewById(R.id.videoView1);
        android.net.Uri videoUri = android.net.Uri.parse(getIntent().getExtras().getString("videoUri"));
        myVideoView.setVideoURI(videoUri);
        myVideoView.start();

        //Removable Image Code, used for testing
        imageView = findViewById(R.id.screenviewv);
        imageView.setVisibility(View.INVISIBLE);

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

        //Loading in the loading bar and wait text
        loading = findViewById(R.id.loading);
        loading.setVisibility(View.INVISIBLE);
        wait = findViewById(R.id.wait);
        wait.setVisibility(View.INVISIBLE);

        //Button for returning
        Button go_back = findViewById(R.id.buttonv);

        //Code that will return to the camera/ picture page
        go_back.setOnClickListener(v -> {
            Intent intent = new Intent(VideoToFrameActivity.this, CameraActivity.class);
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
        for (int i = 0; i < millis * 1000; i += 100000) {
            Bitmap bitmap = retriever.getFrameAtTime(i, MediaMetadataRetriever.OPTION_CLOSEST);
            if (bitmap != null) {
                framesVideo.add(bitmap);
            }
        }
        //getting frame size to initialize mats
        frame = framesVideo.get(0);
        filtframe = frame;
        framemat = new Mat(frame.getHeight(), frame.getWidth(), CvType.CV_8UC4);
        filtmat = new Mat(frame.getHeight(), frame.getWidth(), CvType.CV_8UC4);
    }

    @Override
    //Code that will set up the screen and bars for specific filters
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        filter_pos = position;
        switch (filter_pos) {
            case 0: {//Filter set up for original
                progresslabel.setVisibility(View.INVISIBLE);
                strength.setVisibility(View.INVISIBLE);
                break;
            }
            case 1: {//Set up for Erosion
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
                progresslabel.setText(R.string.iexpansion);
                strength.setMax(49);
                strength.setProgress(0);
                break;
            }
            case 2: { //Set up for Dilations
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
                progresslabel.setText(R.string.idilation);
                strength.setMax(49);
                strength.setProgress(0);
                break;
            }
            case 3: { //Set up for Blur
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
                progresslabel.setText(R.string.iblur);
                strength.setMax(100);
                strength.setProgress(0);
                break;
            }
            case 4: {//Set up for Lowpass
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
                progresslabel.setText(R.string.ilowpass);
                strength.setMax(90);
                strength.setProgress(0);
                break;
            }
            case 5: {//Set up for Highpass
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
                progresslabel.setText(R.string.ihighpass);
                strength.setMax(90);
                strength.setProgress(0);
                break;
            }
            case 6: {//Set up for Rift
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
                progresslabel.setText(R.string.irift);
                strength.setMax(255);
                strength.setProgress(0);
                break;
            }
            case 7: {//Set up for Phase
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
                progresslabel.setText(R.string.iphase);
                strength.setMax(90);
                strength.setProgress(0);
                break;
            }
        }
    }

}



