package domain.actions;

import parameterestimation.EnergyConsumptionEstimator;


import static utils.DistanceGraphUtils.getTripTime;

/**
 * Action of going to nex location
 */
public class NextLocationAction extends MeasurableAction {


    public NextLocationAction(int actionId, int fromNodeId, int toNodeId, int timeStamp) {
        super(actionId, fromNodeId, toNodeId, timeStamp, getTripTime(fromNodeId, toNodeId),
                EnergyConsumptionEstimator.getActionEnergyConsumption(fromNodeId, toNodeId));
    }


    @Override
    public MeasurableAction copy() {
        return new NextLocationAction(this.getActionId(), this.getFromNodeId(), this.getToNodeId(), this.getTimeStamp());
    }
}
