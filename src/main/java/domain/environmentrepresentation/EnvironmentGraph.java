package domain.environmentrepresentation;

import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * Graph structure to plan on
 * @param <TNode>
 * @param <TEdge>
 */
public abstract class EnvironmentGraph<TNode extends EnvironmentNode, TEdge extends EnvironmentEdge>  {

    protected Graph<RoadNode, RoadEdge> osmGraph;
    protected HashMap<Integer, TNode> nodes;
    protected HashMap<Integer, HashMap<Integer, TEdge>> edges;


    public EnvironmentGraph(Graph<RoadNode, RoadEdge> osmGraph) throws IOException, ClassNotFoundException {
        this.osmGraph = osmGraph;
        setNodes();
        setEdges();
    }


    protected abstract void setNodes() throws IOException, ClassNotFoundException;


    protected abstract void setEdges();


    public EnvironmentNode getNode(int nodeId){
        return nodes.get(nodeId);
    }


    public EnvironmentEdge getEdge(int fromNode, int toNode){
        HashMap<Integer, TEdge> fromNodeEdges = edges.get(fromNode);
        return fromNodeEdges != null ? edges.get(fromNode).get(toNode) : null;
    }


    public Collection<TNode> getNodes() {
        return nodes.values();
    }


    public Set<Integer> getNodeIds(){
        return nodes.keySet();
    }
}


