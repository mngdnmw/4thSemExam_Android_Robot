package mafioso.so.so.android_robot.bll;

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


