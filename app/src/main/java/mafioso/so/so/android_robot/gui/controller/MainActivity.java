package mafioso.so.so.android_robot.gui.controller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import mafioso.so.so.android_robot.R;
import mafioso.so.so.android_robot.gui.helper.ImgProcessing;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private DataOutputStream dos;
    private DataInputStream dis;
    private Socket socket = new Socket();

    private static final String TAG = "OCV";
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat mRgba;
    private Mat mIntermediateMat;
    private Mat mGray;
    private Mat mHSV;
    private Mat mThresholded;
    private Mat mThresholded2;
    private Mat mArray255;
    private Mat mDistance;

    private Button btnLocation;
    private ImageView mImageView;
    private GpsLocation mGps;

    private boolean connected = false;
    private TextView txtIP;
    private Button btnConnect;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "Opencv loaded successfully");
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getPermissions();
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        btnConnect = findViewById(R.id.btnConnect);
        txtIP = findViewById(R.id.txtIP);
        hasPermissions(this);
        setLayout();
        setListeners();
        loadConnectionUI();
    }

    protected void getPermissions() {
        int allPermissions = 1;
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};
        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, allPermissions);
        }

    }


    protected static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Sets up layout with the objects.
     */

    protected void setLayout() {
        mGps = new GpsLocation(this);

        mOpenCvCameraView = findViewById(R.id.javaCameraView);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);

        btnLocation = findViewById(R.id.btnLocation);

        mImageView = findViewById(R.id.mImageView);
    }


    /**
     * Sets up listeners.
     */

    protected void setListeners() {
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Current Loc " + mGps.lastKnownLocation().getLatitude() + " " + mGps.lastKnownLocation().getLongitude());
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV libary not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV lib found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {

        Log.d("center_point: width, height", Integer.toString(width) + "," + Integer.toString(height));
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mHSV = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
        mArray255 = new Mat(height, width, CvType.CV_8UC1);
        mDistance = new Mat(height, width, CvType.CV_8UC1);
        mThresholded = new Mat(height, width, CvType.CV_8UC1);
        mThresholded2 = new Mat(height, width, CvType.CV_8UC1);

    }


    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        mIntermediateMat.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        //RBGA
        mRgba = inputFrame.rgba();
        mArray255.setTo(new Scalar(255));

        ImgProcessing imgProc = new ImgProcessing();

        double diameter = imgProc.getDiameter(mRgba, mHSV, mThresholded, mThresholded2, mArray255, mDistance);
        Log.d("Diameter ", Double.toString(diameter));

//        final Handler handler = new Handler();
//        Runnable runnable = new Runnable() {
//
//            @Override
//            public void run() {
//                Log.d("myRunnable", "gets in here");
//
////
////
////                handler.postDelayed(this, 1000);
//            }
//        };
//        handler.postDelayed(runnable, 1000);
//


        return mRgba;
    }


    protected void loadConnectionUI() {

        txtIP.setText("192.168.43.174");
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!socket.isConnected()) {
                    threadConnection(txtIP.getText().toString());
                    btnConnect.setText("Command");
                } else if (socket.isConnected()) {
                    sendCommand(txtIP.getText().toString());
                }
            }
        });

    }

    /**
     * Connecting to robot in new thread.
     */
    protected void threadConnection(final String host) {
        new Thread() {
            public void run() {
                try {
                    if (!socket.isConnected()) {
                        socket = new Socket(host, 5969);
                        socket.setKeepAlive(true);

                        dis = new DataInputStream(socket.getInputStream());
                        dos = new DataOutputStream(socket.getOutputStream());

                    }
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * Sending stuff to the robot.
     * Possible Commands =
     * "Roam", "Quit","Back", "Left","Right","Forward","Stop","ChangeDirection"
     */
    protected void sendCommand(final String command) {
        new Thread() {
            public void run() {
                try {

                    dos.writeUTF(command);
                    dos.flush();


                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }.start();
    }

//    /**
//     * working on the image capture and sending it from here and down.
//     */
//
//    static final int REQUEST_IMAGE_CAPTURE = 1;
//
//    static final int REQUEST_TAKE_PHOTO = 1;
//
//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.example.android.fileprovider",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//            }
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            mImageView.setImageBitmap(imageBitmap);
//        }
//    }
//
//    String mCurrentPhotoPath;
//
//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName, //* prefix *//*
//                ".jpg",         //* suffix *//*
//                storageDir //* directory *//*
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
//        return image;
//    }
}
