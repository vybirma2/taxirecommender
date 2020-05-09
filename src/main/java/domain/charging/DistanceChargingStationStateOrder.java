package domain.charging;

import domain.parameterestimation.ParameterEstimator;
import domain.states.TaxiState;
import domain.utils.DistanceSpeedPairTime;
import domain.utils.Utils;
import jdk.jshell.execution.Util;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class receiving distances from individual nodes to all domain.charging stations and defining domain.charging station order
 * according to the distance from given state - node.
 */
public class DistanceChargingStationStateOrder implements ChargingStationStateOrder {

    private HashMap<Integer, HashMap<Integer, DistanceSpeedPairTime>> chargingStationDistanceSpeedTime;
    private HashMap<Integer, List<Integer>> orders;
    private Set<Integer> nodes;


    public DistanceChargingStationStateOrder(HashMap<Integer, HashMap<Integer, DistanceSpeedPairTime>> chargingStationDistanceSpeedTime, Set<Integer> nodes) {
        this.chargingStationDistanceSpeedTime = chargingStationDistanceSpeedTime;
        this.nodes = nodes;
        computeOrders();
    }


    @Override
    public List<Integer> get(TaxiState state, int numOfChargingStations) {
        return this.orders.get(state.getNodeId())
                .stream()
                .limit(numOfChargingStations)
                .collect(Collectors.toList());
    }


    private void computeOrders(){
        Set<Integer> chargingStations = chargingStationDistanceSpeedTime.keySet();
        File file = new File("data/programdata/chargingOrders" + Utils.DATA_SET_NAME + Utils.ENVIRONMENT +
                "" + Utils.NUM_OF_CLUSTERS + Utils.ONE_GRID_CELL_WIDTH + Utils.ONE_GRID_CELL_HEIGHT + ".fst");
        if (!file.exists()) {
            this.orders = new HashMap<>();

            for (Integer node : nodes){
                List<Integer> list = chargingStations
                        .stream()
                        .map(station -> new TripToChargingStation(node, station))
                        .sorted(Utils.tripToChargingStationComparator)
                        .limit(Utils.NUM_OF_BEST_CHARGING_STATIONS_TO_GO_TO)
                        .map(TripToChargingStation::getChargingStation)
                        .collect(Collectors.toList());

                orders.put(node, list);

                FSTObjectOutput out;
                try {
                    out = new FSTObjectOutput(new FileOutputStream(file));
                    out.writeObject(orders);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            FSTObjectInput in;
            try {
                in = new FSTObjectInput(new FileInputStream(file));
                orders = (HashMap<Integer, List<Integer>>) in.readObject();
                in.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
