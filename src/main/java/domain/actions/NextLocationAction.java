package domain.actions;

import domain.parameterestimation.EnergyConsumptionEstimator;
import domain.utils.DistanceSpeedPairTime;

import static domain.utils.DistanceGraphUtils.*;

/**
 * Action of going to nex location
 */
public class NextLocationAction extends MeasurableAction {

    private int consumption;

    public NextLocationAction(int actionId, int fromNodeId, int toNodeId) {
        super(actionId, fromNodeId, toNodeId, getPathTime(fromNodeId, toNodeId));
        if (fromNodeId != toNodeId){
            DistanceSpeedPairTime distanceSpeedPairTime = getDistancesAndSpeedBetweenNodes(fromNodeId, toNodeId);
            consumption = EnergyConsumptionEstimator.getEnergyConsumption(distanceSpeedPairTime.getDistance());
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
    public String toString() {
        return "NextLocationAction: " + super.toString();
    }
}
