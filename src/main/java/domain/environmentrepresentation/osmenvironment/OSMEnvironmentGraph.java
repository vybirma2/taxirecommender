package domain.environmentrepresentation.osmenvironment;

import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.environmentrepresentation.EnvironmentGraph;
import domain.utils.DistanceGraphUtils;

import java.io.IOException;
import java.util.HashMap;

/**
 * Graph for OSMEnvironment which copies the structure of the original graph parsed from OSM data
 */
public class OSMEnvironmentGraph extends EnvironmentGraph<OSMEnvironmentNode, OSMEnvironmentEdge> {


    public OSMEnvironmentGraph(Graph<RoadNode, RoadEdge> osmGraph) {
        super(osmGraph);
    }

    /**
     * Copying all nodes from given osmGraph to OSMEnvironmentGraph
     */
    @Override
    protected void setNodes(){
        nodes = new HashMap<>();

        for (RoadNode node : osmGraph.getAllNodes()){
            nodes.put(node.getId(), new OSMEnvironmentNode(node, DistanceGraphUtils.getOsmNeighbours(node.getId())));
        }
    }

    /**
     * Copying all edges from given osmGraph to OSMEnvironmentGraph
     */
    @Override
    protected void setEdges(){
        edges = new HashMap<>();

        for (RoadEdge edge : osmGraph.getAllEdges()){
            if (edges.containsKey(edge.fromId)){
                edges.get(edge.fromId).put(edge.toId, new OSMEnvironmentEdge(edge.getFromId(), edge.getToId(), edge.allowedMaxSpeedInMpS, edge.length,
                        DistanceGraphUtils.getTripTime(edge.length, edge.allowedMaxSpeedInMpS)));
            } else {
                HashMap<Integer, OSMEnvironmentEdge> nodeEdges = new HashMap<>();
                nodeEdges.put(edge.toId, new OSMEnvironmentEdge(edge.getFromId(), edge.getToId(), edge.allowedMaxSpeedInMpS, edge.length,
                        DistanceGraphUtils.getTripTime(edge.length, edge.allowedMaxSpeedInMpS)));
                edges.put(edge.fromId, nodeEdges);
            }
        }
    }
}
