package charging;


import cz.agents.multimodalstructures.nodes.RoadNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import utils.DistanceGraphUtils;
import utils.Utils;

import java.io.*;
import java.util.*;

/**
 * Class containing static methods to read available charging stations from given source file. Reads raw data or
 * if data were already cached, it reads charging stations from cached .fst file (faster manipulation). Json file
 * of charging stations received from https://openchargemap.org/site API supported.
 */
public class ChargingStationReader {

    private static JSONParser jsonParser = new JSONParser();
    private static HashMap<Integer, ChargingStation> chargingStations = new HashMap<>();
    private static HashMap<Integer, ChargingConnection> chargingConnections = new HashMap<>();


    /**
     * Needs to be called after creation of reading graph and Environment to be able to fit charging stations to
     * concrete graph node.
     * @param fullPathToSourceFile full path to the source file with raw charging station data.
     * @param sourceFileName name of the raw data source file
     * @return list of available charging stations
     * @throws ParseException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static List<ChargingStation> readChargingStations(String fullPathToSourceFile, String sourceFileName)
            throws ParseException, IOException, ClassNotFoundException {

        File file = new File("data/programdata/" + sourceFileName +".fst");

        if(!file.exists()){
            readRawDataFromSourceFile(fullPathToSourceFile);
            writeSerializedDataToFile(file);
        } else {
            readSerializedDataFromFile(file);
        }

        return new ArrayList<>(ChargingStationReader.chargingStations.values());
    }


    private static void readRawDataFromSourceFile(String fullPathToSourceFile) throws IOException, ParseException {
        JSONArray stations = (JSONArray) jsonParser.parse(new FileReader(fullPathToSourceFile));

        for (JSONObject station : (Iterable<JSONObject>) stations) {
            ChargingStation chargingStation = ChargingStationReader.parseChargingStation(station);
            if (chargingStation != null) {
                ChargingStationReader.chargingStations.put(chargingStation.getRoadNode().getId(), chargingStation);
            }
        }
    }


    private static void writeSerializedDataToFile(File file) throws IOException {
        FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream(file));
        out.writeObject(ChargingStationReader.chargingStations);
        out.writeObject(ChargingStationReader.chargingConnections);
        out.close();
    }


    private static void readSerializedDataFromFile(File file) throws IOException, ClassNotFoundException {
        FSTObjectInput in = new FSTObjectInput(new FileInputStream(file));
        ChargingStationReader.chargingStations = (HashMap<Integer, ChargingStation>) in.readObject();
        ChargingStationReader.chargingConnections = (HashMap<Integer, ChargingConnection>) in.readObject();
        in.close();
    }


    private static ChargingStation parseChargingStation(JSONObject station){
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


    /**
     * @param nodeId original graph node id
     * @return charging station connected with this node if any
     */
    public static ChargingStation getChargingStation(int nodeId){
        return chargingStations.get(nodeId);
    }


    /**
     * @param connectionId
     * @return charging connection
     */
    public static ChargingConnection getChargingConnection(int connectionId) {
        return chargingConnections.get(connectionId);
    }
}