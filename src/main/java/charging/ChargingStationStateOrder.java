package charging;

import domain.states.TaxiGraphState;

import java.util.List;

/**
 * Interface for classes defining different charging station orders for choosing set of the best
 * charging stations to choose from in concrete state of planning.
 */
public interface ChargingStationStateOrder {


    /**
     * @param state current state for which list of charging stations is requested
     * @param numOfChargingStations number of the best charging stations according to the defined order to return
     * @return list of numOfChargingStations best charging stations - their ids
     */
    List<Integer> get(TaxiGraphState state, int numOfChargingStations);
}
