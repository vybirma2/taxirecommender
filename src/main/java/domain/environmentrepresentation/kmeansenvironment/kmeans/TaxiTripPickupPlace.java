package domain.environmentrepresentation.kmeansenvironment.kmeans;


import java.io.Serializable;

public class TaxiTripPickupPlace extends PickUpPoint implements Serializable {

    public TaxiTripPickupPlace(double longitude, double latitude) {
        super(longitude, latitude);
    }
}
