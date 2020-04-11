package domain.environmentrepresentation.kmeansenvironment.kmeans;

import java.io.Serializable;
import java.util.Objects;

public abstract class PickUpPoint implements Serializable {

    private double longitude;
    private double latitude;

    public PickUpPoint(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }


    public double getLongitude() {
        return longitude;
    }


    public double getLatitude() {
        return latitude;
    }


    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PickUpPoint that = (PickUpPoint) o;
        return  Double.compare(that.longitude, longitude) == 0 &&
                Double.compare(that.latitude, latitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(longitude, latitude);
    }


    @Override
    public String toString() {
        return "PickUpPlaceRecord{" +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
