package com.example.opencv2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.FileOutputStream;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, AdapterView.OnItemSelectedListener {

    private static final int Camera_Perms = 100;
    private static final int Storage_Perms = 101;

    String[] colors = {"Filter Select", "Normal", "Grey Scale", "Jet", "Ocean", "Spring", "Parula", "Cool", "Twilight"};
    int color_selected;
    int pic_taken = 0;
    int counter = 0;
    int storperms, camperms;
    Mat image;
    Mat pic_mat;
    Bitmap picture;

    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_main);

        //Checking if Permissions are granted
        checkPermission(Manifest.permission.CAMERA, Camera_Perms);

        Button takepic = findViewById(R.id.Picture);

        //Code for the Dropdown
        Spinner filter_select = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter_select.setAdapter(adapter);
        filter_select.setOnItemSelectedListener(this);

        //Code to set up camera start
        cameraBridgeViewBase = (JavaCameraView) findViewById(R.id.CameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);

        takepic.setOnClickListener(v -> {
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Storage_Perms);
            if (storperms == 0) {
                Toast.makeText(getApplicationContext(), "You Need to Grant Storage Permissions for this app to work", Toast.LENGTH_SHORT).show();
            } else {
                //Only triggers if a frame has been captured
                if (counter > 0) {
                    //Updates images and stops the feed
                    pic_mat = image;
                    pic_taken = 1;

                    //Covert to correct typing
                    if ((pic_mat.type() != CvType.CV_8UC4) && (pic_mat.type() != CvType.CV_8UC1) && (pic_mat.type() != CvType.CV_8UC4)) {
                        pic_mat.convertTo(pic_mat, CvType.CV_8UC4);
                    }

                    //Convert the mat to a bitmap
                    picture = Bitmap.createBitmap(pic_mat.rows(), pic_mat.cols(), Bitmap.Config.ARGB_8888);

                    //Need to rotate the bitmap 90 degrees to match the orientation of the mat.
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    picture = Bitmap.createBitmap(picture, 0, 0, picture.getWidth(), picture.getHeight(), matrix, true);
                    Utils.matToBitmap(pic_mat, picture);

                    //Can rotate 90 again to restore back to regular orientation
                    picture = Bitmap.createBitmap(picture, 0, 0, picture.getWidth(), picture.getHeight(), matrix, true);

                    //Code to call a new activity which will send the bitmap over
                    openNewActivity();
                }
            }
        });

        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);
                if (status == BaseLoaderCallback.SUCCESS) {
                    cameraBridgeViewBase.enableView();
                } else {
                    super.onManagerConnected(status);
                }
            }
        };
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
    }

    @Override
    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        image = inputFrame.rgba();
        counter++;
        if (pic_taken == 0) {
            switch (color_selected) {
                default:
                    return image;
                case 2:
                    Imgproc.cvtColor(image, image, Imgproc.COLOR_RGBA2GRAY);
                    return image;
                case 3:
                    Imgproc.cvtColor(image, image, Imgproc.COLOR_RGBA2RGB);
                    Imgproc.applyColorMap(image, image, Imgproc.COLORMAP_JET);
                    return image;
                case 4:
                    Imgproc.cvtColor(image, image, Imgproc.COLOR_RGBA2RGB);
                    Imgproc.applyColorMap(image, image, Imgproc.COLORMAP_OCEAN);
                    return image;
                case 5:
                    Imgproc.cvtColor(image, image, Imgproc.COLOR_RGBA2RGB);
                    Imgproc.applyColorMap(image, image, Imgproc.COLORMAP_SPRING);
                    return image;
                case 6:
                    Imgproc.cvtColor(image, image, Imgproc.COLOR_RGBA2RGB);
                    Imgproc.applyColorMap(image, image, Imgproc.COLORMAP_PARULA);
                    return image;
                case 7:
                    Imgproc.cvtColor(image, image, Imgproc.COLOR_RGBA2RGB);
                    Imgproc.applyColorMap(image, image, Imgproc.COLORMAP_COOL);
                    return image;
                case 8:
                    Imgproc.cvtColor(image, image, Imgproc.COLOR_RGBA2RGB);
                    Imgproc.applyColorMap(image, image, Imgproc.COLORMAP_TWILIGHT);
                    return image;
            }
        } else {
            return pic_mat;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        pic_taken = 0;
        counter = 0;
        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(getApplicationContext(), "There's a problem", Toast.LENGTH_SHORT).show();
        } else {
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pic_taken = 0;
        counter = 0;
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }

    //Function to create a new activity
    public void openNewActivity() {

        try {
            //Write bitmap to a app_specific folder
            String filename = "bitmap.png";
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            picture.compress(Bitmap.CompressFormat.PNG, 100, stream);

            //Cleanup output stream, free memory
            stream.close();
            picture.recycle();

            //Transfer file name over, as the 2nd activity starts up
            Intent in1 = new Intent(this, PictureEditActivity.class);
            in1.putExtra("image", filename);
            startActivity(in1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Function to check and request permission
    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(CameraActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            if (requestCode == 100) {
                camperms = 0;
            }
            if (requestCode == 101) {
                storperms = 0;
            }
            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{permission}, requestCode);
            if (ContextCompat.checkSelfPermission(CameraActivity.this, permission) == PackageManager.PERMISSION_DENIED)
                Toast.makeText(getApplicationContext(), "You Need to Grant Permissions for this app to work", Toast.LENGTH_SHORT).show();
        } else {
            if (requestCode == 100) {
                camperms = 1;
            }
            if (requestCode == 101) {
                storperms = 1;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        color_selected = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        color_selected = 0;
    }
}

