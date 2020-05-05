package domain.environmentrepresentation.fullenvironment;

import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.environmentrepresentation.EnvironmentGraph;
import domain.utils.DistanceGraphUtils;

import java.io.IOException;
import java.util.HashMap;

/**
 * Graph for FullEnvironment which copies the structure of the original graph parsed from OSM data
 */
public class FullEnvironmentGraph extends EnvironmentGraph<FullEnvironmentNode, FullEnvironmentEdge> {


    public FullEnvironmentGraph(Graph<RoadNode, RoadEdge> osmGraph) throws IOException, ClassNotFoundException {
        super(osmGraph);
    }


    /**
     * Copying all nodes from given osmGraph to FullEnvironmentGraph
     */
    @Override
    protected void setNodes(){
        nodes = new HashMap<>();

        for (RoadNode node : osmGraph.getAllNodes()){
            nodes.put(node.getId(), new FullEnvironmentNode(node.id, DistanceGraphUtils.getOsmNeighbours(node.getId())));
        }
    }


    /**
     * Copying all edges from given osmGraph to FullEnvironmentGraph
     */
    @Override
    protected void setEdges(){
        edges = new HashMap<>();

        for (RoadEdge edge : osmGraph.getAllEdges()){
            if (edges.containsKey(edge.fromId)){
                edges.get(edge.fromId).put(edge.toId, new FullEnvironmentEdge(edge.getFromId(), edge.getToId(), edge.allowedMaxSpeedInMpS, edge.length,
                        DistanceGraphUtils.getTripTime(edge.length, edge.allowedMaxSpeedInMpS)));
            } else {
                HashMap<Integer, FullEnvironmentEdge> nodeEdges = new HashMap<>();
                nodeEdges.put(edge.toId, new FullEnvironmentEdge(edge.getFromId(), edge.getToId(), edge.allowedMaxSpeedInMpS, edge.length,
                        DistanceGraphUtils.getTripTime(edge.length, edge.allowedMaxSpeedInMpS)));
                edges.put(edge.fromId, nodeEdges);
            }
        }
    }
}
