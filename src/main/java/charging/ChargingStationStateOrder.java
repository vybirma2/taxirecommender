package charging;

import domain.states.TaxiGraphState;

import java.util.List;

public interface ChargingStationStateOrder {
    List<Integer> get(TaxiGraphState state, int numOfChargingStations);
}
