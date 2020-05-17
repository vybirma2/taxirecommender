package domain.parameterestimation;

import static domain.utils.DistanceGraphUtils.getDistanceBetweenEnvironmentNodes;
import static domain.utils.Utils.ELECTRIC_VEHICLE_DRIVING_RANGE;

/**
 * Methods used to get energy consumption of taxi trip
 */
public class EnergyConsumptionEstimator {

    public static int getActionEnergyConsumption(int fromNodeId, int toNodeId) {
        double distance = getDistanceBetweenEnvironmentNodes(fromNodeId, toNodeId);
        return getEnergyConsumption(distance);
    }

    public static int getEnergyConsumption(double distance) {
        return - (int)Math.ceil((distance/ ELECTRIC_VEHICLE_DRIVING_RANGE) * 100);
    }
}
