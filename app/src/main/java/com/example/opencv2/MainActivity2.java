package com.example.opencv2;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String[] filternames = {"Original", "Filter1", "Scale Red", "Brightness"};
    int filter_pos, filter_strength;
    Bitmap Image = null;
    Bitmap finalImage = null;
    Bitmap inputimage = null;
    ImageView view1;
    TextView progresslabel;
    SeekBar strength;

    //Code that controls the slider
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            filter_strength = progress;
            progresslabel.setText("Strength: " + (progress - 50));

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
            switch (filter_pos) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    //scale red
                    finalImage = redscale(inputimage, filter_strength);
                    break;
                case 3:
                    //Brighten/Darken
                    finalImage = brightness(inputimage, filter_strength);
                    break;
            }
            view1.setImageBitmap(finalImage);
        }
    };

    //Filter Code to scale the red value
    public static Bitmap redscale(Bitmap orig, int value) {
        //Get Size of the bitmap that is being altered
        int width = orig.getWidth();
        int height = orig.getHeight();
        Bitmap newbit = Bitmap.createBitmap(width, height, orig.getConfig());

        //Scale factor, with 50 set to normal
        float scale = (float) value / 50;

        //Go pixel by pixel to get original color information
        int r, g, b, a;
        int pixvalue;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                //Get values of R from bitmap
                pixvalue = orig.getPixel(i, j);
                a = Color.alpha(pixvalue);
                r = Color.red(pixvalue);
                b = Color.blue(pixvalue);
                g = Color.green(pixvalue);

                //Now scale the value of red;
                r = (int) (r * scale);
                if (r > 255) {
                    r = 255;
                } else if (r < 0) {
                    r = 0;
                }

                // apply new pixel color to output bitmap
                newbit.setPixel(i, j, Color.argb(a, r, g, b));
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

        //Adjust brightness/darkening against 50
        int value = input - 50;
        //Go pixel by pixel to get original color information
        int r, g, b, a;
        int pixvalue;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
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

    //Code to save the image to the gallery
    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

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

        //Preliminary Code for the dropdown
        Spinner image_filters = findViewById(R.id.image_filters);
        ArrayAdapter<String> filtadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filternames);
        filtadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        image_filters.setAdapter(filtadapter);
        image_filters.setOnItemSelectedListener(this);

        //Showing the image on the screen
        view1 = findViewById(R.id.imageView1);
        view1.setImageBitmap(finalImage);

        // Setting up the progress bar and text message of it
        strength = findViewById(R.id.strength);
        strength.setOnSeekBarChangeListener(seekBarChangeListener);
        int strengthvalue = strength.getProgress();
        progresslabel = findViewById(R.id.progress_label);
        progresslabel.setText("Strength: " + strengthvalue);

        // Set up on Click Listeners for the Buttons
        Button go_back = findViewById(R.id.button);
        Button save = findViewById(R.id.save_button);

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
                path.mkdir();
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
        strength.setProgress(50);
        inputimage = finalImage;
        filter_pos = position;
        switch (filter_pos) {
            case 0:
                //Original
                finalImage = Image;
                break;
            case 1:
                break;
            case 2:
                //scale red
                finalImage = redscale(inputimage, filter_strength);
                break;
            case 3:
                //Brighten/Darken
                finalImage = brightness(inputimage, filter_strength);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}