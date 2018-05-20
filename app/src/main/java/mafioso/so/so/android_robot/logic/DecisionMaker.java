package mafioso.so.so.android_robot.logic;

import android.util.Log;

import org.opencv.core.Point;

public class DecisionMaker {

    private Point crntPoint, lastPoint;
    private double crntDiamaeter, lastDiameter;
    private int width, height;
    private int range;
    public enum Command {
        QUIT,TAKE_PICTURE, CHANGEDIR, ROAM, STOP, FORWARD, LEFT, RIGHT, BACK, WAIT, DO_NOTHING
    }

    public Command command;

    public DecisionMaker(int width, int height) {
        this.width = width;
        this.height = height;
        if(this.width <= this.height){
            range = this.height/20;
        }
        else{
            range = this.width/20;
        }
    }

    public void MakeDecision(float[] data2){

        if (data2.length > 0) {
            // Points to the first element and reads the whole thing into data2
            //for (int i = 0; i < data2.length; i = i + 3) {
            // Draw the circles detected
            Point center = new Point(data2[0], data2[0 + 1]);
            Log.d("center_point ", center.toString());
            // (InputOutputArray img, Point center, Size axes, double angle, double startAngle, double endAngle, const Scalar& color, int thickness=1, int lineType=LINE_8, int shift=0 )
            // axes – Half of the size of the ellipse main axes
            // Imgproc.ellipse(rgba, center, new Size((double) data2[i + 2], (double) data2[i + 2]), 0, 0, 360, new Scalar(255, 0, 255), 4, 8, 0);

            double diameter = (double) data2[0 + 2] * 2;
            //}
        }
    }

    /*
     * Behaviors: Quit, SonicAvoidance (Done), Change Direction, Roam Stop Forward
     * Left Right Back Wait
     */

    public static String getStringCommand(Command command) {

        switch (command) {
            case QUIT:
                return "Quit";
            case CHANGEDIR:
                return "ChangeDirection";
            case ROAM:
                return "Roam";
            case STOP:
                return "Stop";
            case FORWARD:
                return "Forward";
            case LEFT:
                return "Left";
            case BACK:
                return "Back";
            default:
                return "";
        }
    }

    public Command getCommand() {
        return command;
    }


}


