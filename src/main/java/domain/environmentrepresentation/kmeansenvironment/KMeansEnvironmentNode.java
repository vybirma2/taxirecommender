package domain.environmentrepresentation.kmeansenvironment;

import domain.environmentrepresentation.EnvironmentNode;
import domain.environmentrepresentation.kmeansenvironment.kmeans.PickUpPointCentroid;
import domain.environmentrepresentation.kmeansenvironment.kmeans.TaxiTripPickupPlace;

import java.util.List;
import java.util.Set;

public class KMeansEnvironmentNode extends EnvironmentNode {

    private PickUpPointCentroid centroid;
    private List<TaxiTripPickupPlace> pickupPlaceList;

    public KMeansEnvironmentNode(int nodeId, Set<Integer> neighbours, PickUpPointCentroid centroid,
                                 List<TaxiTripPickupPlace> pickupPlaces) {

        super(nodeId, neighbours);
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
