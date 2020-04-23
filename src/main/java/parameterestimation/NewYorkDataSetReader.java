package parameterestimation;

import cz.agents.multimodalstructures.nodes.RoadNode;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import utils.DistanceGraphUtils;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class NewYorkDataSetReader implements DataSetReader {
    private HashMap<Integer, RoadNode> zoneLatLongs;
    private String inputFile = "data/taxitrips/newyork/new_york_taxi_trips.csv";



    @Override
    public ArrayList<TaxiTrip> readDataSet() throws IOException, ClassNotFoundException {
        File file = new File("data/programdata/new_york.fst");
        ArrayList<TaxiTrip> taxiTrips = null;

        if(!file.exists()) {
            taxiTrips = parseTaxiTripsFromOriginalDataFileAndSerialize(file);
        } else {
            taxiTrips = readSerializedFile(file);
        }

        return taxiTrips;
    }


    private ArrayList<TaxiTrip> parseTaxiTripsFromOriginalDataFileAndSerialize(File file) throws IOException, ClassNotFoundException {
        ArrayList<TaxiTrip> taxiTrips = null;
        BufferedReader csvReader;
        String row;

        NewYorkLongitudeLatitudeReader newYorkLongitudeLatitudeReader = new NewYorkLongitudeLatitudeReader();
        zoneLatLongs = newYorkLongitudeLatitudeReader.getZoneLatLongs();

        try {
            int numOfRows = 0;
            csvReader = new BufferedReader(new FileReader(inputFile));
            taxiTrips = new ArrayList<>();

            while ((row = csvReader.readLine()) != null) {
                if (numOfRows > 0){
                    TaxiTrip taxiTrip = parseTaxiTrip(row.split(","));
                    if (taxiTrip != null && taxiTrip.getTripLength() != 0){
                        taxiTrips.add(taxiTrip);
                    }
                }
                numOfRows++;
            }

            csvReader.close();

            FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream(file));
            out.writeObject(taxiTrips);
            out.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return taxiTrips;
    }


    private ArrayList<TaxiTrip> readSerializedFile(File file) throws IOException, ClassNotFoundException {
        FSTObjectInput in = new FSTObjectInput(new FileInputStream(file));
        ArrayList<TaxiTrip> taxiTrips = (ArrayList<TaxiTrip>) in.readObject();
        in.close();
        return taxiTrips;
    }


    private TaxiTrip parseTaxiTrip(String [] trip) throws ParseException {




        /*for (String string : trip){
            System.out.println(string);
        }
        return null;*/
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");

        int pickupZone = Integer.parseInt(trip[7]);
        int dropOffZone = Integer.parseInt(trip[8]);

        double distance = Integer.parseInt(trip[4]);

        RoadNode pickUpNode = zoneLatLongs.get(pickupZone);
        RoadNode destinationNode = zoneLatLongs.get(dropOffZone);

        if (pickUpNode == null || destinationNode == null){
            return null;
        }

        Date startDate = dateFormat.parse(trip[1]);
        Date finishDate = dateFormat.parse(trip[2]);

        long tripLengthMilliseconds = Math.abs(finishDate.getTime() - startDate.getTime());
        long tripLengthMinutes = TimeUnit.MINUTES.convert(tripLengthMilliseconds, TimeUnit.MILLISECONDS);

        return null /*new TaxiTrip(orderId, pickUpLongitude, pickUpLatitude, destinationLongitude,
                destinationLatitude, distance,tripLengthMinutes , pickUpNode, destinationNode, startDate, finishDate, null, null)*/;
    }
}
