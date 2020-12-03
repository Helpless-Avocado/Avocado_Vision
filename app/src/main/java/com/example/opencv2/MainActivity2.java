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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String[] filternames = {"Original", "Pixelate", "RGB Manipulation", "Brightness", "Expansion", "Dilate", "Blur", "Low Pass", "High Pass", "Rift", "Phase"};
    int filter_pos, filter_strength, redstrength, bluestrength, greenstrength;
    Bitmap Image = null;
    Bitmap finalImage = null;
    Bitmap inputimage = null;
    ImageView view1;
    TextView progresslabel;
    SeekBar strength;
    SeekBar Red, Green, Blue;
    TextView rlabel, blabel, glabel;
    Mat OpenCVFrame;
    Mat ToScreen;
    Mat kernel;

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
                case 3: {
                    progresslabel.setText("Brightness: " + (progress - 100));
                    filter_strength = progress;
                    break;
                }
                case 4: {
                    progresslabel.setText("Expansion Factor: " + (progress + 1));
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
                    progresslabel.setText("Low Pass Strength: " + (progress + 10));
                    filter_strength = progress + 10;
                    break;
                }
                case 8: {
                    progresslabel.setText("High Pass Strength: " + (progress + 10));
                    filter_strength = progress + 10;
                    break;
                }
                case 9: {
                    progresslabel.setText("Rift: " + (progress + 1));
                    filter_strength = progress + 1;
                    break;
                }
                case 10: {
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
                        finalImage = pixelate(inputimage, filter_strength);
                        break;
                    }
                    case 3: {
                        //Brighten/Darken
                        finalImage = brightness(inputimage, filter_strength);
                        break;
                    }
                    case 4: {
                        kernel = Mat.ones(filter_strength, filter_strength, CvType.CV_8UC1);
                        Imgproc.erode(OpenCVFrame, ToScreen, kernel);
                        Utils.matToBitmap(ToScreen, finalImage);
                        break;
                    }
                    case 5: {
                        kernel = Mat.ones(filter_strength, filter_strength, CvType.CV_8UC1);
                        Imgproc.dilate(OpenCVFrame, ToScreen, kernel);
                        Utils.matToBitmap(ToScreen, finalImage);
                        break;
                    }
                    case 6: {
                        Imgproc.blur(OpenCVFrame, ToScreen, new Size(filter_strength, filter_strength));
                        Utils.matToBitmap(ToScreen, finalImage);
                        break;
                    }
                    case 7: {
                        //Low Pass Filter. Input is OpenCVFrame and output should be ToScreen
                        Toast.makeText(getApplicationContext(), "Low Pass", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 8: {
                        //High Pass Filter. Input is OpenCVFrame and output should be ToScreen
                        Toast.makeText(getApplicationContext(), "High Pass", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 9: {
                        //Rift. Input is OpenCVFrame and output should be ToScreen
                        Toast.makeText(getApplicationContext(), "Rift", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 10: {
                        //Phase. Input is OpenCVFrame and output should be ToScreen
                        Toast.makeText(getApplicationContext(), "Phase", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                view1.setImageBitmap(finalImage);
            }
        }
    };
    SeekBar.OnSeekBarChangeListener redListener = new SeekBar.OnSeekBarChangeListener() {

        int nowval, lastval;

        @SuppressLint("SetTextI18n")
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            redstrength = progress;
            rlabel.setText("Red %: " + progress + "%");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
            lastval = redstrength;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            nowval = redstrength;
            if (nowval != lastval) {
                finalImage = colorscale(inputimage, redstrength, greenstrength, bluestrength);
                view1.setImageBitmap(finalImage);
            }
        }
    };
    SeekBar.OnSeekBarChangeListener greenListener = new SeekBar.OnSeekBarChangeListener() {

        int nowval, lastval;

        @SuppressLint("SetTextI18n")
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            greenstrength = progress;
            glabel.setText("Green %: " + progress + "%");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the
            lastval = greenstrength;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            nowval = greenstrength;
            if (nowval != lastval) {
                finalImage = colorscale(inputimage, redstrength, greenstrength, bluestrength);
                view1.setImageBitmap(finalImage);
            }
        }
    };
    SeekBar.OnSeekBarChangeListener blueListener = new SeekBar.OnSeekBarChangeListener() {

        int nowval, lastval;

        @SuppressLint("SetTextI18n")
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            bluestrength = progress;
            blabel.setText("Blue %: " + progress + "%");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
            lastval = bluestrength;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            nowval = bluestrength;
            if (nowval != lastval) {
                finalImage = colorscale(inputimage, redstrength, greenstrength, bluestrength);
                view1.setImageBitmap(finalImage);
            }
        }
    };

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
        float rscale = (float) (rvalue / 100);
        float gscale = (float) (gvalue / 100);
        float bscale = (float) (bvalue / 100);

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

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

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

        OpenCVFrame = new Mat(Image.getHeight(), Image.getWidth(), CvType.CV_8UC4);
        ToScreen = new Mat(Image.getHeight(), Image.getWidth(), CvType.CV_8UC4);
        //Preliminary Code for the dropdown
        Spinner image_filters = findViewById(R.id.image_filters);
        ArrayAdapter<String> filtadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filternames);
        filtadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        image_filters.setAdapter(filtadapter);
        image_filters.setOnItemSelectedListener(this);

        //Showing the image on the screen
        view1 = findViewById(R.id.imageView1);
        view1.setImageBitmap(finalImage);

        // Setting up the progress bar and text message of it, hiding them all until ready
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

        //Code to refresh the bitmap for OpenCV
        finalImage = reset(finalImage);

        //Code that will return to the camera/ picture page
        go_back.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, MainActivity.class);
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
            try {
                output = new FileOutputStream(file);

                // Compress into png
                finalImage.compress(Bitmap.CompressFormat.PNG, 100, output);
                output.flush();
                output.close();

                Toast.makeText(getApplicationContext(), "File Successfully Saved to" + path.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "File Not Successfully Saved", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            addImageToGallery(file.getAbsolutePath(), this);
        });

    }

    //Code for telling which input the spinner has selected
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        inputimage = finalImage;
        filter_pos = position;
        Utils.bitmapToMat(inputimage, OpenCVFrame);
        switch (filter_pos) {
            case 0: {
                //Reshows Original image, hides all bars and text
                progresslabel.setVisibility(View.INVISIBLE);
                strength.setVisibility(View.INVISIBLE);
                rlabel.setVisibility((View.INVISIBLE));
                glabel.setVisibility((View.INVISIBLE));
                blabel.setVisibility((View.INVISIBLE));
                Red.setVisibility((View.INVISIBLE));
                Green.setVisibility((View.INVISIBLE));
                Blue.setVisibility((View.INVISIBLE));
                finalImage = reset(Image);
                view1.setImageBitmap(finalImage);
                break;
            }
            case 1: {
                //Sets up for Pixelation
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
                rlabel.setVisibility((View.INVISIBLE));
                glabel.setVisibility((View.INVISIBLE));
                blabel.setVisibility((View.INVISIBLE));
                Red.setVisibility((View.INVISIBLE));
                Green.setVisibility((View.INVISIBLE));
                Blue.setVisibility((View.INVISIBLE));
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
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
                rlabel.setVisibility((View.INVISIBLE));
                glabel.setVisibility((View.INVISIBLE));
                blabel.setVisibility((View.INVISIBLE));
                Red.setVisibility((View.INVISIBLE));
                Green.setVisibility((View.INVISIBLE));
                Blue.setVisibility((View.INVISIBLE));
                progresslabel.setText(R.string.ibrightness);
                strength.setMax(200);
                strength.setProgress(100);
                break;
            }
            case 4: {
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
                rlabel.setVisibility((View.INVISIBLE));
                glabel.setVisibility((View.INVISIBLE));
                blabel.setVisibility((View.INVISIBLE));
                Red.setVisibility((View.INVISIBLE));
                Green.setVisibility((View.INVISIBLE));
                Blue.setVisibility((View.INVISIBLE));
                Utils.bitmapToMat(inputimage, OpenCVFrame);
                progresslabel.setText(R.string.iexpansion);
                strength.setMax(49);
                strength.setProgress(0);

                break;
            }
            case 5: {
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
                rlabel.setVisibility((View.INVISIBLE));
                glabel.setVisibility((View.INVISIBLE));
                blabel.setVisibility((View.INVISIBLE));
                Red.setVisibility((View.INVISIBLE));
                Green.setVisibility((View.INVISIBLE));
                Blue.setVisibility((View.INVISIBLE));
                Utils.bitmapToMat(inputimage, OpenCVFrame);
                progresslabel.setText(R.string.idilation);
                strength.setMax(49);
                strength.setProgress(0);
                break;
            }
            case 6: {
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
                rlabel.setVisibility((View.INVISIBLE));
                glabel.setVisibility((View.INVISIBLE));
                blabel.setVisibility((View.INVISIBLE));
                Red.setVisibility((View.INVISIBLE));
                Green.setVisibility((View.INVISIBLE));
                Blue.setVisibility((View.INVISIBLE));
                Utils.bitmapToMat(inputimage, OpenCVFrame);
                progresslabel.setText(R.string.iblur);
                strength.setMax(100);
                strength.setProgress(0);
                break;
            }
            case 7: {
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
                rlabel.setVisibility((View.INVISIBLE));
                glabel.setVisibility((View.INVISIBLE));
                blabel.setVisibility((View.INVISIBLE));
                Red.setVisibility((View.INVISIBLE));
                Green.setVisibility((View.INVISIBLE));
                Blue.setVisibility((View.INVISIBLE));
                Utils.bitmapToMat(inputimage, OpenCVFrame);
                progresslabel.setText(R.string.ilowpass);
                strength.setMax(90);
                strength.setProgress(0);
                break;
            }
            case 8: {
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
                rlabel.setVisibility((View.INVISIBLE));
                glabel.setVisibility((View.INVISIBLE));
                blabel.setVisibility((View.INVISIBLE));
                Red.setVisibility((View.INVISIBLE));
                Green.setVisibility((View.INVISIBLE));
                Blue.setVisibility((View.INVISIBLE));
                Utils.bitmapToMat(inputimage, OpenCVFrame);
                progresslabel.setText(R.string.ihighpass);
                strength.setMax(90);
                strength.setProgress(0);
                break;
            }
            case 9: {
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
                rlabel.setVisibility((View.INVISIBLE));
                glabel.setVisibility((View.INVISIBLE));
                blabel.setVisibility((View.INVISIBLE));
                Red.setVisibility((View.INVISIBLE));
                Green.setVisibility((View.INVISIBLE));
                Blue.setVisibility((View.INVISIBLE));
                Utils.bitmapToMat(inputimage, OpenCVFrame);
                progresslabel.setText(R.string.irift);
                strength.setMax(254);
                strength.setProgress(1);
                break;
            }
            case 10: {
                progresslabel.setVisibility(View.VISIBLE);
                strength.setVisibility(View.VISIBLE);
                rlabel.setVisibility((View.INVISIBLE));
                glabel.setVisibility((View.INVISIBLE));
                blabel.setVisibility((View.INVISIBLE));
                Red.setVisibility((View.INVISIBLE));
                Green.setVisibility((View.INVISIBLE));
                Blue.setVisibility((View.INVISIBLE));
                Utils.bitmapToMat(inputimage, OpenCVFrame);
                progresslabel.setText(R.string.iphase);
                strength.setMax(90);
                strength.setProgress(0);
                break;
            }
        }
    }
}