package parameterestimation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public interface DataSetReader {

    String inputFile = null;

    ArrayList<TaxiTrip> readDataSet() throws IOException, ClassNotFoundException;

}
