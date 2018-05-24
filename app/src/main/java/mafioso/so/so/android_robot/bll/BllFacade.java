package mafioso.so.so.android_robot.bll;

import android.content.Context;

import mafioso.so.so.android_robot.bll.Behaviours.BackBehaviour;
import mafioso.so.so.android_robot.bll.Behaviours.ChangeDirectionBehaviour;
import mafioso.so.so.android_robot.bll.Behaviours.ForwardBehaviour;
import mafioso.so.so.android_robot.bll.Behaviours.LeftBehaviour;
import mafioso.so.so.android_robot.bll.Behaviours.QuitBehaviour;
import mafioso.so.so.android_robot.bll.Behaviours.RightBehaviour;
import mafioso.so.so.android_robot.bll.Behaviours.RoamBehaviour;
import mafioso.so.so.android_robot.bll.Behaviours.StopBehaviour;
import mafioso.so.so.android_robot.bll.Behaviours.TakePictureBehaviour;
import mafioso.so.so.android_robot.dal.DalFacade;

public class BllFacade {

    private DalFacade mDalFac;
    private DecisionMaker decisionMaker;
    private ImgProcessing imgProcessing;
    private GpsLocation gpsLocation;
    private Arbitrator arb;
    private PhotoUploadedNotifier photoUploadedNotifier;
    private DebugLogger debugger;

    public BllFacade(Context context) {
        mDalFac = new DalFacade();
        imgProcessing = new ImgProcessing();
        gpsLocation = new GpsLocation(context);
        photoUploadedNotifier = new PhotoUploadedNotifier();
        debugger = new DebugLogger();
        initializeArbitrator();
    }

    private void initializeArbitrator(){
       /* IBehaviour roam = new RoamBehaviour(this);
        IBehaviour back = new BackBehaviour(this);
        IBehaviour changeDir = new ChangeDirectionBehaviour(this);
        IBehaviour forward = new ForwardBehaviour(this);
        IBehaviour left = new LeftBehaviour(this);
        IBehaviour right = new RightBehaviour(this);
        IBehaviour stop = new StopBehaviour(this);
        */
        IBehaviour takePicture = new TakePictureBehaviour(this);
        //IBehaviour quit = new QuitBehaviour(this);
        //        IBehaviour[] behaviours = {stop, right,left,forward,changeDir,back,roam,takePicture,quit};
           IBehaviour[] behaviours = {takePicture};
        arb = new Arbitrator(behaviours);
    }
    public GpsLocation getGpsLocation() {
        return gpsLocation;
    }

    public DebugLogger getDebugger() {
        return debugger;
    }

    public ImgProcessing getImgProcessing() {
        return imgProcessing;
    }

    public PhotoUploadedNotifier getPhotoUploadedNotifier() {
        return photoUploadedNotifier;
    }

    public void setDecisionMaker(int width, int height) {
        this.decisionMaker = new DecisionMaker(width, height, this);
    }

    public DecisionMaker getDecisionMaker() {
        return decisionMaker;
    }

    public DalFacade getmDalFac(){
        return mDalFac;
    }


    public void startAbitrator(){
        threadArbitrator(arb);
    }
    private void threadArbitrator(final Arbitrator arbitrator) {
        new Thread() {
            public void run() {
                arbitrator.go();
            }
        }.start();

    }
}
