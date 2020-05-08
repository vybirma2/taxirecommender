package domain.actions;

/**
 * Action of staying in location
 */
public class StayingInLocationAction extends MeasurableAction {


    public StayingInLocationAction(int actionId, int fromNodeId, int toNodeId, int length) {
        super(actionId, fromNodeId, toNodeId, length);
    }


    @Override
    public int getRestConsumption() {
        return 0;
    }

    @Override
    public double getReward() {
        return 0;
    }

    @Override
    public void setRestConsumption(int restConsumption) {

    }


    @Override
    public MeasurableAction copy() {
        return new StayingInLocationAction(this.getActionId(), this.getFromNodeId(), this.getToNodeId(),
                this.getTimeToFinish());
    }


    @Override
    public String toString() {
        return "StayingInLocationAction: " + super.toString();
    }
}
