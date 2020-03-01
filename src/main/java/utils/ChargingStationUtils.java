package utils;


import cz.agents.multimodalstructures.nodes.RoadNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ChargingStationUtils {

    private static JSONParser jsonParser = new JSONParser();
    private static Collection<RoadNode> nodes;
    private static double maxChargingStationRoadNodeDistance = 0.5;
    private static HashSet<Integer> chargingStationRoadNodes = new HashSet();


    public static List<ChargingStation> readChargingStations(String sourceFile, Collection<RoadNode> nodes)
            throws ParseException, IOException {

        ChargingStationUtils.nodes = nodes;
        ArrayList<ChargingStation> chargingStations = new ArrayList<>();

        JSONArray stations = (JSONArray) jsonParser.parse(new FileReader(sourceFile));
        Iterator<JSONObject> iterator = stations.iterator();

        while (iterator.hasNext()) {
            ChargingStation chargingStation = ChargingStationUtils.createChargingStation(iterator.next());
            if (chargingStation != null){

                System.out.println(chargingStation);

                chargingStations.add(chargingStation);
            }
        }

        return chargingStations;
    }


    public static boolean isChargingStationRoadNode(Integer roadNodeId){
        return chargingStationRoadNodes.contains(roadNodeId);
    }


    private static ChargingStation createChargingStation(JSONObject station){
        Long id = (Long)station.get("ID");
        Long countryId = (Long)station.get("CountryID");
        String postCode = (String)station.get("Postcode");

        String title = (String)station.get("Title");
        String address = (String)station.get("AddressLine1");
        String town = (String)station.get("Town");

        double longitude = (double)station.get("Longitude");
        double latitude = (double)station.get("Latitude");

        if (station.get("AddressLine2") != null){
            address += "\n" + station.get("AddressLine2");
        }

        RoadNode node = ChargingStationUtils.chooseRoadNode(longitude, latitude);

        if (node != null){
            chargingStationRoadNodes.add(node.getId());
            return new ChargingStation(Math.toIntExact(id), Math.toIntExact(countryId), postCode, title, address, town, longitude, latitude, node);
        } else {
            return null;
        }
    }


    private static RoadNode chooseRoadNode(double longitude, double latitude){
        double min = Double.MAX_VALUE;
        RoadNode roadNode = null;

        for (RoadNode node : nodes){
            double distance = getDistance(longitude, latitude, node.getLongitude(), node.getLatitude());

            if (distance < ChargingStationUtils.maxChargingStationRoadNodeDistance && distance < min){
                min = distance;
                roadNode = node;
            }
        }

        return roadNode;
    }


    public static double getDistance(double longitude1, double latitude1, double longitude2, double latitude2){
        longitude1 = Math.toRadians(longitude1);
        latitude1 = Math.toRadians(latitude1);
        longitude2 = Math.toRadians(longitude2);
        latitude2 = Math.toRadians(latitude2);

        double dlon = longitude2 - longitude1;
        double dlat = latitude2 - latitude1;
        double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(latitude1) * Math.cos(latitude2) * Math.pow(Math.sin(dlon / 2),2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double radius = 6371;

        return (c * radius);
    }
}
