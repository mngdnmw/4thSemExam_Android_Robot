package mafioso.so.so.android_robot.gui.controller;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import mafioso.so.so.android_robot.R;
import mafioso.so.so.android_robot.shared.Circle;
import mafioso.so.so.android_robot.bll.BllFacade;
import mafioso.so.so.android_robot.gui.helper.GpsLocation;
import mafioso.so.so.android_robot.gui.helper.ImgProcessing;
import mafioso.so.so.android_robot.shared.Callback;

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
    private ImageView mImgViewUploaded;
    private GpsLocation mGps;
    private boolean mIsRunning;

    private static long THREAD_SLEEP = 500;
    private Context mContext;
    private BllFacade mBllFac;

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
        mContext = this;
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
        mIsRunning = true;
        mBllFac = new BllFacade();

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

        mImgViewUploaded = findViewById(R.id.mImageView);
    }


    /**
     * Sets up listeners.
     */

    protected void setListeners() {
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.dwimage);

                if (mGps.lastKnownLocation() != null){

                    mBllFac.uploadImage(image, mGps.lastKnownLocation(), new Callback(){
                        @Override
                        public void onTaskCompleted(boolean done) {
                            mImgViewUploaded.setImageBitmap(image);

                        }
                    });

                }
            }
        });
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
        new Thread(new ImgProcessingRunnable()).start();

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

    private class ImgProcessingRunnable implements Runnable {


        public void run() {
            ImgProcessing imgProc = new ImgProcessing();

            while (mIsRunning) {
                Circle circle = imgProc.getCircle(mRgba, mHSV, mThresholded, mThresholded2, mArray255, mDistance);
                if (circle != null) {
                    Log.d("myRunnable ", "diameter " + Double.toString(circle.getDiameter()));
                    Log.d("myRunnable ", "center " + circle.getCenter().toString());
                }
                try {
                    Thread.sleep(THREAD_SLEEP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
