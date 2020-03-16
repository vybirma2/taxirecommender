package charging;


import cz.agents.multimodalstructures.nodes.RoadNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.DistanceGraphUtils;
import utils.Utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ChargingStationUtils {

    private static JSONParser jsonParser = new JSONParser();
    private static HashMap<Integer, ChargingStation> chargingStations = new HashMap<>();
    private static HashMap<Integer, ChargingConnection> chargingConnections = new HashMap<>();


    public static List<ChargingStation> readChargingStations(String sourceFile)
            throws ParseException, IOException {

        ArrayList<ChargingStation> chargingStations = new ArrayList<>();

        JSONArray stations = (JSONArray) jsonParser.parse(new FileReader(sourceFile));

        for (JSONObject station : (Iterable<JSONObject>) stations) {
            ChargingStation chargingStation = ChargingStationUtils.createChargingStation(station);
            if (chargingStation != null) {
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

        RoadNode node = DistanceGraphUtils.chooseRoadNode(longitude, latitude);

        if (node != null) {
            ChargingStation chargingStation = new ChargingStation(Math.toIntExact(id), Math.toIntExact(countryId), postCode, title, address, town, longitude, latitude, node, connections);
            chargingStations.put(node.getId(), chargingStation);
            return chargingStation;
        } else {
            return null;
        }
    }


    public static ChargingStation getChargingStation(int nodeId){
        return chargingStations.get(nodeId);
    }


    public static ChargingConnection getChargingConnection(int connectionId) {
        return chargingConnections.get(connectionId);
    }
}
