package parameterestimation;

import domain.states.TaxiGraphState;

import static utils.DistanceGraphUtils.getDistanceBetweenNodes;
import static utils.Utils.CAR_FULL_BATTERY_DISTANCE;

/**
 * Methods used to get energy consumption of taxi trip
 */
public class EnergyConsumptionEstimator {


    // TODO - get some good energy consumption estimate
    public static int getMovingEnergyConsumption(int fromNodeId, int toNodeId){
        double distance = getDistanceBetweenNodes(fromNodeId, toNodeId);
        return - (int)Math.ceil((distance/CAR_FULL_BATTERY_DISTANCE) * 100);
    }


    public static int getMovingEnergyConsumption(double distance){
        return - (int)Math.ceil((distance/CAR_FULL_BATTERY_DISTANCE) * 100);
    }


    public static int getActionEnergyConsumption(TaxiGraphState state, int toNodeId, double actionTime) {
        return getMovingEnergyConsumption(state.getNodeId(), toNodeId);
    }


    public static int getEnergyConsumption(double distance) {
        return getMovingEnergyConsumption(distance);
    }
}