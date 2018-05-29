package mafioso.so.so.android_robot.dal;



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
