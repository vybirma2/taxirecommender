package evaluation;

public class SimulationTaxiTrip {
    private double distance;
    private final long tripLength;
    private final int tripEnergyConsumption;

    private final int fromEnvironmentNode;
    private final int toEnvironmentNode;

    public SimulationTaxiTrip(double distance, long tripLength, int tripEnergyConsumption, int fromEnvironmentNode, int toEnvironmentNode) {
        this.distance = distance;
        this.tripLength = tripLength;
        this.tripEnergyConsumption = tripEnergyConsumption;
        this.fromEnvironmentNode = fromEnvironmentNode;
        this.toEnvironmentNode = toEnvironmentNode;
    }


    public double getDistance() {
        return distance;
    }

    public long getTripLength() {
        return tripLength;
    }

    public int getTripEnergyConsumption() {
        return tripEnergyConsumption;
    }

    public int getFromEnvironmentNode() {
        return fromEnvironmentNode;
    }

    public int getToEnvironmentNode() {
        return toEnvironmentNode;
    }
}
