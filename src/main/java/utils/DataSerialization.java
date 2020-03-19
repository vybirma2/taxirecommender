package utils;

import charging.ChargingStation;
import domain.DistanceSpeedPair;
import domain.environmentrepresentation.EnvironmentNode;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static utils.DistanceGraphUtils.*;

public class DataSerialization {


    public static void serializeChargingStationDistancesAndSpeed(List<ChargingStation> chargingStations,
                                                                 Collection<? extends EnvironmentNode> nodes, String fileName) throws IOException {
        HashMap<Integer, HashMap<Integer, Double>> resultDistances = new HashMap<>();
        HashMap<Integer, HashMap<Integer, Double>> resultSpeeds = new HashMap<>();

        for (ChargingStation chargingStation : chargingStations) {
            HashMap<Integer, Double> stationDistances = new HashMap<>();
            HashMap<Integer, Double> stationSpeeds = new HashMap<>();

            Set<LinkedList<Integer>> paths = nodes.parallelStream().map(node -> aStar(node.getId(), chargingStation.getRoadNode().getId())).collect(Collectors.toSet());

            for (LinkedList<Integer> path :paths){
                if (path != null){
                    double distance = 0;
                    double speed = 0;
                    Integer current = path.getFirst();

                    for (Integer node : path){
                        distance += getDistanceBetweenOsmNodes(current, node);
                        speed += getSpeedBetweenOsmNodes(current, node);
                        current = node;
                    }

                    stationDistances.put(path.getFirst(), distance);
                    stationSpeeds.put(path.getFirst(), speed/(path.size() - 1));

                } else {
                    throw new IllegalArgumentException("No connection between node: " + path.getFirst() + " and node: " + chargingStation.getId());
                }
            }

            resultDistances.put(chargingStation.getRoadNode().getId(), stationDistances);
            resultSpeeds.put(chargingStation.getRoadNode().getId(), stationSpeeds);
        }

        FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream(fileName));
        out.writeObject( new DistanceSpeedPair(resultDistances, resultSpeeds) );
        out.close();
    }
}
