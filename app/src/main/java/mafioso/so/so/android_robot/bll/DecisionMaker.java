package mafioso.so.so.android_robot.bll;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import mafioso.so.so.android_robot.shared.Callback;
import mafioso.so.so.android_robot.shared.Circle;

public class DecisionMaker {

    private static String TAG = "AAR - DecisionMaker";
    private Point crntPoint, lastPoint;
    private double crntDiameter, lastDiameter;
    private static Point NO_POINT = new Point(-1, -1);
    private static double NO_DIAMETER = -1.0;
    private Mat currentFrame;
    private int width, height;
    private int range;
   private int maxTicks = 5;
   private int ticks;

    public enum Command {
        QUIT, OBJECT_FOUND, TAKE_PICTURE, CHANGEDIR, ROAM, STOP, FORWARD, LEFT, RIGHT, BACK, WAIT, DO_NOTHING
    }

    private Command lastCommand;
    public Command command;
    private BllFacade bllFadace;

    public DecisionMaker(int width, int height, BllFacade bllFacade) {
        command = Command.DO_NOTHING;
        this.width = width;
        this.height = height;
        crntDiameter = NO_DIAMETER;
        crntPoint = NO_POINT;
        this.bllFadace = bllFacade;

        if (this.width <= this.height) {
            range = this.height / 3;
        } else {
            range = this.width / 3;
        }

    }

    public Mat getCurrentFrame() {
        return currentFrame;
    }

    public void MakeDecision(Circle circle, Mat currentFrame) {
        lastPoint = crntPoint;
        lastDiameter = crntDiameter;
        lastCommand = command;
        this.currentFrame = currentFrame;
        String debug = "Making Decision";
        //No circle found in picture.
        if(ticks != 0){
            if(ticks == maxTicks){
                command = Command.CHANGEDIR;
            }
            else{
                command = Command.ROAM;
            }
            ticks--;
        }
        if (circle != null && circle.getDiameter() ==0) {
            crntPoint = circle.getCenter();
            crntDiameter = circle.getDiameter();
            debug += " ObjectFound ";
            if (lastPoint == NO_POINT && lastDiameter == NO_DIAMETER) {

                command = Command.STOP;
            } else if (crntPoint.x <= ((width / 2) - (range))) {

                command = Command.LEFT;
            } else if (crntPoint.x >= ((width / 2) + (range))) {

                command = Command.RIGHT;
            }
            else if(crntPoint.y >= (height/2) + range){
                command = Command.BACK;
            }
            else if(crntPoint.y <= (height/2) - range){
                command = Command.FORWARD;
            }
            else if (lastCommand == Command.OBJECT_FOUND && ticks == 0) {

                debug += ": Take Picture";
                command = Command.TAKE_PICTURE;
                PictureTaken();
            }
            /*else if (lastCommand == Command.TAKE_PICTURE) {
                bllFadace.getDebugger().setDebug("ChangeDirection");
                command = Command.CHANGEDIR;
            } */
            else if (ticks > 0) {
                command = Command.OBJECT_FOUND;
            }
            else{
                command = Command.CHANGEDIR;
            }

        }
        //Circle not found.
        else {
            debug+= " NoObject";
           // if( pointRangeCheck(lastPoint, crntPoint))
            crntPoint = NO_POINT;
            crntDiameter = NO_DIAMETER;
            //if there was no circle last picture, we once again continue to roam
            if (lastPoint == crntPoint && lastDiameter == crntDiameter) {
                command = Command.ROAM;
            }
            //if there was an object with last decision, we will check what the last command was.
            //if it was going left we assume the object has left the frame since then and we would like to find it again
            else if (lastCommand == Command.LEFT) {
                command = Command.RIGHT;
            } else if (lastCommand == Command.RIGHT) {
                command = Command.LEFT;
            } else if (lastCommand == Command.BACK) {
                command = Command.FORWARD;
            } else if (lastCommand == Command.FORWARD) {
                command = Command.BACK;
            }
            else{
                command = Command.ROAM;
            }
        }

  /*       else{
             if(crntPoint.x <= ((width/2)-(range*2))){
                 command = Command.LEFT;

                 Log.d(TAG, "MakeDecision: Left " + crntPoint.x);
             }
             else if(crntPoint.x >= ((width/2)+(range*2))){
                 command = Command.RIGHT;
                 Log.d(TAG, "MakeDecision: Right " + crntPoint.x);
             }

         }
*/      if(ticks != 0){
        ticks--;}
        debug += " Ticks: " +ticks;
        bllFadace.getDebugger().setDebug(debug);
    }
    public void PictureTaken(){
        ticks = maxTicks;
    }


    private boolean pointRangeCheck(Point lastPoint, Point crntPoint) {
        if ((lastPoint.x >= crntPoint.x - range && lastPoint.x <= crntPoint.x + range)
                && (lastPoint.y >= crntPoint.y - range && lastPoint.y <= crntPoint.y + range)) {
            return true;
        } else {
            return false;
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
            case RIGHT:
                return "Right";
            default:
                return "";
        }
    }
    public Command getCommand() {
        return command;
    }
}


