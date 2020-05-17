package domain.parameterestimation;

import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageForwardRequest;
import com.byteowls.jopencage.model.JOpenCageLatLng;
import com.byteowls.jopencage.model.JOpenCageResponse;
import cz.agents.multimodalstructures.nodes.RoadNode;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import domain.utils.DistanceGraphUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * class using OpenCage tool to parse longitude latitude data from Adress for the purpose of new york
 * taxi trip data set.
 */
public class NewYorkLongitudeLatitudeReader {

    private static JOpenCageGeocoder jOpenCageGeocoder = new JOpenCageGeocoder("9a25fc76f3e54b7e8034ce69f2e0d22e");
    private HashMap<Integer, RoadNode> zoneLatLongs = new HashMap<>();
    private String lookupFile = "data\\taxitrips\\newyork\\zone_lookup.csv";

    public HashMap<Integer, RoadNode> getZoneLatLongs() throws IOException, ClassNotFoundException {
        File file = new File("data/taxitrips/newyork/zone_lookup.fst");
        ArrayList<TaxiTrip> taxiTrips = null;

        if(!file.exists()) {
            parseZoneLatLongsFromAndSerialize(file);
        } else {
            readSerializedZoneLatLongs(file);
        }

        return zoneLatLongs;
    }

    private void readSerializedZoneLatLongs(File file) throws IOException, ClassNotFoundException {
        FSTObjectInput in = new FSTObjectInput(new FileInputStream(file));
        zoneLatLongs = (HashMap<Integer, RoadNode>) in.readObject();
        in.close();
    }

    private void parseZoneLatLongsFromAndSerialize(File file){
        BufferedReader csvReader;
        String row;

        try {
            csvReader = new BufferedReader(new FileReader(lookupFile));

            while ((row = csvReader.readLine()) != null) {
                addNewLatLongCoordinate(row.split(","));
            }

            csvReader.close();

            FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream(file));
            out.writeObject(zoneLatLongs);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNewLatLongCoordinate(String [] address){

        int placeId = Integer.parseInt(address[0].substring(1));
        String borough = address[1].substring(2, address[1].length() - 2);
        String zone = address[2].substring(2, address[2].length() - 2);

        JOpenCageForwardRequest request = new JOpenCageForwardRequest(zone +", " + borough + ", New York, USA");
        request.setRestrictToCountryCode("us");
        request.setBounds(-74.217763, 40.507326, -73.616169, 40.942979);

        JOpenCageResponse response = jOpenCageGeocoder.forward(request);
        JOpenCageLatLng lonLat = response.getFirstPosition();

        RoadNode roadNode = DistanceGraphUtils.chooseRoadNode(lonLat.getLng(), lonLat.getLat());
        zoneLatLongs.put(placeId, roadNode);
    }
}
