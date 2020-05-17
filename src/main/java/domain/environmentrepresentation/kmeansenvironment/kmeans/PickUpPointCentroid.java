package domain.environmentrepresentation.kmeansenvironment.kmeans;

import java.io.Serializable;

public class PickUpPointCentroid extends PickUpPoint implements Serializable {

    public PickUpPointCentroid(double longitude, double latitude) {
        super(longitude, latitude);
    }
}
