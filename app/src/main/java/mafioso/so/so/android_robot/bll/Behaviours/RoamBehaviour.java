package mafioso.so.so.android_robot.bll.Behaviours;

import mafioso.so.so.android_robot.bll.BllFacade;
import mafioso.so.so.android_robot.bll.DecisionMaker;
import mafioso.so.so.android_robot.bll.IBehaviour;
import mafioso.so.so.android_robot.dal.RobotConnection;

public class RoamBehaviour implements IBehaviour {
    private boolean suppressed;
    private BllFacade bllFacade;
    public RoamBehaviour(BllFacade bllFacade)
    {
        this.bllFacade = bllFacade;
        suppressed = false;
    }
    @Override
    public boolean takeControl() {
            return true;

    }

    @Override
    public void action() {
        bllFacade.getmDalFac().getmRobotCon().sendCommand((DecisionMaker.getStringCommand(DecisionMaker.Command.ROAM)));
        while(!suppressed){
            Thread.yield();
        }
    }

    @Override
    public void suppress() {
        suppressed = true;
    }
}