package charging;

import cz.agents.multimodalstructures.nodes.RoadNode;

import java.util.ArrayList;

public class ChargingStation {

    private Integer id;
    private Integer countryId;

    private String postCode;
    private String title;
    private String address;
    private String town;

    private double longitude;
    private double latitude;

    private RoadNode roadNode;

    private ArrayList<ChargingConnection> connections;

    public ChargingStation(Integer id, Integer countryId, String postCode, String title, String address, String town,
                           double longitude, double latitude, RoadNode roadNode, ArrayList<ChargingConnection> connections) {
        this.id = id;
        this.countryId = countryId;
        this.postCode = postCode;
        this.title = title;
        this.address = address;
        this.town = town;
        this.longitude = longitude;
        this.latitude = latitude;
        this.roadNode = roadNode;
        this.connections = connections;
    }

    public Integer getId() {
        return id;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public String getPostCode() {
        return postCode;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public String getTown() {
        return town;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public RoadNode getRoadNode() {
        return roadNode;
    }

    public ArrayList<ChargingConnection> getAvailableConnections() {
        return connections;
    }

    @Override
    public String toString() {
        return "Id: " + id +
                ", Title: " + title + '\'' +
                ", Town: " + town + '\'' +
                ", Lon: " + longitude +
                ", Lat: " + latitude +
                ", lon: " + roadNode.getLongitude() + ", lat: " + roadNode.getLatitude();
    }
}
