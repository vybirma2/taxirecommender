package domain.environmentrepresentation;

import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;

import java.io.IOException;
import java.time.temporal.Temporal;
import java.util.*;

/**
 * Graph structure to plan on
 * @param <TNode>
 * @param <TEdge>
 */
public abstract class EnvironmentGraph<TNode extends EnvironmentNode, TEdge extends EnvironmentEdge>  {

    protected Graph<RoadNode, RoadEdge> osmGraph;
    protected HashMap<Integer, TNode> nodes;
    protected HashMap<Integer, HashMap<Integer, TEdge>> edges;

    public EnvironmentGraph(Graph<RoadNode, RoadEdge> osmGraph) {
        this.osmGraph = osmGraph;
        setNodes();
        setEdges();
    }

    public TNode getNode(int nodeId){
        return nodes.get(nodeId);
    }

    public EnvironmentEdge getEdge(int fromNode, int toNode){
        HashMap<Integer, TEdge> fromNodeEdges = edges.get(fromNode);
        return fromNodeEdges != null ? edges.get(fromNode).get(toNode) : null;
    }

    public Collection<TNode> getNodes() {
        return nodes.values();
    }

    public Collection<TEdge> getEdges(){
        Set<TEdge> ed = new HashSet<>();

        for (Map.Entry<Integer, HashMap<Integer, TEdge>> entry : edges.entrySet()){
            for (Map.Entry<Integer, TEdge> edge : entry.getValue().entrySet()){
                ed.add(edge.getValue());
            }
        }
        return ed;
    }

    public Set<Integer> getNodeIds(){
        return nodes.keySet();
    }

    protected abstract void setNodes();

    protected abstract void setEdges();
}


