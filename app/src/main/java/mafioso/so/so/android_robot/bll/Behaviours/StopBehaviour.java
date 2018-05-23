package mafioso.so.so.android_robot.bll.Behaviours;

import mafioso.so.so.android_robot.bll.DecisionMaker;
import mafioso.so.so.android_robot.bll.IBehaviour;
import mafioso.so.so.android_robot.dal.RobotConnection;

public class StopBehaviour implements IBehaviour {
    private boolean suppressed;
    private RobotConnection robotConnection;
    private DecisionMaker decMaker;
    public StopBehaviour(RobotConnection robotConnection,DecisionMaker decMaker){
        suppressed = false;
    }
    @Override
    public boolean takeControl() {
        if(decMaker.command == DecisionMaker.Command.STOP) {
            return true;
        }
        return false;
    }

    @Override
    public void action() {
        robotConnection.sendCommand(DecisionMaker.getStringCommand(DecisionMaker.Command.STOP));
        while(!suppressed){
            Thread.yield();
        }
    }

    @Override
    public void suppress() {
        suppressed = true;
    }
}