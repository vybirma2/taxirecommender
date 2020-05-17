package domain.environmentrepresentation.osmenvironment;

import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.environmentrepresentation.EnvironmentNode;

import java.util.Set;

public class OSMEnvironmentNode extends EnvironmentNode {

    public OSMEnvironmentNode(RoadNode node, Set<Integer> neighbours) {
        super(node, neighbours);
    }
}
