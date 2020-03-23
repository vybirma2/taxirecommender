package parameterestimation;

import java.io.IOException;
import java.util.ArrayList;

public interface DataSetReader {

    ArrayList<TaxiTrip> readDataSet() throws IOException, ClassNotFoundException;

}
