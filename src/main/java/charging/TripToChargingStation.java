package charging;

import domain.states.TaxiGraphState;
import utils.DistanceGraphUtils;

import javax.crypto.spec.PSource;

public class TripToChargingStation {

    private TaxiGraphState state;
    private ChargingStation chargingStation;
    private double distance;

    public TripToChargingStation(TaxiGraphState state, ChargingStation chargingStation) {
        this.state = state;
        this.chargingStation = chargingStation;
        this.distance = DistanceGraphUtils.getDistanceBetweenNodes(state.getNodeId(), chargingStation.getRoadNode().getId());
    }

    public TaxiGraphState getState() {
        return state;
    }

    public ChargingStation getChargingStation() {
        return chargingStation;
    }

    public double getDistance() {
        return distance;
    }
}
