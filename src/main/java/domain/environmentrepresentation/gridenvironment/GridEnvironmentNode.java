package domain.environmentrepresentation.gridenvironment;

import cz.agents.basestructures.GPSLocation;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.environmentrepresentation.EnvironmentNode;

import java.io.Serializable;
import java.util.Set;

/**
 * Grid wold node/cell with its bounding box coordinates and set of fitted nodes
 */
public class GridEnvironmentNode extends EnvironmentNode implements Serializable {


    public GridEnvironmentNode(int nodeId, Set<Integer> neighbours) {
        super(nodeId, neighbours);
    }
}
