package domain.environmentrepresentation.kmeansenvironment.kmeans;


import java.util.*;

/**
 * K-means algorithm modification of implementation on https://www.baeldung.com/java-k-means-clustering-algorithm
 * K-means++ initialization modification of implementation on https://www.geeksforgeeks.org/ml-k-means-algorithm/
 * */
public class KMeansAlgorithm {
    private static final Random random = new Random();

    public static Map<PickUpPointCentroid, List<TaxiTripPickupPlace>> runKMeans(List<TaxiTripPickupPlace> pickupPlaces, int numOfCentroids,
                                                                                PickUpPlacesDistance distance, int maxIterations) {

        List<PickUpPointCentroid> centroids = kMeansPPInit(pickupPlaces, numOfCentroids, distance);
        Map<PickUpPointCentroid, List<TaxiTripPickupPlace>> clusters = new HashMap<>();
        Map<PickUpPointCentroid, List<TaxiTripPickupPlace>> lastState = new HashMap<>();

        for (int i = 0; i < maxIterations; i++) {
            boolean isLastIteration = i == maxIterations - 1;

            for (TaxiTripPickupPlace record : pickupPlaces) {
                PickUpPointCentroid centroid = findTheNearestCentroid(record, centroids, distance);
                assignToCluster(clusters, record, centroid);
            }

            if (isLastIteration) {
                break;
            } else if (clusters.equals(lastState)){
                break;
            }
            lastState = clusters;

            reallocateCentroids(clusters);
            clusters = new HashMap<>();
        }

        return lastState;
    }


    private static void assignToCluster(Map<PickUpPointCentroid, List<TaxiTripPickupPlace>> clusters,
                                        TaxiTripPickupPlace pickupPlace,
                                        PickUpPointCentroid centroid) {
        if (!clusters.containsKey(centroid)){
            List<TaxiTripPickupPlace> centroidPickupPlaces = new ArrayList<>();
            centroidPickupPlaces.add(pickupPlace);
            clusters.put(centroid, centroidPickupPlaces);
        } else {
            clusters.get(centroid).add(pickupPlace);
        }
    }


    private static void reallocateCentroids(Map<PickUpPointCentroid, List<TaxiTripPickupPlace>> assignment){
        for (Map.Entry<PickUpPointCentroid, List<TaxiTripPickupPlace>> centroid : assignment.entrySet()){
            recomputeCentroid(centroid.getKey(), centroid.getValue());
        }
    }



    private static void recomputeCentroid(PickUpPointCentroid centroid, List<TaxiTripPickupPlace> pickupPlaces) {
        if (pickupPlaces == null || pickupPlaces.isEmpty()) {
            return;
        }

        double longitude = 0;
        double latitude = 0;

        for (TaxiTripPickupPlace pickupPlace : pickupPlaces){
            longitude += pickupPlace.getLongitude();
            latitude += pickupPlace.getLatitude();
        }

        longitude = longitude/pickupPlaces.size();
        latitude = latitude/pickupPlaces.size();

        centroid.setLongitude(longitude);
        centroid.setLatitude(latitude);
    }


    private static PickUpPointCentroid findTheNearestCentroid(TaxiTripPickupPlace pickupPlace,
                                                              List<PickUpPointCentroid> centroids,
                                                              PickUpPlacesDistance distance){

        double minimumDistance = Double.MAX_VALUE;
        PickUpPointCentroid nearest = null;

        for (PickUpPointCentroid centroid : centroids) {
            double currentDistance = distance.getDistance(pickupPlace, centroid);

            if (currentDistance < minimumDistance) {
                minimumDistance = currentDistance;
                nearest = centroid;
            }
        }

        return nearest;
    }

    private static List<PickUpPointCentroid> kMeansPPInit(List<TaxiTripPickupPlace> pickupPlaces, int numOfCentroids, PickUpPlacesDistance distance) {
        List<PickUpPointCentroid> centroids = new ArrayList<>();
        int placeIndex = random.nextInt(pickupPlaces.size());

        centroids.add(createCentroid(pickupPlaces.get(placeIndex).getLongitude(), pickupPlaces.get(placeIndex).getLatitude()));

        for (int i = 0; i < numOfCentroids - 1; i++){

            double maxDistance = Double.MIN_VALUE;
            TaxiTripPickupPlace maxDistancePickUpPlace = null;

            for (TaxiTripPickupPlace pickupPlace : pickupPlaces){
                double minDistance = Double.MAX_VALUE;
                for (PickUpPointCentroid centroid : centroids){
                    double newDistance = distance.getDistance(pickupPlace, centroid);
                    minDistance = Math.min(minDistance, newDistance);
                }
                if (maxDistance < minDistance){
                    maxDistance = minDistance;
                    maxDistancePickUpPlace = pickupPlace;
                }
            }
            PickUpPointCentroid newCentroid = createCentroid(maxDistancePickUpPlace.getLongitude(), maxDistancePickUpPlace.getLatitude());
            centroids.add(newCentroid);
        }

        return centroids;
    }


    private static PickUpPointCentroid createCentroid(double longitude, double latitude){
        return new PickUpPointCentroid(longitude, latitude);
    }
}
