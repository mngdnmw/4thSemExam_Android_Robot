package mafioso.so.so.android_robot.dal;

import android.graphics.Bitmap;
import android.location.Location;

import mafioso.so.so.android_robot.shared.Callback;

public class DalFacade {
    private Dao mDao;
    private RobotConnection mRobotCon;
    public DalFacade() {
        mDao = new Dao();
        mRobotCon = new RobotConnection();
    }

    public Dao getmDao() {
        return mDao;
    }

    public RobotConnection getmRobotCon() {
        return mRobotCon;
    }
}
