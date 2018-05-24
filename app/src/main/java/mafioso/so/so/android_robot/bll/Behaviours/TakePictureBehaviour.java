package mafioso.so.so.android_robot.bll.Behaviours;

import mafioso.so.so.android_robot.bll.BllFacade;
import mafioso.so.so.android_robot.bll.DecisionMaker;
import mafioso.so.so.android_robot.bll.IBehaviour;
import mafioso.so.so.android_robot.shared.Callback;

public class TakePictureBehaviour implements IBehaviour {
    private boolean suppressed;


    BllFacade bllFacade;
    public TakePictureBehaviour(BllFacade bllFacade) {
        this.bllFacade = bllFacade;
        suppressed = false;
    }

    @Override
    public boolean takeControl() {
        if (bllFacade.getDecisionMaker().command == DecisionMaker.Command.TAKE_PICTURE) {
            bllFacade.getDecisionMaker().command= DecisionMaker.Command.DO_NOTHING;
            return true;
        }
        return false;
    }

    @Override
    public void action() {

        bllFacade.getmDalFac().getmDao().uploadImage(
                bllFacade.getImgProcessing().convertMatToBitmap(
                        bllFacade.getDecisionMaker().getCurrentFrame()),
                bllFacade.getGpsLocation().lastKnownLocation(),
                new Callback() {
            @Override
            public void onTaskCompleted(boolean done) {
                bllFacade.getPhotoUploadedNotifier().setUploaded(true);
            }
        });
        while(!suppressed){
            Thread.yield();
        }
    }
    @Override
    public void suppress() {
        suppressed = true;
    }
}
