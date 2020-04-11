package domain.environmentrepresentation;

import java.io.Serializable;
import java.util.Set;

public abstract class EnvironmentNode implements Serializable {

    protected Set<Integer> neighbours;
    private final int nodeId;


    public EnvironmentNode(int nodeId, Set<Integer> neighbours) {
        this.neighbours = neighbours;
        this.nodeId = nodeId;
    }


    public Set<Integer> getNeighbours(){
        return neighbours;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void addNeighbour(int neighbour){
        neighbours.add(neighbour);
    }


    public void addNeighbours(Set<Integer> neighbours){
        this.neighbours.addAll(neighbours);
    }


    public double getLatitude() {
        return Environment.getNodeLatitude(nodeId);
    }


    public double getLongitude() {
        return Environment.getNodeLongitude(nodeId);
    }
}
