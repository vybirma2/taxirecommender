package domain.charging;

import domain.utils.DistanceGraphUtils;

/**
 * Class representing trip from some node to some domain.charging station
 */
public class TripToChargingStation {

    private int fromNode;
    private int chargingStation;
    private double distance;


    public TripToChargingStation(Integer state, Integer chargingStation) {
        this.fromNode = state;
        this.chargingStation = chargingStation;
        if (!state.equals(chargingStation)){
            this.distance = DistanceGraphUtils.getDistanceSpeedPairOfPath(DistanceGraphUtils.aStar(fromNode, chargingStation)).getDistance();
        }else {
            distance = 0;
        }
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
