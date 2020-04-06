package domain.actions;

/**
 * Action of staying in location
 */
public class StayingInLocationAction extends MeasurableAction {


    public StayingInLocationAction(int actionId, int fromNodeId, int toNodeId, int timeStamp, int length, int consumption) {
        super(actionId, fromNodeId, toNodeId, timeStamp, length, consumption);
    }


    @Override
    public MeasurableAction copy() {
        return new PickUpPassengerAction(this.getActionId(), this.getFromNodeId(), this.getToNodeId(), this.getTimeStamp(),
                this.getLength(), this.getConsumption());
    }
}
