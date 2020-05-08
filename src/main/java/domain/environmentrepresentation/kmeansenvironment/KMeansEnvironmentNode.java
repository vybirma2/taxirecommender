package domain.environmentrepresentation.kmeansenvironment;

import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.environmentrepresentation.EnvironmentNode;
import domain.environmentrepresentation.kmeansenvironment.kmeans.PickUpPointCentroid;
import domain.environmentrepresentation.kmeansenvironment.kmeans.TaxiTripPickupPlace;

import java.util.List;
import java.util.Set;

public class KMeansEnvironmentNode extends EnvironmentNode {

    private final PickUpPointCentroid centroid;
    private final List<TaxiTripPickupPlace> pickupPlaceList;


    public KMeansEnvironmentNode(RoadNode node, Set<Integer> neighbours, PickUpPointCentroid centroid,
                                 List<TaxiTripPickupPlace> pickupPlaces) {

        super(node, neighbours);
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
