package domain.environmentrepresentation.kmeansenvironment.kmeansenvironmentutils;

import domain.environmentrepresentation.kmeansenvironment.kmeans.TaxiTripPickupPlace;

import java.util.ArrayList;
import java.util.List;

public class ConvexHullFinder {

    public static List<TaxiTripPickupPlace> getConvexHull(List<TaxiTripPickupPlace> pickupPlaces) {

        if (pickupPlaces.size() <= 3) {
            return pickupPlaces;
        }

        final ArrayList<TaxiTripPickupPlace> convexHull = new ArrayList<>();

        int minPoint = -1;
        int maxPoint = -1;
        double minX = Integer.MAX_VALUE;
        double maxX = Integer.MIN_VALUE;

        for (int i = 0; i < pickupPlaces.size(); i++) {
            if (pickupPlaces.get(i).getLongitude() < minX) {
                minX = pickupPlaces.get(i).getLongitude();
                minPoint = i;
            }
            if (pickupPlaces.get(i).getLongitude() > maxX) {
                maxX = pickupPlaces.get(i).getLongitude();
                maxPoint = i;
            }
        }

        final TaxiTripPickupPlace a = pickupPlaces.get(minPoint);
        final TaxiTripPickupPlace b = pickupPlaces.get(maxPoint);
        convexHull.add(a);
        convexHull.add(b);
        pickupPlaces.remove(a);
        pickupPlaces.remove(b);

        ArrayList<TaxiTripPickupPlace> leftSet = new ArrayList<>();
        ArrayList<TaxiTripPickupPlace> rightSet = new ArrayList<>();

        for(TaxiTripPickupPlace p : pickupPlaces){
            if (pointLocation(a, b, p) == -1){
                leftSet.add(p);
            }else {
                rightSet.add(p);
            }
        }

        hullSet(a, b, rightSet, convexHull);
        hullSet(b, a, leftSet, convexHull);

        return convexHull;
    }

    private static void hullSet(TaxiTripPickupPlace a, TaxiTripPickupPlace b, ArrayList<TaxiTripPickupPlace> set, ArrayList<TaxiTripPickupPlace> convexHull) {
        final int insertPosition = convexHull.indexOf(b);
        if (set.size() == 0) return;
        if (set.size() == 1) {
            final TaxiTripPickupPlace p = set.get(0);
            set.remove(p);
            convexHull.add(insertPosition, p);
            return;
        }
        double dist = Integer.MIN_VALUE;
        int furthestPoint = -1;
        for(int i = 0 ; i < set.size() ; i++){
            TaxiTripPickupPlace p = set.get(i);
            double distance = distance(a, b, p);
            if (distance > dist) {
                dist = distance;
                furthestPoint = i;
            }
        }

        final TaxiTripPickupPlace p = set.get(furthestPoint);
        set.remove(furthestPoint);
        convexHull.add(insertPosition, p);

        // Determine who's to the left of AP
        final ArrayList<TaxiTripPickupPlace> leftSetAP = new ArrayList<>();
        for(TaxiTripPickupPlace m : set){
            if (pointLocation(a, p, m) == 1) {
                leftSetAP.add(m);
            }
        }

        // Determine who's to the left of PB
        final ArrayList<TaxiTripPickupPlace> leftSetPB = new ArrayList<>();
        for(TaxiTripPickupPlace m : set){
            if (pointLocation(p, b, m) == 1) {
                leftSetPB.add(m);
            }
        }

        hullSet(a, p, leftSetAP, convexHull);
        hullSet(p, b, leftSetPB, convexHull);
    }

    private static double distance(TaxiTripPickupPlace a, TaxiTripPickupPlace b, TaxiTripPickupPlace c) {
        final double ABx = b.getLongitude() - a.getLongitude();
        final double ABy = b.getLatitude() - a.getLatitude();
        double dist = ABx * (a.getLatitude() - c.getLatitude()) - ABy * (a.getLongitude() - c.getLongitude());
        if (dist < 0) dist = -dist;
        return dist;
    }

    private static int pointLocation(TaxiTripPickupPlace a, TaxiTripPickupPlace b, TaxiTripPickupPlace p) {
        double cp1 = (b.getLatitude() - a.getLatitude()) * (p.getLongitude() - a.getLongitude()) - (b.getLongitude() - a.getLongitude()) * (p.getLatitude() - a.getLatitude());
        return (cp1 > 0) ? 1 : -1;
    }


}
