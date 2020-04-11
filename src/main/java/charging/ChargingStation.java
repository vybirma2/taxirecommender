package charging;

import cz.agents.multimodalstructures.nodes.RoadNode;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class representing physical charging station which is connected with one concrete RoadNode - node in graph
 * on which whole planning is done. Contains all needed location parameters and also list of available connections.
 */
public class ChargingStation implements Serializable {

    private RoadNode roadNode;
    private ArrayList<ChargingConnection> connections;


    public ChargingStation(RoadNode roadNode, ArrayList<ChargingConnection> connections) {
        this.roadNode = roadNode;
        this.connections = connections;
    }


    public double getLongitude() {
        return roadNode.getLongitude();
    }


    public double getLatitude() {
        return roadNode.getLatitude();
    }


    public RoadNode getRoadNode() {
        return roadNode;
    }


    public ArrayList<ChargingConnection> getAvailableConnections() {
        return connections;
    }


    @Override
    public String toString() {
        return "Lon: " + roadNode.getLongitude() + ", lat: " + roadNode.getLatitude();
    }
}
