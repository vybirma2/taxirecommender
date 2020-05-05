package domain.charging;

import java.util.Comparator;

/**
 * Comparator for trips to domain.charging station from road nodes with respect to the distance of the trip.
 */
public class ClosestStationComparator implements Comparator<TripToChargingStation> {


    @Override
    public int compare(TripToChargingStation o1, TripToChargingStation o2) {
        return Double.compare(o1.getDistance(), o2.getDistance());
    }
}
