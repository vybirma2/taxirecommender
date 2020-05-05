package domain.actions;

import domain.parameterestimation.EnergyConsumptionEstimator;
import static domain.utils.DistanceGraphUtils.getTripTime;

/**
 * Action of going to domain.charging station from some node in the Environment
 */
public class GoingToChargingStationAction extends MeasurableAction  {


    public GoingToChargingStationAction(int actionId, int fromNodeId, int toNodeId) {
        super(actionId, fromNodeId, toNodeId, getTripTime(fromNodeId, toNodeId));
    }


    @Override
    public int getConsumption() {
        return EnergyConsumptionEstimator.getActionEnergyConsumption(this.getFromNodeId(), this.getToNodeId());
    }

    @Override
    public double getReward() {
        return 0;
    }

    @Override
    public MeasurableAction copy() {
        return new GoingToChargingStationAction(this.getActionId(), this.getFromNodeId(), this.getToNodeId());
    }


    @Override
    public String toString() {
        return "GoingToChargingStationAction: " + super.toString();
    }
}
