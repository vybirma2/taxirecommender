package domain.environmentrepresentation.fullenvironment;

import cz.agents.basestructures.GPSLocation;
import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.environmentrepresentation.EnvironmentGraph;
import utils.DistanceGraphUtils;

import java.util.HashMap;
import java.util.Set;

public class FullEnvironmentGraph extends EnvironmentGraph<FullEnvironmentNode, FullEnvironmentEdge> {


    public FullEnvironmentGraph(Graph<RoadNode, RoadEdge> osmGraph) {
        super(osmGraph);
    }

    @Override
    public Set<Integer> getNeighbours(int nodeId) {
        return nodes.get(nodeId).getNeighbours();
    }


    @Override
    protected void setNodes(){
        nodes = new HashMap<>();

        for (RoadNode node : osmGraph.getAllNodes()){
            nodes.put(node.getId(), new FullEnvironmentNode(
                    node.id, node.sourceId,
                    new GPSLocation(node.latE6, node.lonE6, node.latProjected, node.lonProjected, node.elevation),
                    DistanceGraphUtils.getOsmNeighbours(node.getId()))
            );
        }
    }


    @Override
    protected void setEdges(){
        edges = new HashMap<>();

        for (RoadEdge edge : osmGraph.getAllEdges()){
            if (edges.containsKey(edge.fromId)){
                edges.get(edge.fromId).put(edge.toId, new FullEnvironmentEdge(edge.fromId, edge.toId,edge.wayID,
                        edge.getPermittedModes(), edge.allowedMaxSpeedInMpS, edge.length, edge.getCategory()));
            } else {
                HashMap<Integer, FullEnvironmentEdge> nodeEdges = new HashMap<>();
                nodeEdges.put(edge.toId, new FullEnvironmentEdge(edge.fromId, edge.toId,edge.wayID,
                        edge.getPermittedModes(), edge.allowedMaxSpeedInMpS, edge.length, edge.getCategory()));
                edges.put(edge.fromId, nodeEdges);
            }
        }
    }
}
