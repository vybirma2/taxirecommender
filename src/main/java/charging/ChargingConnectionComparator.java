package charging;

import java.util.Comparator;

/**
 * Comparator comparing charging connections according to the power provided bz connection.
 */
public class ChargingConnectionComparator implements Comparator<ChargingConnection> {


    @Override
    public int compare(ChargingConnection o1, ChargingConnection o2) {
        return Double.compare(o1.getPowerKW(), o2.getPowerKW());
    }
}
