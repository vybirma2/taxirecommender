package domain.actions;

import parameterestimation.EnergyConsumptionEstimator;
import static utils.DistanceGraphUtils.getTripTime;

/**
 * Action of going to charging station from some node in the Environment
 */
public class GoingToChargingStationAction extends MeasurableAction  {


    public GoingToChargingStationAction(int actionId, int fromNodeId, int toNodeId, int timeStamp) {
        super(actionId, fromNodeId, toNodeId, timeStamp, getTripTime(fromNodeId, toNodeId),
                EnergyConsumptionEstimator.getActionEnergyConsumption(fromNodeId, toNodeId));
    }




    @Override
    public MeasurableAction copy() {
        return new GoingToChargingStationAction(this.getActionId(), this.getFromNodeId(), this.getToNodeId(), this.getTimeStamp());
    }

}
