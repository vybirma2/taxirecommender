package domain.actions;


import domain.TaxiRewardFunction;
import domain.parameterestimation.EnergyConsumptionEstimator;

/**
 * Action of picking up passenger
 */
public class PickUpPassengerAction extends MeasurableAction  {

    private final int currentNodeId;
    private final double distance;
    private int consumption;

    public PickUpPassengerAction(int currentNodeId, double distance, int actionId, int fromNodeId, int toNodeId, int length) {
        super(actionId, fromNodeId, toNodeId, length);
        this.currentNodeId = currentNodeId;
        this.distance = distance;
        this.consumption = EnergyConsumptionEstimator.getEnergyConsumption(distance);
    }

    @Override
    public int getRestConsumption() {
        return consumption;
    }

    @Override
    public double getReward() {
        return TaxiRewardFunction.getTripReward(distance);
    }

    @Override
    public void setRestConsumption(int restConsumption) {
        this.consumption = restConsumption;
    }

    public int getCurrentNodeId() {
        return currentNodeId;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public MeasurableAction copy() {
        return new PickUpPassengerAction(getCurrentNodeId(), getDistance(), this.getActionId(), this.getFromNodeId(), this.getToNodeId(),
                this.getActionTime());
    }
}