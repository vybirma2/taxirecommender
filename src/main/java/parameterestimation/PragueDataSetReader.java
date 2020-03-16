package parameterestimation;

import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.environmentrepresentation.EnvironmentNode;
import utils.DistanceGraphUtils;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PragueDataSetReader implements DataSetReader {

    String inputFile = "data\\taxitrips\\prague\\liftago_prague.csv";


    @Override
    public ArrayList<TaxiTrip> readDataSet() {

        ArrayList<TaxiTrip> taxiTrips = null;
        File csvFile = new File(inputFile);
        if (csvFile.isFile()) {
            BufferedReader csvReader;
            String row;
            try {
                int numOfRows = 0;
                csvReader = new BufferedReader(new FileReader(inputFile));
                taxiTrips = new ArrayList<>();

                while ((row = csvReader.readLine()) != null) {
                    if (numOfRows > 0){
                        TaxiTrip taxiTrip = parseTaxiTrip(row.split(","));
                        if (taxiTrip != null){
                            taxiTrips.add(taxiTrip);
                        }
                    }
                    numOfRows++;
                }

                csvReader.close();
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }

        return taxiTrips;
    }


    private TaxiTrip parseTaxiTrip(String [] trip) throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String orderId = trip[0];

        double pickUpLatitude = Double.parseDouble(trip[1]);
        double pickUpLongitude = Double.parseDouble(trip[2]);

        double destinationLatitude = Double.parseDouble(trip[3]);
        double destinationLongitude = Double.parseDouble(trip[4]);

        double distance = Integer.parseInt(trip[5])/1000.;

        EnvironmentNode pickUpNode = DistanceGraphUtils.chooseEnvironmentNode(pickUpLongitude, pickUpLatitude);
        EnvironmentNode destinationNode = DistanceGraphUtils.chooseEnvironmentNode(destinationLongitude, destinationLatitude);

       // RoadNode pickUpOsmNode = DistanceGraphUtils.chooseRoadNode(pickUpLongitude, pickUpLatitude);
       // RoadNode destinationOsmNode = DistanceGraphUtils.chooseRoadNode(destinationLongitude, destinationLatitude);


        if (pickUpNode == null || destinationNode == null){
            return null;
        }

        Date startDate = dateFormat.parse(trip[6]);
        Date finishDate = dateFormat.parse(trip[7]);


        return new TaxiTrip(orderId, pickUpLongitude, pickUpLatitude, destinationLongitude,
                destinationLatitude, distance, pickUpNode, destinationNode, null, null, startDate, finishDate);
    }

}
