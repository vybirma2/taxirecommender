package charging;


import cz.agents.multimodalstructures.nodes.RoadNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.Utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ChargingStationUtils {

    private static JSONParser jsonParser = new JSONParser();
    private static Collection<RoadNode> nodes;
    private static double maxChargingStationRoadNodeDistance = 0.5;
    private static HashMap<Integer, ChargingStation> chargingStations = new HashMap<>();
    private static HashMap<Integer, ChargingConnection> chargingConnections = new HashMap<>();


    public static List<ChargingStation> readChargingStations(String sourceFile, Collection<RoadNode> nodes)
            throws ParseException, IOException {

        ChargingStationUtils.nodes = nodes;
        ArrayList<ChargingStation> chargingStations = new ArrayList<>();

        JSONArray stations = (JSONArray) jsonParser.parse(new FileReader(sourceFile));
        Iterator<JSONObject> iterator = stations.iterator();

        while (iterator.hasNext()) {
            ChargingStation chargingStation = ChargingStationUtils.createChargingStation(iterator.next());
            if (chargingStation != null) {

                System.out.println(chargingStation);

                chargingStations.add(chargingStation);
            }
        }

        return chargingStations;
    }


    public static boolean isChargingStationRoadNode(Integer roadNodeId){
        return chargingStations.containsKey(roadNodeId);
    }


    private static ChargingStation createChargingStation(JSONObject station){
        JSONObject addressInfo = (JSONObject) station.get("AddressInfo");
        Long id = (Long)addressInfo.get("ID");
        Long countryId = (Long)addressInfo.get("CountryID");
        String postCode = (String)addressInfo.get("Postcode");

        String title = (String)addressInfo.get("Title");
        String address = (String)addressInfo.get("AddressLine1");
        String town = (String)addressInfo.get("Town");

        double longitude = (double)addressInfo.get("Longitude");
        double latitude = (double)addressInfo.get("Latitude");

        JSONArray connectionsJSON = (JSONArray)station.get("Connections");
        ArrayList<ChargingConnection> connections = new ArrayList<>(connectionsJSON.size());

        for (JSONObject connection : (Iterable<JSONObject>) connectionsJSON) {
            if (connection.get("ID") != null && connection.get("PowerKW") != null){
                ChargingConnection chargingConnection = new ChargingConnection(Math.toIntExact((Long) connection.get("ID")),
                        (Double) connection.get("PowerKW"), Utils.COST_FOR_KW);
                connections.add(chargingConnection);
                chargingConnections.put(chargingConnection.getId(), chargingConnection);
            }
        }

        if (addressInfo.get("AddressLine2") != null) {
            address += "\n" + addressInfo.get("AddressLine2");
        }

        RoadNode node = ChargingStationUtils.chooseRoadNode(longitude, latitude);

        if (node != null){
            ChargingStation chargingStation = new ChargingStation(Math.toIntExact(id), Math.toIntExact(countryId), postCode, title, address, town, longitude, latitude, node, connections);
            chargingStations.put(node.getId(), chargingStation);
            return chargingStation;
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


    public static ChargingStation getChargingStation(int nodeId){
        return chargingStations.get(nodeId);
    }

    public static ChargingConnection getChargingConnection(int connectionId) {
        return chargingConnections.get(connectionId);
    }
}
