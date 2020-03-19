package domain.environmentrepresentation;



import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.environmentrepresentation.gridenvironment.GridEnvironmentEdge;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public abstract class EnvironmentGraph<TNode extends EnvironmentNode, TEdge extends EnvironmentEdge>  {
    protected Graph<RoadNode, RoadEdge> osmGraph;
    protected HashMap<Integer, TNode> nodes;
    protected HashMap<Integer, HashMap<Integer, TEdge>> edges;

    public EnvironmentGraph(Graph<RoadNode, RoadEdge> osmGraph) {
        this.osmGraph = osmGraph;
        setNodes();
        setEdges();
    }

    public abstract Set<Integer> getNeighbours(int nodeId);
    protected abstract void setNodes();
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


