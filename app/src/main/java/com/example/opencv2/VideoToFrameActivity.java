package com.example.opencv2;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.arthenica.mobileffmpeg.FFmpeg;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    //Accessor and control variables
    Uri original;
    String[] filternames = {"Original", "Erosion", "Dilate", "Blur", "Rift"};
    int filter_pos, filter_strength;
    int processing = 0;
    int present = 0;
    File path;

    //View Variables
    ProgressBar loading;
    TextView progresslabel, wait, ffproc, del;
    SeekBar strength;
    ImageView imageView;
    private VideoView myVideoView;
    Button go_back;

    //Image Variables
    ArrayList<Bitmap> framesVideo = new ArrayList<>();
    ArrayList<Bitmap> filteredVideo = new ArrayList<>();
    Bitmap frame;
    Mat framemat;
    Mat filtmat;

    //Code that controls the sliders
    SeekBar.OnSeekBarChangeListener strengthListener = new SeekBar.OnSeekBarChangeListener() {
        int nowval, lastval;

        @SuppressLint("SetTextI18n")
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb, only if not processing
            if (processing == 0) {
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
                        progresslabel.setText("Rift Magnitude: " + (progress + 1));
                        filter_strength = progress + 1;
                        break;
                    }
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
            //Checks if the image is processing, and if the value has changed
            if (processing == 0) {
                if (nowval != lastval) {
                    startProcessing();
                    switch (filter_pos) {
                        //Sets up the loading / progress bar, creates threads to run specific filters
                        //Then once done running progress bar dissappears and video ready
                        case 1: {
                            new Thread(() -> {
                                Mat kernel = Mat.ones(filter_strength, filter_strength, CvType.CV_8UC1);
                                for (int i = 0; i < framesVideo.size(); i++) {
                                    frame = framesVideo.get(i);
                                    Bitmap filtframe = frame;
                                    Utils.bitmapToMat(frame, framemat);
                                    Imgproc.erode(framemat, filtmat, kernel);
                                    Utils.matToBitmap(filtmat, filtframe);
                                    filteredVideo.add(filtframe);
                                }
                                runOnUiThread(() -> {
                                    makeVideo();
                                    stopProcessing();
                                });
                            }).start();
                            break;
                        }
                        case 2: {
                            new Thread(() -> {
                                Mat kernel = Mat.ones(filter_strength, filter_strength, CvType.CV_8UC1);
                                for (int i = 0; i < framesVideo.size(); i++) {
                                    frame = framesVideo.get(i);
                                    Bitmap filtframe = frame;
                                    Utils.bitmapToMat(frame, framemat);
                                    Imgproc.dilate(framemat, filtmat, kernel);
                                    Utils.matToBitmap(filtmat, filtframe);
                                    filteredVideo.add(filtframe);
                                }
                                runOnUiThread(() -> {
                                    makeVideo();
                                    stopProcessing();
                                });
                            }).start();
                            break;
                        }
                        case 3: {
                            new Thread(() -> {
                                Size size = new Size(filter_strength, filter_strength);
                                for (int i = 0; i < framesVideo.size(); i++) {
                                    frame = framesVideo.get(i);
                                    Bitmap filtframe = frame;
                                    Utils.bitmapToMat(frame, framemat);
                                    Imgproc.blur(framemat, filtmat, size);
                                    Utils.matToBitmap(filtmat, filtframe);
                                    filteredVideo.add(filtframe);
                                }
                                runOnUiThread(() -> {
                                    makeVideo();
                                    stopProcessing();
                                });
                            }).start();
                            break;
                        }
                        case 4: {
                            new Thread(() -> {
                                for (int i = 0; i < framesVideo.size(); i++) {
                                    frame = framesVideo.get(i);
                                    Bitmap filtframe = frame;
                                    Utils.bitmapToMat(frame, framemat);
                                    filtmat = rift(framemat, filter_strength);
                                    Utils.matToBitmap(filtmat, filtframe);
                                    filteredVideo.add(filtframe);
                                }
                                runOnUiThread(() -> {
                                    makeVideo();
                                    stopProcessing();
                                });
                            }).start();
                            break;
                        }
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
        }

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


        //Creating a mat that has original input type and size
        Mat rifted = new Mat(img.size(), img.type());

        //then merge them back
        merge(newchans, rifted);

        //Convert to type for display then return
        rifted.convertTo(rifted, CvType.CV_8UC4, 255);
        return rifted;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.video_main);

        setTitle("Edit video");

        //Video Code to initialize Video
        myVideoView = findViewById(R.id.videoView1);
        android.net.Uri videoUri = android.net.Uri.parse(getIntent().getExtras().getString("videoUri"));
        myVideoView.setVideoURI(videoUri);

        //Removable Image Code, used for testing
        imageView = findViewById(R.id.screenViewv);
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
        ffproc = findViewById(R.id.ffwait);
        ffproc.setVisibility(View.INVISIBLE);
        del = findViewById(R.id.del);
        del.setVisibility(View.INVISIBLE);

        //Initialize folder location
        // Find the SD Card patH
        File filepath = Environment.getExternalStorageDirectory();
        // Create a new folder in SD Card
        path = new File(filepath.getAbsolutePath() + "/Avocado_Vision/");
        if (!path.exists()) {
            boolean check = path.mkdir();
            if (!check) {
                Toast.makeText(getApplicationContext(), "Failed to Find/ Make Directory", Toast.LENGTH_SHORT).show();
            }
        }

        //Button for returning
        go_back = findViewById(R.id.buttonv);

        //Code that will return to the camera/ picture page
        go_back.setOnClickListener(v -> {
            deletefiles();
            Intent intent = new Intent(VideoToFrameActivity.this, CameraActivity.class);
            startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });

        //Button for playing the video again
        Button play = findViewById(R.id.play);
        play.setOnClickListener(v -> myVideoView.start());

        //get videoUri from MainActivity and store it for later use
        original = Uri.parse(getIntent().getExtras().getString("videoUri"));

        //create new object to retrieve data, set video to be data source
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, original);

        //Create new mediaplayer
        MediaPlayer mp = MediaPlayer.create(getBaseContext(), videoUri);

        //get duration of video
        int millis = mp.getDuration();

        //for each time interval save bitmap to bitmap array
        startProcessing();
        new Thread(() -> {
            for (int i = 0; i < millis * 1000; i += 100000) {
                Bitmap bitmap = retriever.getFrameAtTime(i, MediaMetadataRetriever.OPTION_CLOSEST);
                if (bitmap != null) {
                    framesVideo.add(bitmap);
                }
            }
            runOnUiThread(() -> {
                frame = framesVideo.get(0);
                //getting frame size to initialize mats
                framemat = new Mat(frame.getHeight(), frame.getWidth(), CvType.CV_8UC4);
                filtmat = new Mat(frame.getHeight(), frame.getWidth(), CvType.CV_8UC4);
                stopProcessing();
                myVideoView.start();
            });
        }).start();
    }

    //Code to make the video
    public void makeVideo() {
        startProcessing();
        present = 1;
        new Thread(() -> {
            //save all filtered video frames as .png
            for (int i = 0; i < framesVideo.size(); i++) {
                try {
                    String filename = "frame" + i + ".png";
                    File file = new File(path, filename);
                    FileOutputStream out = new FileOutputStream(file);
                    Bitmap New = filteredVideo.get(i);
                    New.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "File Not Successfully Saved", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    });
                }
                runOnUiThread(this::startFFProcessing);
            }

            String apath = path.getAbsolutePath();
            frame = framesVideo.get(1);
            String Size = frame.getWidth() + "x" + frame.getHeight();

            //Setting up saved video name
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
            Date now = new Date();
            String newname = formatter.format(now) + ".mp4";
            String[] cmd = new String[]{"-y", "-r", "10", "-f", "image2", "-s", Size, "-i", apath + "/frame%d.png",
                    "-crf", "25", "-pix_fmt", "yuv420p", path + "/" + newname};
            FFmpeg.execute(cmd);

            runOnUiThread(() -> {
                myVideoView.setVideoURI(Uri.parse(path + "/" + newname));
                go_back.setText(R.string.Return);
                savetogallery(path + "/" + newname, newname);
                Toast.makeText(getApplicationContext(), "Video Saved to: " + path, Toast.LENGTH_SHORT).show();
                stopFFProcessing();
            });
        }).start();
    }

    //Code to link the video with the gallery
    public void savetogallery(String filepath, String name) {
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Video.Media.TITLE, name);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, filepath);
        getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }

    //Function that initializes loading screen , pauses video
    public void startProcessing() {
        myVideoView.pause();
        loading.setVisibility(View.VISIBLE);
        wait.setVisibility(View.VISIBLE);
        ffproc.setVisibility(View.INVISIBLE);
        processing = 1;
    }

    //Indicates FFMPEG is working
    public void startFFProcessing() {
        wait.setVisibility(View.INVISIBLE);
        ffproc.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);
        processing = 1;
    }

    //Function that hides loading screen
    public void stopProcessing() {
        loading.setVisibility(View.INVISIBLE);
        wait.setVisibility(View.INVISIBLE);
        ffproc.setVisibility(View.INVISIBLE);
        processing = 0;
    }

    //Indicates FFMPEG is done and plays result
    public void stopFFProcessing() {
        wait.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.INVISIBLE);
        ffproc.setVisibility(View.INVISIBLE);
        processing = 0;
        myVideoView.start();
    }

    //Code to clear .png files
    public void deletefiles() {
        //Code to delete the files and clean them up before going back, if files were present
        if (present == 1) {
            del.setVisibility(View.VISIBLE);
            loading.setVisibility(View.VISIBLE);
            present = 0;
            new Thread(() -> {
                boolean deleted;
                for (int i = 0; i < filteredVideo.size(); i++) {
                    try {
                        String filename = "frame" + i + ".png";
                        File file = new File(path, filename);
                        deleted = file.delete();
                        if (!deleted) {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Failed to Delete Files", Toast.LENGTH_SHORT).show());
                        }
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Failed to Find Files", Toast.LENGTH_SHORT).show());
                        e.printStackTrace();
                    }
                }
                runOnUiThread(() -> {
                    del.setVisibility(View.INVISIBLE);
                    loading.setVisibility(View.INVISIBLE);
                });
            }).start();
        }
    }

    @Override
    //Code that will set up the screen and bars for specific filters
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        filter_pos = position;
        deletefiles();
        switch (filter_pos) {
            case 0: {//Filter set up for original
                progresslabel.setVisibility(View.INVISIBLE);
                strength.setVisibility(View.INVISIBLE);
                myVideoView.setVideoURI(original);
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
            case 4: {//Set up for Rift
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
                progresslabel.setText(R.string.irift);
                strength.setMax(255);
                strength.setProgress(0);
                break;
            }
        }
    }
}



