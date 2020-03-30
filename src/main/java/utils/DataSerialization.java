package utils;

import charging.ChargingStation;
import domain.AllDistancesSpeedsPair;
import domain.actions.ChargingAction;
import domain.environmentrepresentation.EnvironmentNode;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static utils.DistanceGraphUtils.*;

public class DataSerialization {


    /**
     * Computes and serialized distances and speeds from environment nodes to charging stations
     * @param chargingStations
     * @param nodes
     * @param fileName
     * @throws IOException
     */
    public static void serializeChargingStationDistancesAndSpeed(List<ChargingStation> chargingStations,
                                                                 Collection<? extends EnvironmentNode> nodes, String fileName) throws IOException {
        HashMap<Integer, HashMap<Integer, Double>> resultDistances = new HashMap<>();
        HashMap<Integer, HashMap<Integer, Double>> resultSpeeds = new HashMap<>();

        for (ChargingStation chargingStation : chargingStations) {
            addDistancesAndSpeedsToChargingStation(chargingStation, nodes, resultDistances, resultSpeeds);
        }

        serializeData(fileName, resultDistances, resultSpeeds);
    }


    private static void addDistancesAndSpeedsToChargingStation(ChargingStation chargingStation,
                                                               Collection<? extends EnvironmentNode> nodes,
                                                               HashMap<Integer, HashMap<Integer, Double>> resultDistances,
                                                               HashMap<Integer, HashMap<Integer, Double>> resultSpeeds
                                                               ){

        HashMap<Integer, Double> stationDistances = new HashMap<>();
        HashMap<Integer, Double> stationSpeeds = new HashMap<>();

        Set<LinkedList<Integer>> paths = nodes.parallelStream().map(node -> aStar(node.getId(), chargingStation.getRoadNode().getId())).collect(Collectors.toSet());

        for (LinkedList<Integer> path :paths){
            if (path != null){
                DistanceSpeedPair distanceSpeedPair = getDistanceSpeedPairOfPath(path);

                stationDistances.put(path.getFirst(), distanceSpeedPair.getDistance());
                stationSpeeds.put(path.getFirst(), distanceSpeedPair.getSpeed());

            } else {
                throw new IllegalArgumentException("No connection between node: " + path.getFirst() + " and node: " + chargingStation.getId());
            }
        }

        resultDistances.put(chargingStation.getRoadNode().getId(), stationDistances);
        resultSpeeds.put(chargingStation.getRoadNode().getId(), stationSpeeds);
    }


    private static void serializeData(String fileName,
                                      HashMap<Integer, HashMap<Integer, Double>> resultDistances,
                                      HashMap<Integer, HashMap<Integer, Double>> resultSpeeds) throws IOException {

        FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream(fileName));
        out.writeObject( new AllDistancesSpeedsPair(resultDistances, resultSpeeds) );
        out.close();
    }
}
