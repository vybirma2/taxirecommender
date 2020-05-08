package domain.environmentrepresentation.gridworldenvironment;

import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.environmentrepresentation.EnvironmentNode;

import java.io.Serializable;
import java.util.Set;

/**
 * Grid wold node/cell with its bounding box coordinates and set of fitted nodes
 */
public class GridWorldEnvironmentNode extends EnvironmentNode implements Serializable {

    public GridWorldEnvironmentNode(RoadNode node, Set<Integer> neighbours) {
        super(node, neighbours);
    }
}
