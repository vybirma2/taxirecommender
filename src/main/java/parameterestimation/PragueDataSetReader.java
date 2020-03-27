package parameterestimation;

import domain.environmentrepresentation.EnvironmentNode;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import utils.DistanceGraphUtils;
import utils.Utils;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PragueDataSetReader implements DataSetReader {

    String inputFile = "data\\taxitrips\\prague\\liftago_prague.csv";


    @Override
    public ArrayList<TaxiTrip> readDataSet() throws IOException, ClassNotFoundException {

        File file = new File("data/programdata/" + Utils.ONE_GRID_CELL_WIDTH+ "x"+ Utils.ONE_GRID_CELL_HEIGHT + "liftago_prague.fst");

        ArrayList<TaxiTrip> taxiTrips = null;

        if(!file.exists()) {
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

                    FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream(file));
                    out.writeObject(taxiTrips);
                    out.close();
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            FSTObjectInput in = new FSTObjectInput(new FileInputStream(file));
            taxiTrips = (ArrayList<TaxiTrip>) in.readObject();
            in.close();
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

        if (pickUpNode == null || destinationNode == null){
            return null;
        }

        Date startDate = dateFormat.parse(trip[6]);
        Date finishDate = dateFormat.parse(trip[7]);

        long tripLengthMilliseconds = Math.abs(finishDate.getTime() - startDate.getTime());
        long tripLengthMinutes = TimeUnit.MINUTES.convert(tripLengthMilliseconds, TimeUnit.MILLISECONDS);

        return new TaxiTrip(orderId, pickUpLongitude, pickUpLatitude, destinationLongitude,
                destinationLatitude, distance,tripLengthMinutes , pickUpNode, destinationNode, startDate, finishDate);
    }

}
