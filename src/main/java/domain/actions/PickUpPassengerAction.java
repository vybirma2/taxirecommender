package domain.actions;


/**
 * Action of picking up passenger
 */
public class PickUpPassengerAction extends MeasurableAction  {

    private int consumption;

    public PickUpPassengerAction(int actionId, int fromNodeId, int toNodeId, int length, int consumption) {
        super(actionId, fromNodeId, toNodeId, length);
        this.consumption = consumption;
    }

    @Override
    public int getConsumption() {
        return consumption;
    }

    @Override
    public MeasurableAction copy() {
        return new PickUpPassengerAction(this.getActionId(), this.getFromNodeId(), this.getToNodeId(),
                this.getLength(), this.getConsumption());
    }
}
