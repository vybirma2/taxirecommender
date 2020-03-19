package domain.environmentrepresentation;

import cz.agents.basestructures.GPSLocation;
import cz.agents.multimodalstructures.nodes.RoadNode;

import java.io.Serializable;
import java.util.Set;

public abstract class EnvironmentNode extends RoadNode implements Serializable {
    protected Set<Integer> neighbours;


    public EnvironmentNode(int id, long sourceId, GPSLocation location, boolean isParkAndRide, boolean isBikeSharingStation, Set<Integer> neighbours) {
        super(id, sourceId, location, isParkAndRide, isBikeSharingStation);
        this.neighbours = neighbours;
    }

    public Set<Integer> getNeighbours(){
        return neighbours;
    }


    @Override
    public double getLatitude() {
        return super.getLatitude();
    }


    @Override
    public double getLongitude() {
        return super.getLongitude();
    }
}
