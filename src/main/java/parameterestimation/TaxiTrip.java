package parameterestimation;

import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.actions.ActionUtils;
import domain.environmentrepresentation.EnvironmentNode;

import java.io.Serializable;
import java.util.Date;

public class TaxiTrip implements Comparable, Serializable {
    private String orderId;

    private double pickUpLongitude;
    private double pickUpLatitude;
    private double destinationLongitude;
    private double destinationLatitude;
    private double distance;
    private long tripLength;
    private double tripEnergyConsumption;

    private EnvironmentNode pickUpNode;
    private EnvironmentNode destinationNode;



    private Date startDate;
    private Date finishDate;


    public TaxiTrip(String orderId, double pickUpLongitude, double pickUpLatitude, double destinationLongitude,
                    double destinationLatitude, double distance, long tripLength ,EnvironmentNode pickUpNode,
                    EnvironmentNode destinationNode, Date startDate, Date finishDate) {
        this.orderId = orderId;
        this.pickUpLongitude = pickUpLongitude;
        this.pickUpLatitude = pickUpLatitude;
        this.destinationLongitude = destinationLongitude;
        this.destinationLatitude = destinationLatitude;
        this.distance = distance;
        this.tripLength = tripLength;
        this.pickUpNode = pickUpNode;
        this.destinationNode = destinationNode;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.tripEnergyConsumption = computeEnergyConsumption();
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
        return pickUpNode;
    }

    public void setPickUpNode(EnvironmentNode pickUpNode) {
        this.pickUpNode = pickUpNode;
    }

    public RoadNode getDestinationNode() {
        return destinationNode;
    }

    public void setDestinationNode(EnvironmentNode destinationNode) {
        this.destinationNode = destinationNode;
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

    public double getTripEnergyConsumption() {
        return tripEnergyConsumption;
    }

    private double computeEnergyConsumption(){
        return ActionUtils.getEnergyConsumption(this.distance);
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
    public int compareTo(Object o) {
        TaxiTrip otherTrip = (TaxiTrip) o;
        return Integer.compare(this.startDate.getHours() * 60 + this.startDate.getMinutes(),
                otherTrip.startDate.getHours()*60 + otherTrip.startDate.getMinutes());
    }
}

