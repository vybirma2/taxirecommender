package domain.environmentrepresentation.fullenvironment;

import cz.agents.basestructures.GPSLocation;
import domain.environmentrepresentation.EnvironmentNode;

import java.util.Set;

public class FullEnvironmentNode extends EnvironmentNode {


    public FullEnvironmentNode(int id, long sourceId, GPSLocation location, Set<Integer> neighbours) {
        super(id, sourceId, location, false, false, neighbours);
    }
}
