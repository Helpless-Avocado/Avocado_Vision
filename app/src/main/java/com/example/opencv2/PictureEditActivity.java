package com.example.opencv2;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static org.opencv.core.Core.BORDER_CONSTANT;
import static org.opencv.core.Core.DFT_COMPLEX_OUTPUT;
import static org.opencv.core.Core.DFT_INVERSE;
import static org.opencv.core.Core.NORM_MINMAX;
import static org.opencv.core.Core.copyMakeBorder;
import static org.opencv.core.Core.dft;
import static org.opencv.core.Core.getOptimalDFTSize;
import static org.opencv.core.Core.magnitude;
import static org.opencv.core.Core.merge;
import static org.opencv.core.Core.normalize;
import static org.opencv.core.Core.split;

public class PictureEditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //Variables for Control and text
    String[] filternames = {"Original", "Pixelate", "RGB Manipulation", "Brightness", "Erosion", "Dilate", "Blur", "Low Pass", "High Pass", "Rift"};
    int filter_pos, filter_strength, redstrength, bluestrength, greenstrength;
    int reset;
    int processing = 0;

    //View Variables
    ImageView screenview;
    ProgressBar loading;
    TextView progresslabel;
    TextView wait;
    SeekBar strength;
    SeekBar Red, Green, Blue;
    TextView rlabel, blabel, glabel;

    //Image Variables
    Bitmap Image = null;
    Bitmap finalImage = null;
    Bitmap inputimage = null;
    Mat OpenCVFrame;
    Mat ToScreen;
    Mat kernel;

    //Code that controls the slider for the main functions
    SeekBar.OnSeekBarChangeListener strengthListener = new SeekBar.OnSeekBarChangeListener() {

        int nowval, lastval;

        @SuppressLint("SetTextI18n")
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            if (processing == 0) {
                switch (filter_pos) {
                    case 1: {
                        progresslabel.setText("Pixelation Factor: " + (progress + 1));
                        filter_strength = progress + 1;
                        break;
                    }
                    case 3: {
                        progresslabel.setText("Brightness: " + (progress - 100));
                        filter_strength = progress;
                        break;
                    }
                    case 4: {
                        progresslabel.setText("Erosion Factor: " + (progress + 1));
                        filter_strength = progress + 1;
                        break;
                    }
                    case 5: {
                        progresslabel.setText("Dilation Factor: " + (progress + 1));
                        filter_strength = progress + 1;
                        break;
                    }
                    case 6: {
                        progresslabel.setText("Blur Strength: " + (progress));
                        filter_strength = progress + 1;
                        break;
                    }
                    case 7: {
                        progresslabel.setText("Low Pass Strength: " + (progress + 1));
                        filter_strength = progress + 1;
                        break;
                    }
                    case 8: {
                        progresslabel.setText("High Pass Strength: " + (progress + 1));
                        filter_strength = progress + 1;
                        break;
                    }
                    case 9: {
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
            //Called after the user finishes moving the SeekBar only if not processing
            nowval = filter_strength;
            if (processing == 0) {
                if (nowval != lastval) {
                    startProcessing();
                    //Sets up the loading / progress bar, creates threads to run specific filters
                    //Then once done running progress bar dissappears and image is shown
                    switch (filter_pos) {
                        default:
                            break;
                        case 1: {
                            //Pixelate
                            new Thread(() -> {
                                finalImage = pixelate(inputimage, filter_strength);
                                runOnUiThread(() -> stopProcessing());
                                reset = 1;
                            }).start();
                            break;
                        }
                        case 3: {
                            //Brighten/Darken
                            new Thread(() -> {
                                finalImage = brightness(inputimage, filter_strength);
                                runOnUiThread(() -> stopProcessing());
                                reset = 1;
                            }).start();
                            break;
                        }
                        case 4: {
                            //Erode
                            if (reset == 0) {
                                new Thread(() -> {
                                    Image = reset(Image);
                                    runOnUiThread(() -> stopProcessing());
                                    reset = 1;
                                }).start();
                            } else {
                                new Thread(() -> {
                                    kernel = Mat.ones(filter_strength, filter_strength, CvType.CV_8UC1);
                                    Imgproc.erode(OpenCVFrame, ToScreen, kernel);
                                    Utils.matToBitmap(ToScreen, finalImage);
                                    runOnUiThread(() -> stopProcessing());
                                }).start();
                            }
                            break;
                        }
                        case 5: {
                            //Dilate
                            if (reset == 0) {
                                new Thread(() -> {
                                    Image = reset(Image);
                                    runOnUiThread(() -> stopProcessing());
                                    reset = 1;
                                }).start();
                            } else {
                                new Thread(() -> {
                                    kernel = Mat.ones(filter_strength, filter_strength, CvType.CV_8UC1);
                                    Imgproc.dilate(OpenCVFrame, ToScreen, kernel);
                                    Utils.matToBitmap(ToScreen, finalImage);
                                    runOnUiThread(() -> stopProcessing());
                                }).start();
                            }
                            break;
                        }
                        case 6: {
                            //Blur
                            if (reset == 0) {
                                new Thread(() -> {
                                    Image = reset(Image);
                                    runOnUiThread(() -> stopProcessing());
                                    reset = 1;
                                }).start();
                            } else {
                                new Thread(() -> {
                                    Imgproc.blur(OpenCVFrame, ToScreen, new Size(filter_strength, filter_strength));
                                    Utils.matToBitmap(ToScreen, finalImage);
                                    runOnUiThread(() -> stopProcessing());
                                }).start();
                            }
                            break;
                        }
                        case 7: {
                            //Lowpass Filter
                            if (reset == 0) {
                                new Thread(() -> {
                                    Image = reset(Image);
                                    runOnUiThread(() -> stopProcessing());
                                    reset = 1;
                                }).start();
                            } else {
                                new Thread(() -> {
                                    //Low Pass Filter. Input is OpenCVFrame and output should be ToScreen
                                    ToScreen = lowpass(OpenCVFrame, filter_strength);
                                    Utils.matToBitmap(ToScreen, finalImage);
                                    runOnUiThread(() -> stopProcessing());
                                }).start();
                            }
                            break;
                        }
                        case 8: {
                            //HighPass Filter
                            if (reset == 0) {
                                new Thread(() -> {
                                    Image = reset(Image);
                                    runOnUiThread(() -> stopProcessing());
                                    reset = 1;
                                }).start();
                            } else {
                                new Thread(() -> {
                                    //High Pass Filter. Input is OpenCVFrame and output should be ToScreen
                                    ToScreen = highpass(OpenCVFrame, filter_strength);
                                    Utils.matToBitmap(ToScreen, finalImage);
                                    runOnUiThread(() -> stopProcessing());
                                }).start();
                            }
                            break;
                        }
                        case 9: {
                            //Rift
                            if (reset == 0) {
                                new Thread(() -> {
                                    Image = reset(Image);
                                    runOnUiThread(() -> stopProcessing());
                                    reset = 1;
                                }).start();
                            } else {
                                new Thread(() -> {
                                    //Rift. Input is OpenCVFrame and output should be ToScreen
                                    ToScreen = rift(OpenCVFrame, filter_strength);
                                    Utils.matToBitmap(ToScreen, finalImage);
                                    runOnUiThread(() -> stopProcessing());
                                }).start();
                            }
                            break;
                        }
                    }
                    screenview.setImageBitmap(finalImage);
                }
            }
        }
    };

    //Code for the 3 sliders for RGB
    SeekBar.OnSeekBarChangeListener redListener = new SeekBar.OnSeekBarChangeListener() {

        int nowval, lastval;

        @SuppressLint("SetTextI18n")
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb if not processing
            if (processing == 0) {
                redstrength = progress;
                rlabel.setText("Red : " + progress + "%");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
            lastval = redstrength;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //When user stops, apply filter
            nowval = redstrength;
            if (processing == 0) {
                if (nowval != lastval) {
                    startProcessing();
                    new Thread(() -> {
                        finalImage = colorscale(inputimage, redstrength, greenstrength, bluestrength);
                        runOnUiThread(() -> stopProcessing());
                        reset = 1;
                    }).start();
                }
            }
        }
    };
    SeekBar.OnSeekBarChangeListener greenListener = new SeekBar.OnSeekBarChangeListener() {

        int nowval, lastval;

        @SuppressLint("SetTextI18n")
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb while not processing
            if (processing == 0) {
                greenstrength = progress;
                glabel.setText("Green : " + progress + "%");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the thumb
            lastval = greenstrength;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //When user stops, apply filter
            nowval = greenstrength;
            if (processing == 0) {
                if (nowval != lastval) {
                    startProcessing();
                    new Thread(() -> {
                        finalImage = colorscale(inputimage, redstrength, greenstrength, bluestrength);
                        runOnUiThread(() -> stopProcessing());
                        reset = 1;
                    }).start();
                }
            }
        }
    };
    SeekBar.OnSeekBarChangeListener blueListener = new SeekBar.OnSeekBarChangeListener() {

        int nowval, lastval;

        @SuppressLint("SetTextI18n")
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb while not processing
            if (processing == 0) {
                bluestrength = progress;
                blabel.setText("Blue : " + progress + "%");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
            lastval = bluestrength;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //When user stops, apply filter
            nowval = bluestrength;
            if (processing == 0) {
                if (nowval != lastval) {
                    startProcessing();
                    new Thread(() -> {
                        finalImage = colorscale(inputimage, redstrength, greenstrength, bluestrength);
                        runOnUiThread(() -> stopProcessing());
                        reset = 1;
                    }).start();
                }
            }
        }
    };

    //Function that initializes loading bar
    public void startProcessing() {
        loading.setVisibility(View.VISIBLE);
        wait.setVisibility(View.VISIBLE);
        processing = 1;
    }

    //Function that hides loading bar
    public void stopProcessing() {
        screenview.setImageBitmap(finalImage);
        loading.setVisibility(View.INVISIBLE);
        wait.setVisibility(View.INVISIBLE);
        processing = 0;
    }

    //Code to save the image to the gallery
    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

    }

    //Filter Code to scale the red value
    public static Bitmap colorscale(Bitmap orig, int rvalue, int gvalue, int bvalue) {
        //Get Size of the bitmap that is being altered
        int width = orig.getWidth();
        int height = orig.getHeight();
        Bitmap newbit = Bitmap.createBitmap(width, height, orig.getConfig());

        //Scale factor, with 100 set to normal
        float rscale = (float) rvalue / 100;
        float gscale = (float) gvalue / 100;
        float bscale = (float) bvalue / 100;

        //Go pixel by pixel to get original color information
        int r, g, b, a;
        int pixvalue;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //Get values of R from bitmap
                pixvalue = orig.getPixel(i, j);
                a = Color.alpha(pixvalue);
                r = (int) (rscale * Color.red(pixvalue));
                b = (int) (bscale * Color.blue(pixvalue));
                g = (int) (gscale * Color.green(pixvalue));
                //Now control the color based on pixel bounds
                if (r > 255) {
                    r = 255;
                } else if (r < 0) {
                    r = 0;
                }

                if (g > 255) {
                    g = 255;
                } else if (g < 0) {
                    g = 0;
                }

                if (b > 255) {
                    b = 255;
                } else if (b < 0) {
                    b = 0;
                }
                // apply new pixel color to output bitmap
                newbit.setPixel(i, j, Color.argb(a, r, g, b));
            }
        }
        return newbit;
    }

    //Code to adjust the image brightness
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

    //Code to adjust the image brightness
    public static Bitmap brightness(Bitmap orig, int input) {
        //Get Size of the bitmap that is being altered
        int width = orig.getWidth();
        int height = orig.getHeight();
        Bitmap newbit = Bitmap.createBitmap(width, height, orig.getConfig());

        //Adjust brightness/darkening against base value 100
        int value = input - 100;

        //Go pixel by pixel to get original color information
        int r, g, b, a;
        int pixvalue;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //Get values of RGBA from bitmap
                pixvalue = orig.getPixel(i, j);
                a = Color.alpha(pixvalue);
                r = Color.red(pixvalue);
                b = Color.blue(pixvalue);
                g = Color.green(pixvalue);

                //Now apply the change to them, with limits from 0 to 255;
                r = r + value;
                if (r > 255) {
                    r = 255;
                } else if (r < 0) {
                    r = 0;
                }

                g = g + value;
                if (g > 255) {
                    g = 255;
                } else if (g < 0) {
                    g = 0;
                }

                b = b + value;
                if (b > 255) {
                    b = 255;
                } else if (b < 0) {
                    b = 0;
                }

                // apply new pixel color to output bitmap
                newbit.setPixel(i, j, Color.argb(a, r, g, b));
            }
        }
        return newbit;
    }

    //Code to initialize it so that OPEN CV works
    // For some reason, OPENCV only works after something goes through all the pixels in a bitmap
    public static Bitmap reset(Bitmap orig) {
        int width = orig.getWidth();
        int height = orig.getHeight();
        int pixvalue;
        Bitmap newbit = Bitmap.createBitmap(width, height, orig.getConfig());
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixvalue = orig.getPixel(i, j);
                newbit.setPixel(i, j, pixvalue);
            }
        }
        return newbit;
    }

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
        convert4return(rifted);
        return rifted;
    }

    //Function to apply Fourier Shift
    public static void fftshift(Mat FT) {
        // rearrange the quadrants of Fourier image  so that the origin is at the image center
        int cx = FT.cols() / 2;
        int cy = FT.rows() / 2;

        //Creating ROI
        Rect r1 = new Rect(0, 0, cx, cy);
        Rect r2 = new Rect(cx, 0, cx, cy);
        Rect r3 = new Rect(0, cy, cx, cy);
        Rect r4 = new Rect(cx, cy, cx, cy);

        Mat q0 = new Mat(FT, r1);   // Top-Left - Create a ROI per quadrant
        Mat q1 = new Mat(FT, r2);  // Top-Right
        Mat q2 = new Mat(FT, r3);  // Bottom-Left
        Mat q3 = new Mat(FT, r4); // Bottom-Right

        Mat tmp = new Mat(q0.size(), q0.type());           // swap quadrants (Top-Left with Bottom-Right)
        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);

        q1.copyTo(tmp);                    // swap quadrant (Top-Right with Bottom-Left)
        q2.copyTo(q1);
        tmp.copyTo(q2);
    }

    //Function to create distribution
    public static double distform(int x1, int y1, int x2, int y2) {
        return sqrt(pow((x1 - x2), 2) + pow((y1 - y2), 2));
    }

    //Function to create low pass filter mask
    public static Mat lowMask(Mat m, int radius) {
        int row = m.rows();
        int col = m.cols();
        Mat mask = Mat.zeros(row, col, m.type());
        int cx = col / 2;
        int cy = row / 2;
        int i, j;
        for (i = 0; i < row; i++) {
            for (j = 0; j < col; j++) {
                if (distform(cx, cy, j, i) < (double) radius) {
                    mask.put(i, j, (float) 1);
                }
            }
        }
        return mask;
    }

    //Function to create high pass filter mask
    public static Mat hiMask(Mat m, int radius) {
        int row = m.rows();
        int col = m.cols();
        Mat mask = Mat.zeros(row, col, m.type());

        int cx = col / 2;
        int cy = row / 2;
        int i, j;
        for (i = 0; i < row; i++) {
            for (j = 0; j < col; j++) {
                if (distform(cx, cy, j, i) > (double) radius) {
                    mask.put(i, j, (float) 1);
                }
            }
        }
        return mask;
    }

    //Function to apply a highpass filter
    public static Mat highpass(Mat img, int radius) {
        int m = getOptimalDFTSize(img.rows());
        int n = getOptimalDFTSize(img.cols());

        List<Mat> chans = new ArrayList<>();
        //split the channels in order to manipulate them
        split(img, chans);

        //Making mat variables to hold transforms
        Mat curim;
        Mat curdft = new Mat(m, n, CvType.CV_32FC2);

        Mat padded = new Mat(m, n, CvType.CV_32FC1);
        //Creating a mask for the high pass filter
        Mat mask = hiMask(padded, radius);
        Mat curidft = new Mat(m, n, CvType.CV_32FC2);
        List<Mat> newchans = new ArrayList<>();

        //Loop through the channels
        for (int i = 0; i < 3; i++) {
            curim = chans.get(i);
            // on the border add zero values
            copyMakeBorder(curim, padded, 0, m - curim.rows(), 0, n - curim.cols(), BORDER_CONSTANT, Scalar.all(0));

            //Create the channels for the merging and DFT
            List<Mat> planes = new ArrayList<>();
            planes.add(padded);
            planes.add(Mat.zeros(padded.size(), padded.type()));
            merge(planes, curdft);    // Add to the expanded another plane with zeros
            curdft.convertTo(curdft, CvType.CV_32FC2);
            dft(curdft, curdft);          // this way the result may fit in the source matrix
            split(curdft, planes);       // planes[0] = Re(DFT(I), planes[1] = Im(DFT(I))

            //Loop through the planes
            for (int j = 0; j < 2; j++) {
                fftshift(planes.get(j));                // rearrange so low frequencies are centered
                planes.set(j, planes.get(j).mul(mask));  // apply mask: only use values inside/outside radius defined above
                fftshift(planes.get(j));                // rearrange back
            }

            //Merge the multiple mats together into a new channel
            merge(planes, curdft);
            dft(curdft, curidft, DFT_INVERSE | DFT_COMPLEX_OUTPUT);
            split(curidft, planes);
            magnitude(planes.get(0), planes.get(1), curidft);
            normalize(curidft, curidft, 0, 255, NORM_MINMAX);
            newchans.add(curidft);
        }
        Mat hipass = new Mat(img.size(), img.type());
        //then merge newly made channels together
        merge(newchans, hipass);
        hipass.convertTo(hipass, CvType.CV_8UC4);
        return hipass;
    }

    //Function to apply a lowpass filter
    public static Mat lowpass(Mat img, int radius) {
        int m = getOptimalDFTSize(img.rows());
        int n = getOptimalDFTSize(img.cols());

        List<Mat> chans = new ArrayList<>();
        //split the channels in order to manipulate them
        split(img, chans);

        //Making mat variables to hold transforms
        Mat curim;
        Mat curdft = new Mat(m, n, CvType.CV_32FC2);

        Mat padded = new Mat(m, n, CvType.CV_32FC1);
        //Creating a mask for the high pass filter
        Mat mask = lowMask(padded, radius);
        Mat curidft = new Mat(m, n, CvType.CV_32FC2);
        List<Mat> newchans = new ArrayList<>();

        //Loop through the channels
        for (int i = 0; i < 3; i++) {
            curim = chans.get(i);
            // on the border add zero values
            copyMakeBorder(curim, padded, 0, m - curim.rows(), 0, n - curim.cols(), BORDER_CONSTANT, Scalar.all(0));

            //Create the channels for the merging and DFT
            List<Mat> planes = new ArrayList<>();
            planes.add(padded);
            planes.add(Mat.zeros(padded.size(), padded.type()));
            merge(planes, curdft);    // Add to the expanded another plane with zeros
            curdft.convertTo(curdft, CvType.CV_32FC2);
            dft(curdft, curdft);          // this way the result may fit in the source matrix
            split(curdft, planes);       // planes[0] = Re(DFT(I), planes[1] = Im(DFT(I))

            //Loop through the planes
            for (int j = 0; j < 2; j++) {
//                Mat size = new Mat(planes.get(j).cols() & -2, planes.get(j).rows() & -2, planes.get(j).type());
//                planes.set(j, size);
                fftshift(planes.get(j));                // rearrange so low frequencies are centered
//                resize(mask, mask, planes.get(j).size());
//                mask.convertTo(mask, CvType.CV_32FC1);
//                planes.get(j).convertTo(planes.get(j), CvType.CV_32FC1);
                planes.set(j, planes.get(j).mul(mask));  // apply mask: only use values inside/outside radius defined above
                fftshift(planes.get(j));                // rearrange back
            }

            //Merge the multiple mats together into a new channel
            merge(planes, curdft);
            dft(curdft, curidft, DFT_INVERSE | DFT_COMPLEX_OUTPUT);
            split(curidft, planes);
            magnitude(planes.get(0), planes.get(1), curidft);
            normalize(curidft, curidft, 0, 255, NORM_MINMAX);
            newchans.add(curidft);
        }
        Mat hipass = new Mat(img.size(), img.type());
        //then merge newly made channels together
        merge(newchans, hipass);
        hipass.convertTo(hipass, CvType.CV_8UC4);
        return hipass;
    }

    //Function to convert specific filters into values for bitmap display
    public static void convert4return(Mat m) {
        m.convertTo(m, CvType.CV_8UC4, 255);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_main);

        //Resetting Reset
        reset = 0;

        //Load in bitmap info
        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            Image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Create a copy bitmap which will have changes applied to it
        finalImage = Image;

        //Set up Mats for Open CV
        OpenCVFrame = new Mat(Image.getHeight(), Image.getWidth(), CvType.CV_8UC4);
        ToScreen = new Mat(Image.getHeight(), Image.getWidth(), CvType.CV_8UC4);

        //Preliminary Code for the dropdown
        Spinner image_filters = findViewById(R.id.image_filters);
        ArrayAdapter<String> filtadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filternames);
        filtadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        image_filters.setAdapter(filtadapter);
        image_filters.setOnItemSelectedListener(this);

        //Loading in the loading bar and wait text
        loading = findViewById(R.id.loading);
        loading.setVisibility(View.INVISIBLE);
        wait = findViewById(R.id.wait);
        wait.setVisibility(View.INVISIBLE);

        //Showing the image on the screen
        screenview = findViewById(R.id.screenview);
        screenview.setImageBitmap(finalImage);

        // Setting up the strength bar and text message of it, hiding them all until ready
        strength = findViewById(R.id.strength);
        strength.setOnSeekBarChangeListener(strengthListener);
        strength.setVisibility(View.INVISIBLE);
        progresslabel = findViewById(R.id.progress_label);
        progresslabel.setVisibility(View.INVISIBLE);

        // Setting up the RGB bars and text messages of it
        Red = findViewById(R.id.redBar);
        Red.setOnSeekBarChangeListener(redListener);
        Red.setVisibility(View.INVISIBLE);
        rlabel = findViewById(R.id.redText);
        rlabel.setVisibility(View.INVISIBLE);

        Green = findViewById(R.id.greenBar);
        Green.setOnSeekBarChangeListener(greenListener);
        Green.setVisibility(View.INVISIBLE);
        glabel = findViewById(R.id.greenText);
        glabel.setVisibility(View.INVISIBLE);

        Blue = findViewById(R.id.blueBar);
        Blue.setOnSeekBarChangeListener(blueListener);
        Blue.setVisibility(View.INVISIBLE);
        blabel = findViewById(R.id.blueText);
        blabel.setVisibility(View.INVISIBLE);

        // Set up on Click Listeners for the Buttons
        Button go_back = findViewById(R.id.button);
        Button save = findViewById(R.id.save_button);

        //Code that will return to the camera/ picture page
        go_back.setOnClickListener(v -> {
            Intent intent = new Intent(PictureEditActivity.this, CameraActivity.class);
            startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });

        //Code that saves the image to the file, and adds it to the picture gallery.
        save.setOnClickListener(v -> {
            //Store the bitmap in the image gallery
            OutputStream output;

            // Find the SD Card path
            File filepath = Environment.getExternalStorageDirectory();

            // Create a new folder in SD Card
            File path = new File(filepath.getAbsolutePath() + "/FilterImages/");
            if (!path.exists()) {
                boolean check = path.mkdir();
                if (!check) {
                    Toast.makeText(getApplicationContext(), "Failed to Find/ Make Directory", Toast.LENGTH_SHORT).show();
                }
            }

            // Create a name for the saved image and save to gallery
            Date currentTime = Calendar.getInstance().getTime();
            String newfilename = currentTime.toString();
            File file = new File(path, newfilename + ".png");

            //If successfully saved, shows filepath, otherwise shows error
            try {
                output = new FileOutputStream(file);

                // Compress into png
                finalImage.compress(Bitmap.CompressFormat.PNG, 100, output);
                output.flush();
                output.close();
                Toast.makeText(getApplicationContext(), "File Successfully Saved to" + path.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                //Adds the file to gallery
                addImageToGallery(file.getAbsolutePath(), this);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "File Not Successfully Saved", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    //Function that sets up 1 filter view
    public void onefiltsetup() {
        progresslabel.setVisibility(View.VISIBLE);
        strength.setVisibility(View.VISIBLE);
        rlabel.setVisibility((View.INVISIBLE));
        glabel.setVisibility((View.INVISIBLE));
        blabel.setVisibility((View.INVISIBLE));
        Red.setVisibility((View.INVISIBLE));
        Green.setVisibility((View.INVISIBLE));
        Blue.setVisibility((View.INVISIBLE));
    }

    //Code for telling which input the spinner has selected
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        inputimage = finalImage;
        filter_pos = position;
        Utils.bitmapToMat(inputimage, OpenCVFrame);
        switch (filter_pos) {
            case 0: {
                progresslabel.setVisibility(View.INVISIBLE);
                strength.setVisibility(View.INVISIBLE);
                rlabel.setVisibility((View.INVISIBLE));
                glabel.setVisibility((View.INVISIBLE));
                blabel.setVisibility((View.INVISIBLE));
                Red.setVisibility((View.INVISIBLE));
                Green.setVisibility((View.INVISIBLE));
                Blue.setVisibility((View.INVISIBLE));
                //Reshows Original image, hides all bars and text
                finalImage = reset(Image);
                reset = 1;
                screenview.setImageBitmap(finalImage);
                break;
            }
            case 1: {
                //Sets up for Pixelation
                onefiltsetup();
                progresslabel.setText(R.string.ipixel);
                strength.setMax(19);
                strength.setProgress(0);
                break;
            }
            case 2: {
                //Sets up for Color Scaling
                progresslabel.setVisibility(View.INVISIBLE);
                strength.setVisibility(View.INVISIBLE);
                rlabel.setVisibility((View.VISIBLE));
                glabel.setVisibility((View.VISIBLE));
                blabel.setVisibility((View.VISIBLE));
                Red.setVisibility((View.VISIBLE));
                Green.setVisibility((View.VISIBLE));
                Blue.setVisibility((View.VISIBLE));
                rlabel.setText(R.string.ired);
                glabel.setText(R.string.igreen);
                blabel.setText(R.string.iblue);
                Red.setMax(200);
                Red.setProgress(100);
                Green.setMax(200);
                Green.setProgress(100);
                Blue.setMax(200);
                Blue.setProgress(100);
                break;
            }
            case 3: {
                //Sets up for Brighten/Darkening
                onefiltsetup();
                progresslabel.setText(R.string.ibrightness);
                strength.setMax(200);
                strength.setProgress(100);
                break;
            }
            case 4: {
                onefiltsetup();
                Utils.bitmapToMat(inputimage, OpenCVFrame);
                progresslabel.setText(R.string.iexpansion);
                strength.setMax(49);
                strength.setProgress(0);

                break;
            }
            case 5: {
                onefiltsetup();
                Utils.bitmapToMat(inputimage, OpenCVFrame);
                progresslabel.setText(R.string.idilation);
                strength.setMax(49);
                strength.setProgress(0);
                break;
            }
            case 6: {
                onefiltsetup();
                Utils.bitmapToMat(inputimage, OpenCVFrame);
                progresslabel.setText(R.string.iblur);
                strength.setMax(100);
                strength.setProgress(0);
                break;
            }
            case 7: {
                onefiltsetup();
                Utils.bitmapToMat(inputimage, OpenCVFrame);
                progresslabel.setText(R.string.ilowpass);
                strength.setMax(99);
                strength.setProgress(0);
                break;
            }
            case 8: {
                onefiltsetup();
                Utils.bitmapToMat(inputimage, OpenCVFrame);
                progresslabel.setText(R.string.ihighpass);
                strength.setMax(99);
                strength.setProgress(0);
                break;
            }
            case 9: {
                onefiltsetup();
                Utils.bitmapToMat(inputimage, OpenCVFrame);
                progresslabel.setText(R.string.irift);
                strength.setMax(255);
                strength.setProgress(0);
                break;
            }
        }
    }
}