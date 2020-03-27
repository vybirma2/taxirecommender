package charging;

import domain.states.TaxiGraphState;
import utils.Utils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class DistanceChargingStationStateOrder implements ChargingStationStateOrder {

    private HashMap<Integer, HashMap<Integer, Double>> chargingStationDistances;
    private HashMap<Integer, List<Integer>> orders;
    private Set<Integer> nodes;



    public DistanceChargingStationStateOrder(HashMap<Integer, HashMap<Integer, Double>> chargingStationDistances, Set<Integer> nodes) {
        this.chargingStationDistances = chargingStationDistances;
        this.nodes = nodes;
        computeOrders();
    }

    @Override
    public List<Integer> get(TaxiGraphState state, int numOfChargingStations) {

        return this.orders.get(state.getNodeId())
                .stream()
                .limit(numOfChargingStations)
                .collect(Collectors.toList());
    }


    private void computeOrders(){
        this.orders = new HashMap<>();

        Set<Integer> chargingStations = chargingStationDistances.keySet();


        for (Integer node : nodes){
            List<Integer> list = chargingStations
                                        .stream()
                                        .map(station -> new TripToChargingStation(node, station))
                                        .sorted(Utils.tripToChargingStationComparator)
                                        .limit(Utils.NUM_OF_BEST_CHARGING_STATIONS_TO_GO_TO)
                                        .map(TripToChargingStation::getChargingStation)
                                        .collect(Collectors.toList());

            orders.put(node, list);
        }
    }

}
