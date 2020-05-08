package evaluation;

public class SimulationTaxiTrip {
    private double distance;
    private final long tripLength;
    private final int tripEnergyConsumption;

    private final int fromNode;
    private final int toNode;

    public SimulationTaxiTrip(double distance, long tripLength, int tripEnergyConsumption, int fromNode, int toNode) {
        this.distance = distance;
        this.tripLength = tripLength;
        this.tripEnergyConsumption = tripEnergyConsumption;
        this.fromNode = fromNode;
        this.toNode = toNode;
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

    public int getFromNode() {
        return fromNode;
    }

    public int getToNode() {
        return toNode;
    }
}
