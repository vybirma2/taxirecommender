package domain.environmentrepresentation.fullenvironment;

import cz.agents.basestructures.GPSLocation;
import domain.environmentrepresentation.EnvironmentNode;

import java.util.Set;

public class FullEnvironmentNode extends EnvironmentNode {


    public FullEnvironmentNode(int nodeId, Set<Integer> neighbours) {
        super(nodeId, neighbours);
    }
}
