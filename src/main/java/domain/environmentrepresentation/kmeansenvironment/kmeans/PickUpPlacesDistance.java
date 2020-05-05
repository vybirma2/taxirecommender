package domain.environmentrepresentation.kmeansenvironment.kmeans;

import domain.utils.DistanceGraphUtils;

public class PickUpPlacesDistance {
    double getDistance(PickUpPoint place1, PickUpPoint place2){
        return DistanceGraphUtils.getEuclideanDistance(place1.getLongitude(), place1.getLatitude(), place2.getLongitude(), place2.getLatitude());
    }
}
