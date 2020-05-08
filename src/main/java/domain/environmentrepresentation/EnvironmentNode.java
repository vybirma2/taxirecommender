package domain.environmentrepresentation;

import cz.agents.multimodalstructures.nodes.RoadNode;

import java.io.Serializable;
import java.util.Set;

public abstract class EnvironmentNode implements Serializable {

    protected Set<Integer> neighbours;
    private final RoadNode node;


    public EnvironmentNode(RoadNode node, Set<Integer> neighbours) {
        this.neighbours = neighbours;
        this.node = node;
    }


    public Set<Integer> getNeighbours(){
        return neighbours;
    }

    public int getNodeId() {
        return node.getId();
    }

    public void addNeighbour(int neighbour){
        neighbours.add(neighbour);
    }

    public void addNeighbours(Set<Integer> neighbours){
        this.neighbours.addAll(neighbours);
    }

    public double getLatitude() {
        return node.getLatitude();
    }

    public double getLongitude() {
        return node.getLongitude();
    }

    public void setNeighbours(Set<Integer> neighbours) {
        this.neighbours = neighbours;
    }
}
