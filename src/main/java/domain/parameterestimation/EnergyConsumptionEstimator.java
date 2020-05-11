package domain.parameterestimation;

import static domain.utils.DistanceGraphUtils.getDistanceBetweenNodes;
import static domain.utils.Utils.ELECTRIC_VEHICLE_DRIVING_RANGE;

/**
 * Methods used to get energy consumption of taxi trip
 */
public class EnergyConsumptionEstimator {


    // TODO - get some good energy consumption estimate
    public static int getMovingEnergyConsumption(int fromNodeId, int toNodeId){
        double distance = getDistanceBetweenNodes(fromNodeId, toNodeId);
        return - (int)Math.ceil((distance/ ELECTRIC_VEHICLE_DRIVING_RANGE) * 100);
    }


    public static int getMovingEnergyConsumption(double distance){
        return - (int)Math.ceil((distance/ ELECTRIC_VEHICLE_DRIVING_RANGE) * 100);
    }


    public static int getActionEnergyConsumption(int fromNodeId, int toNodeId) {
        return getMovingEnergyConsumption(fromNodeId, toNodeId);
    }


    public static int getEnergyConsumption(double distance) {
        return getMovingEnergyConsumption(distance);
    }
}
