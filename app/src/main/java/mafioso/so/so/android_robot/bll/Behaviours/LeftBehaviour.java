package mafioso.so.so.android_robot.bll.Behaviours;

import mafioso.so.so.android_robot.bll.BllFacade;
import mafioso.so.so.android_robot.bll.DecisionMaker;
import mafioso.so.so.android_robot.bll.IBehaviour;
import mafioso.so.so.android_robot.dal.RobotConnection;

public class LeftBehaviour implements IBehaviour {
    private boolean suppressed;
    private BllFacade bllFacade;
    public LeftBehaviour(BllFacade bllFacade)
    {
        this.bllFacade = bllFacade;
        suppressed = false;
    }
    @Override
    public boolean takeControl() {
        if(bllFacade.getDecisionMaker().command == DecisionMaker.Command.LEFT) {
            return true;
        }
        return false;
    }

    @Override
    public void action() {
        bllFacade.getmDalFac().getmRobotCon().sendCommand((DecisionMaker.getStringCommand(DecisionMaker.Command.LEFT)));
        while(!suppressed){
            Thread.yield();
        }
    }

    @Override
    public void suppress() {
        suppressed = true;
    }
}