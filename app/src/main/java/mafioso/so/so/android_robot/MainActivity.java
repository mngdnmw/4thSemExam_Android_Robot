package mafioso.so.so.android_robot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Connection_to_OpenCv";
    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket socket = new Socket();

    private EditText txtIP;
    private TextView txtMessage;
    private Button btnConnect;
    private String host;

    BaseLoaderCallback mCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case BaseLoaderCallback.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    break;
                }
                default:
                {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0,this,mCallBack); // Start OpenCV

        txtIP = findViewById(R.id.txtIP);
        txtIP.setText("192.168.43.174");
        btnConnect = findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                threadConnection( txtIP.getText().toString());

            }
        });
    }

    private void sendCommand(String command) {
        try {

            dos.writeUTF(command);dos.flush();


        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    private void threadConnection(String ip){
        host = ip;
        new Thread(){
            public void run() {
                try {
                    if(!socket.isConnected()) {
                        socket = new Socket(host, 5969);
//			Socket socket = new Socket("127.0.0.1", 19231);//for mocking
                        dis = new DataInputStream(socket.getInputStream());
                        dos = new DataOutputStream(socket.getOutputStream());
                    }
                    sendCommand("We Are Here");
                    //pw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
