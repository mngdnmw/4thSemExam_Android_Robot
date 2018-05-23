package mafioso.so.so.android_robot.bll.Behaviours;

        import mafioso.so.so.android_robot.bll.BllFacade;
        import mafioso.so.so.android_robot.bll.DecisionMaker;
        import mafioso.so.so.android_robot.bll.IBehaviour;

public class QuitBehaviour implements IBehaviour {
    private boolean suppressed;
    private BllFacade bllFacade;
    public QuitBehaviour(BllFacade bllFacade)
    {
        this.bllFacade = bllFacade;
        suppressed = false;
    }
    @Override
    public boolean takeControl() {
        if(bllFacade.getDecisionMaker().command == DecisionMaker.Command.QUIT) {
            return true;
        }
        return false;
    }

    @Override
    public void action() {
        bllFacade.getmDalFac().getmRobotCon().sendCommand((DecisionMaker.getStringCommand(DecisionMaker.Command.QUIT)));

        while(!suppressed){
            Thread.yield();
        }
    }

    @Override
    public void suppress() {
        suppressed = true;
    }
}

