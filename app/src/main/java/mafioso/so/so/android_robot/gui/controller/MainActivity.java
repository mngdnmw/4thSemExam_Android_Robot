package mafioso.so.so.android_robot.gui.controller;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import mafioso.so.so.android_robot.R;
import mafioso.so.so.android_robot.dal.RobotConnection;
import mafioso.so.so.android_robot.shared.Circle;
import mafioso.so.so.android_robot.bll.BllFacade;
import mafioso.so.so.android_robot.gui.helper.GpsLocation;
import mafioso.so.so.android_robot.gui.helper.ImgProcessing;
import mafioso.so.so.android_robot.shared.Callback;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "Testing things";
    private static long THREAD_SLEEP = 500;

    private boolean mIsRunning;
    private Mat mRgba;
    private Mat mIntermediateMat;
    private Mat mGray;
    private Mat mHSV;
    private Mat mThresholded;
    private Mat mThresholded2;
    private Mat mArray255;
    private Mat mDistance;
    private BaseLoaderCallback mLoaderCallback;
    private CameraBridgeViewBase mOpenCvCameraView;
    private GpsLocation mGps;
    private BllFacade mBllFac;

    //TODO fix for layers ----------------------------------------
    private RobotConnection robotConnection = new RobotConnection();

    //TODO delete ------------------------------------------------
    private TextView mTxtIP;
    private Button mBtnConnect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        hasPermissions(this);
        getPermissions();

        setupMemberVariables();
        mBtnConnect = findViewById(R.id.btnConnect);
        mTxtIP = findViewById(R.id.txtIP);
        loadConnectionUI();

    }

    protected void getPermissions() {
        int allPermissions = 1;
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};
        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, allPermissions);
        }

    }

    protected static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void setupMemberVariables() {
        mIsRunning = true;
        mBllFac = new BllFacade();
        mGps = new GpsLocation(this);

        mOpenCvCameraView = findViewById(R.id.javaCameraView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS: {
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

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        mIsRunning = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsRunning = true;
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
        mIsRunning = false;
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

    }

    public void onCameraViewStarted(int width, int height) {

        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mHSV = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
        mArray255 = new Mat(height, width, CvType.CV_8UC1);
        mDistance = new Mat(height, width, CvType.CV_8UC1);
        mThresholded = new Mat(height, width, CvType.CV_8UC1);
        mThresholded2 = new Mat(height, width, CvType.CV_8UC1);
        mBllFac.setDecisionMaker(width,height);

    }


    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        mIntermediateMat.release();
        mIsRunning = false;
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        return mRgba;
    }


    protected void loadConnectionUI() {

        mTxtIP.setText("192.168.43.174");
        mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO fix this so it fits the layers
               mBllFac.getRobotConnection().threadConnection(mTxtIP.getText().toString());
                new Thread(new ImgProcessingRunnable()).start();
            }
        });

    }


    private class ImgProcessingRunnable implements Runnable {


        public void run() {
            ImgProcessing imgProc = new ImgProcessing();
           while (!mBllFac.getRobotConnection().isConnected()) {
                Thread.yield();
            }
            while (mIsRunning) {
                Circle circle = imgProc.getCircle(mRgba, mHSV, mThresholded, mThresholded2, mArray255, mDistance);
                mBllFac.getDecisionMaker().MakeDecision(circle);
                if (circle != null) {
                    Log.d(TAG, "diameter " + Double.toString(circle.getDiameter()));
                    Log.d(TAG, "center " + circle.getCenter().toString());
                }
                try {
                    Thread.sleep(THREAD_SLEEP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    protected void uploadImage(final Bitmap image) {

        if (mGps.lastKnownLocation() != null) {

            mBllFac.getmDalFac().uploadImage(image, mGps.lastKnownLocation(), new Callback() {
                @Override
                public void onTaskCompleted(boolean done) {
                    //TODO something with the GUI to notify image has been uploaded

                }
            });
        } else {
            Log.i("Error", "No location found, cannot upload image");
        }

    }
}
