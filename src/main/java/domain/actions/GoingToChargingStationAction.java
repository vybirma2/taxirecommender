package domain.actions;

import domain.parameterestimation.EnergyConsumptionEstimator;
import domain.utils.DistanceGraphUtils;

import static domain.utils.DistanceGraphUtils.*;

/**
 * Action of going to domain.charging station from some node in the Environment
 */
public class GoingToChargingStationAction extends MeasurableAction  {

    private int consumption;

    public GoingToChargingStationAction(int actionId, int fromNodeId, int toNodeId) {
        super(actionId, fromNodeId, toNodeId, getPathTime(fromNodeId, toNodeId));
        if (fromNodeId != toNodeId){
            consumption = EnergyConsumptionEstimator.getEnergyConsumption(DistanceGraphUtils.getDistanceSpeedPairOfPath(aStar(fromNodeId, toNodeId)).getDistance());
        } else {
            consumption = 0;
        }
    }


    @Override
    public int getRestConsumption() {
        return consumption;
    }

    @Override
    public double getReward() {
        return 0;
    }

    @Override
    public void setRestConsumption(int restConsumption) {
        this.consumption = restConsumption;
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
