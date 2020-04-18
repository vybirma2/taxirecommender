package domain.actions;

import parameterestimation.EnergyConsumptionEstimator;


import static utils.DistanceGraphUtils.getTripTime;

/**
 * Action of going to nex location
 */
public class NextLocationAction extends MeasurableAction {


    public NextLocationAction(int actionId, int fromNodeId, int toNodeId) {
        super(actionId, fromNodeId, toNodeId, getTripTime(fromNodeId, toNodeId));
    }

    @Override
    public int getConsumption() {
        return EnergyConsumptionEstimator.getActionEnergyConsumption(getFromNodeId(), getToNodeId());
    }

    @Override
    public double getReward() {
        return 0;
    }


    @Override
    public MeasurableAction copy() {
        return new NextLocationAction(this.getActionId(), this.getFromNodeId(), this.getToNodeId());
    }

    @Override
    public String toString() {
        return "NextLocationAction{}" + super.toString();
    }
}
