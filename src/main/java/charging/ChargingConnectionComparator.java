package charging;

import java.util.Comparator;

public class ChargingConnectionComparator implements Comparator<ChargingConnection> {
    @Override
    public int compare(ChargingConnection o1, ChargingConnection o2) {
        return Double.compare(o1.getPowerKW(), o2.getPowerKW());
    }
}