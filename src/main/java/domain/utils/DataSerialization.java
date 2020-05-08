package domain.utils;

import domain.charging.ChargingStation;
import domain.environmentrepresentation.EnvironmentNode;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static domain.utils.DistanceGraphUtils.*;

public class DataSerialization {


    /**
     * Computes and serialized distances and speeds from environment nodes to domain.charging stations
     * @param chargingStations
     * @param nodes
     * @param fileName
     * @throws IOException
     */
    public static void serializeChargingStationDistancesAndSpeed(List<ChargingStation> chargingStations,
                                                                 Collection<? extends EnvironmentNode> nodes, String fileName) throws IOException {
        HashMap<Integer, HashMap<Integer, DistanceSpeedPairTime>> resultDistanceSpeedTime = new HashMap<>();

        int i = 0;
        for (ChargingStation chargingStation : chargingStations) {
            addDistancesAndSpeedsToChargingStation(chargingStation, nodes, resultDistanceSpeedTime);
            i++;
            System.out.println("Computed distances for " + i + " domain.charging station.");
        }

        serializeData(fileName, resultDistanceSpeedTime);
    }


    private static void addDistancesAndSpeedsToChargingStation(ChargingStation chargingStation,
                                                               Collection<? extends EnvironmentNode> nodes,
                                                               HashMap<Integer, HashMap<Integer, DistanceSpeedPairTime>> resultDistanceSpeedTime){

        HashMap<Integer, DistanceSpeedPairTime> stationDistanceSpeedTime = new HashMap<>();

        Set<LinkedList<Integer>> paths = nodes.parallelStream().map(node -> aStar(node.getNodeId(), chargingStation.getRoadNode().getId())).collect(Collectors.toSet());

        for (LinkedList<Integer> path : paths){
            if (path != null){
                DistanceSpeedPairTime distanceSpeedPairTime = getDistanceSpeedPairOfPath(path);

                if (!path.isEmpty()) {
                    stationDistanceSpeedTime.put(path.getFirst(), distanceSpeedPairTime);
                } else {
                    stationDistanceSpeedTime.put(chargingStation.getRoadNode().getId(), distanceSpeedPairTime);
                }

            } else {
                throw new IllegalArgumentException("No connection between nodes");
            }
        }

        resultDistanceSpeedTime.put(chargingStation.getRoadNode().getId(), stationDistanceSpeedTime);
    }


    private static void serializeData(String fileName,
                                      HashMap<Integer, HashMap<Integer, DistanceSpeedPairTime>> resultDistanceSpeedTime) throws IOException {

        FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream(fileName));
        out.writeObject(new AllDistancesSpeedsPair(resultDistanceSpeedTime));
        out.close();
    }
}
