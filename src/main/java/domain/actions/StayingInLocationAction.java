package domain.actions;

/**
 * Action of staying in location
 */
public class StayingInLocationAction extends MeasurableAction {


    public StayingInLocationAction(int actionId, int fromNodeId, int toNodeId, int length) {
        super(actionId, fromNodeId, toNodeId, length);
    }


    @Override
    public int getConsumption() {
        return 0;
    }

    @Override
    public double getReward() {
        return 0;
    }


    @Override
    public MeasurableAction copy() {
        return new StayingInLocationAction(this.getActionId(), this.getFromNodeId(), this.getToNodeId(),
                this.getLength());
    }


    @Override
    public String toString() {
        return "StayingInLocationAction: " + super.toString();
    }
}
