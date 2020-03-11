package parameterestimation;

import java.util.ArrayList;

public interface DataSetReader {

    String inputFile = null;

    ArrayList<TaxiTrip> readDataSet();

}
