package parameterestimation;

import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.environmentrepresentation.EnvironmentNode;

import java.io.Serializable;
import java.util.Date;

/**
 * Class representing taxi trip parsed from historical trip data
 */
public class TaxiTrip implements Comparable<TaxiTrip>, Serializable {

    private final double pickUpLongitude;
    private final double pickUpLatitude;
    private double distance;
    private final long tripLength;
    private final int tripEnergyConsumption;

    private final int fromEnvironmentNode;
    private final int toEnvironmentNode;


    private final Date startDate;
    private final Date finishDate;


    public TaxiTrip(double pickUpLongitude, double pickUpLatitude, double distance, long tripLength, Date startDate, Date finishDate, int fromNode, int toNode) {
        this.pickUpLongitude = pickUpLongitude;
        this.pickUpLatitude = pickUpLatitude;
        this.distance = distance;
        this.tripLength = tripLength;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.tripEnergyConsumption = computeEnergyConsumption();
        fromEnvironmentNode = fromNode;
        toEnvironmentNode = toNode;
    }


    public double getPickUpLongitude() {
        return pickUpLongitude;
    }

    public double getPickUpLatitude() {
        return pickUpLatitude;
    }


    public double getDistance() {
        return distance;
    }


    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getFromEnvironmentNode() {
        return fromEnvironmentNode;
    }

    public int getToEnvironmentNode() {
        return toEnvironmentNode;
    }

    public Date getStartDate() {
        return startDate;
    }

    public long getTripLength() {
        return tripLength;
    }


    public int getTripEnergyConsumption() {
        return tripEnergyConsumption;
    }


    private int computeEnergyConsumption(){
        return EnergyConsumptionEstimator.getEnergyConsumption(this.distance);
    }


    @Override
    public String toString() {
        return "TaxiTrip{" +
                "pickUpLongitude=" + pickUpLongitude +
                ", pickUpLatitude=" + pickUpLatitude +
                ", distance=" + distance +
                ", startDate=" + startDate +
                ", finishDate=" + finishDate +
                '}';
    }


    @Override
    public int compareTo(TaxiTrip o) {
        return Integer.compare(this.startDate.getHours() * 60 + this.startDate.getMinutes(),
                o.startDate.getHours()*60 + o.startDate.getMinutes());
    }
}

