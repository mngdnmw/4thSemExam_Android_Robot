package mafioso.so.so.android_robot.bll.Behaviours;

import mafioso.so.so.android_robot.bll.BllFacade;
import mafioso.so.so.android_robot.bll.DecisionMaker;
import mafioso.so.so.android_robot.bll.IBehaviour;
import mafioso.so.so.android_robot.dal.RobotConnection;

public class DirectionalControl implements IBehaviour {
    private boolean suppressed;
    private BllFacade bllFacade;
    public DirectionalControl(BllFacade bllFacade)
    {
        this.bllFacade = bllFacade;
        suppressed = false;
    }
    @Override
    public boolean takeControl() {
        if(bllFacade.getDecisionMaker().command == DecisionMaker.Command.FORWARD ||
        bllFacade.getDecisionMaker().command == DecisionMaker.Command.BACK ||
        bllFacade.getDecisionMaker().command == DecisionMaker.Command.LEFT ||
        bllFacade.getDecisionMaker().command == DecisionMaker.Command.RIGHT
                ) {

            bllFacade.getDebugger().setDebug("Forward");
            return true;
        }
        return false;
    }

    @Override
    public void action() {
        bllFacade.getmDalFac().getmRobotCon().sendCommand((DecisionMaker.getStringCommand(bllFacade.getDecisionMaker().command)));

    }

    @Override
    public void suppress() {
        suppressed = true;
    }
}