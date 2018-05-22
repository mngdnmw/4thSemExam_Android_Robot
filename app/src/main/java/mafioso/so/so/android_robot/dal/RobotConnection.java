package mafioso.so.so.android_robot.dal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class RobotConnection {

private Socket socket;
private DataOutputStream dos;
private DataInputStream dis;


    public void threadConnection(final String host){
        new Thread() {
            public void run() {
                try {
                        socket = new Socket(host, 5969);
                        socket.setKeepAlive(true);

                        dis = new DataInputStream(socket.getInputStream());
                        dos = new DataOutputStream(socket.getOutputStream());


                }
                catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
        public void sendCommand(final String command) {
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
        public boolean isConnected(){
            if(socket ==null){
                return false;
            }
            else    {
            return socket.isConnected();
            }
        }

    public DataInputStream getDis() {
        return dis;
    }

    public DataOutputStream getDos() {
        return dos;
    }
}

