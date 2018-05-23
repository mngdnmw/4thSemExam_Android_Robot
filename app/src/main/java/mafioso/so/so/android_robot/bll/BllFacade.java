package mafioso.so.so.android_robot.bll;

import android.graphics.Bitmap;
import android.location.Location;
import mafioso.so.so.android_robot.dal.DalFacade;
import mafioso.so.so.android_robot.dal.RobotConnection;
import mafioso.so.so.android_robot.shared.Callback;

public class BllFacade {

    private DalFacade mDalFac;
    private RobotConnection mRobotConnection;
    private DecisionMaker decisionMaker;
    public BllFacade() {
        mDalFac = new DalFacade();
        mRobotConnection = new RobotConnection();
    }

    public void setDecisionMaker(int width, int height) {
        this.decisionMaker = new DecisionMaker(width, height, mRobotConnection);
    }

    public DecisionMaker getDecisionMaker() {
        return decisionMaker;
    }

    public DalFacade getmDalFac(){
        return mDalFac;
    }

    public RobotConnection getRobotConnection() {
        return mRobotConnection;
    }
}
