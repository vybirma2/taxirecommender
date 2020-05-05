package domain.charging;

import domain.states.TaxiState;

import java.util.List;

/**
 * Interface for classes defining different domain.charging station orders for choosing set of the best
 * domain.charging stations to choose from in concrete state of planning.
 */
public interface ChargingStationStateOrder {


    /**
     * @param state current state for which list of domain.charging stations is requested
     * @param numOfChargingStations number of the best domain.charging stations according to the defined order to return
     * @return list of numOfChargingStations best domain.charging stations - their ids
     */
    List<Integer> get(TaxiState state, int numOfChargingStations);
}
