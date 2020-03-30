package charging;

import utils.DistanceGraphUtils;

/**
 * Class representing trip from some node to some charging station
 */
public class TripToChargingStation {

    private int fromNode;
    private int chargingStation;
    private double distance;


    public TripToChargingStation(Integer state, Integer chargingStation) {
        this.fromNode = state;
        this.chargingStation = chargingStation;
        this.distance = DistanceGraphUtils.getDistanceBetweenNodes(fromNode, chargingStation);
    }


    public Integer getState() {
        return fromNode;
    }


    public Integer getChargingStation() {
        return chargingStation;
    }


    public double getDistance() {
        return distance;
    }
}
