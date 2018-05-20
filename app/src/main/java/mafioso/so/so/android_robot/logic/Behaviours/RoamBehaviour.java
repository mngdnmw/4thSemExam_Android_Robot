package mafioso.so.so.android_robot.logic.Behaviours;

import mafioso.so.so.android_robot.logic.IBehaviour;

public class RoamBehaviour implements IBehaviour {
    private boolean suppressed;

    public RoamBehaviour(){
        suppressed = false;
    }
    @Override
    public boolean takeControl() {
        return true;
    }

    @Override
    public void action() {

        while(!suppressed){
            Thread.yield();
        }
    }

    @Override
    public void suppress() {
        suppressed = true;
    }
}
