package mafioso.so.so.android_robot.bll.Behaviours;

import mafioso.so.so.android_robot.bll.DecisionMaker;
import mafioso.so.so.android_robot.bll.IBehaviour;
import mafioso.so.so.android_robot.dal.RobotConnection;

public class BackBehaviour implements IBehaviour {
    private boolean suppressed;
    private RobotConnection robotConnection;
    private DecisionMaker decMaker;
    public BackBehaviour(RobotConnection robotConnection,DecisionMaker decMaker){
        suppressed = false;
    }
    @Override
    public boolean takeControl() {
        if(decMaker.command == DecisionMaker.Command.BACK) {
            return true;
        }
        return false;
    }

    @Override
    public void action() {
        robotConnection.sendCommand(DecisionMaker.getStringCommand(DecisionMaker.Command.BACK));
        while(!suppressed){
            Thread.yield();
        }
    }

    @Override
    public void suppress() {
        suppressed = true;
    }
}