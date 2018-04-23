package mafioso.so.so.android_robot.gui.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import mafioso.so.so.android_robot.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Connection_to_OpenCv";
    private DataOutputStream dos;
    private DataInputStream dis;
    private Socket socket = new Socket();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start OpenCV
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mCallBack);


    }

    protected void loadUI(){

        final TextView txtIP = findViewById(R.id.txtIP);
        txtIP.setText("192.168.43.208");
        Button btnConnect = findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                threadConnection(txtIP.getText().toString());

            }
        });

    }

    BaseLoaderCallback mCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };


    /*
    * Connecting to robot in new thread.
    */
    private void threadConnection(final String host) {
        new Thread() {
            public void run() {
                try {
                    if (!socket.isConnected()) {
                        socket = new Socket(host, 5969);
                        dis = new DataInputStream(socket.getInputStream());
                        dos = new DataOutputStream(socket.getOutputStream());
                    }
                    sendCommand("We Are Here");
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
     * */
    private void sendCommand(String command) {
        try {

            dos.writeUTF(command);
            dos.flush();


        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
