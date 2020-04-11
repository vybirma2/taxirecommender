package domain.environmentrepresentation.kmeansenvironment;

import cz.agents.basestructures.GPSLocation;
import domain.environmentrepresentation.EnvironmentNode;
import domain.environmentrepresentation.kmeansenvironment.kmeans.PickUpPointCentroid;
import domain.environmentrepresentation.kmeansenvironment.kmeans.TaxiTripPickupPlace;

import java.util.List;
import java.util.Set;

public class KMeansEnvironmentNode extends EnvironmentNode {

    private PickUpPointCentroid centroid;
    private List<TaxiTripPickupPlace> pickupPlaceList;

    public KMeansEnvironmentNode(int id, long sourceId, GPSLocation location, Set<Integer> neighbours, PickUpPointCentroid centroid, List<TaxiTripPickupPlace> pickupPlaces) {
        super(id, sourceId, location, false, false, neighbours);
        this.centroid = centroid;
        this.pickupPlaceList = pickupPlaces;
    }


    public PickUpPointCentroid getCentroid() {
        return centroid;
    }

    public List<TaxiTripPickupPlace> getPickupPlaceList() {
        return pickupPlaceList;
    }
}
