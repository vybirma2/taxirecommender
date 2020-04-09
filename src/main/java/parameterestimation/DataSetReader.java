package parameterestimation;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Interface to implement for any other new taxi trip dataset
 */
public interface DataSetReader {
    ArrayList<TaxiTrip> readDataSet() throws IOException, ClassNotFoundException;
}
