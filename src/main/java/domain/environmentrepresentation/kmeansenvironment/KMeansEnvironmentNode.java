package domain.environmentrepresentation.kmeansenvironment;

import cz.agents.basestructures.GPSLocation;
import domain.environmentrepresentation.EnvironmentNode;

import java.util.Set;

public class KMeansEnvironmentNode extends EnvironmentNode {


    public KMeansEnvironmentNode(int id, long sourceId, GPSLocation location, Set<Integer> neighbours) {
        super(id, sourceId, location, false, false, neighbours);
    }
}
