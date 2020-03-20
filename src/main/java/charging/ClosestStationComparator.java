package charging;

import java.util.Comparator;

public class ClosestStationComparator implements Comparator<TripToChargingStation> {
    @Override
    public int compare(TripToChargingStation o1, TripToChargingStation o2) {
        return Double.compare(o1.getDistance(), o2.getDistance());
    }
}
