package domain.environmentrepresentation.gridenvironment;

import cz.agents.basestructures.GPSLocation;
import domain.environmentrepresentation.EnvironmentNode;

import java.util.Set;

public class GridEnvironmentNode extends EnvironmentNode {
    public GridEnvironmentNode(int id, long sourceId, GPSLocation location, boolean isParkAndRide, boolean isBikeSharingStation, Set<Integer> neighbours) {
        super(id, sourceId, location, isParkAndRide, isBikeSharingStation, neighbours);
    }
}
