package parameterestimation;

import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.environmentrepresentation.EnvironmentNode;

import java.io.Serializable;
import java.util.Date;

/**
 * Class representing taxi trip parsed from historical trip data
 */
public class TaxiTrip implements Comparable<TaxiTrip>, Serializable {

    private String orderId;

    private double pickUpLongitude;
    private double pickUpLatitude;
    private double destinationLongitude;
    private double destinationLatitude;
    private double distance;
    private long tripLength;
    private int tripEnergyConsumption;

    private RoadNode pickUpRoadNode;
    private RoadNode destinationRoadNode;


    private EnvironmentNode fromEnvironmentNode;
    private EnvironmentNode toEnvironmentNode;


    private Date startDate;
    private Date finishDate;


    public TaxiTrip(String orderId, double pickUpLongitude, double pickUpLatitude, double destinationLongitude,
                    double destinationLatitude, double distance, long tripLength , RoadNode pickUpNode,
                    RoadNode destinationRoadNode, Date startDate, Date finishDate, EnvironmentNode fromNode, EnvironmentNode toNode) {
        this.orderId = orderId;
        this.pickUpLongitude = pickUpLongitude;
        this.pickUpLatitude = pickUpLatitude;
        this.destinationLongitude = destinationLongitude;
        this.destinationLatitude = destinationLatitude;
        this.distance = distance;
        this.tripLength = tripLength;
        this.pickUpRoadNode = pickUpNode;
        this.destinationRoadNode = destinationRoadNode;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.tripEnergyConsumption = computeEnergyConsumption();
        fromEnvironmentNode = fromNode;
        toEnvironmentNode = toNode;
    }


    public String getOrderId() {
        return orderId;
    }


    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


    public double getPickUpLongitude() {
        return pickUpLongitude;
    }


    public void setPickUpLongitude(double pickUpLongitude) {
        this.pickUpLongitude = pickUpLongitude;
    }


    public double getPickUpLatitude() {
        return pickUpLatitude;
    }


    public void setPickUpLatitude(double pickUpLatitude) {
        this.pickUpLatitude = pickUpLatitude;
    }


    public double getDestinationLongitude() {
        return destinationLongitude;
    }


    public void setDestinationLongitude(double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }


    public double getDestinationLatitude() {
        return destinationLatitude;
    }


    public void setDestinationLatitude(double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }


    public double getDistance() {
        return distance;
    }


    public void setDistance(double distance) {
        this.distance = distance;
    }


    public RoadNode getPickUpNode() {
        return pickUpRoadNode;
    }


    public void setPickUpNode(EnvironmentNode pickUpNode) {
        this.pickUpRoadNode = pickUpNode;
    }


    public RoadNode getDestinationRoadNode() {
        return destinationRoadNode;
    }


    public void setDestinationRoadNode(EnvironmentNode destinationRoadNode) {
        this.destinationRoadNode = destinationRoadNode;
    }


    public RoadNode getPickUpRoadNode() {
        return pickUpRoadNode;
    }

    public EnvironmentNode getFromEnvironmentNode() {
        return fromEnvironmentNode;
    }

    public EnvironmentNode getToEnvironmentNode() {
        return toEnvironmentNode;
    }

    public Date getStartDate() {
        return startDate;
    }


    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }


    public Date getFinishDate() {
        return finishDate;
    }


    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
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


    public void setFromEnvironmentNode(EnvironmentNode fromEnvironmentNode) {
        this.fromEnvironmentNode = fromEnvironmentNode;
    }

    public void setToEnvironmentNode(EnvironmentNode toEnvironmentNode) {
        this.toEnvironmentNode = toEnvironmentNode;
    }

    @Override
    public String toString() {
        return "TaxiTrip{" +
                "orderId='" + orderId + '\'' +
                ", pickUpLongitude=" + pickUpLongitude +
                ", pickUpLatitude=" + pickUpLatitude +
                ", destinationLongitude=" + destinationLongitude +
                ", destinationLatitude=" + destinationLatitude +
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

