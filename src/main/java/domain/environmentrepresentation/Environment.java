package domain.environmentrepresentation;

import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.parameterestimation.TaxiTrip;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Abstract environment with prescribed methods and input parameters needed to be implement to be able to plan on
 * concrete implemented environment
 * @param <TNode> Class representing nodes in concrete implemented environment
 * @param <TEdge> Class representing edges in concrete implemented environment
 */
public abstract class Environment<TNode extends EnvironmentNode, TEdge extends EnvironmentEdge> {

    private final Graph<RoadNode, RoadEdge> osmGraph;
    protected EnvironmentGraph<TNode, TEdge> environmentGraph;
    protected List<TaxiTrip> trainingDataSet;


    public Environment(Graph<RoadNode, RoadEdge> osmGraph, List<TaxiTrip> trainingDataSet) {
        this.osmGraph = osmGraph;
        this.trainingDataSet = trainingDataSet;
        setEnvironmentGraph();
    }


    public Graph<RoadNode, RoadEdge> getOsmGraph() {
        return osmGraph;
    }

    public EnvironmentGraph<TNode, TEdge> getEnvironmentGraph() {
        return environmentGraph;
    }

    public Collection<TNode> getEnvironmentNodes() {
        return this.environmentGraph.getNodes();
    }

    public Set<Integer> getNodes() {
        return this.environmentGraph.getNodeIds();
    }

    public double getNodeLongitude(int nodeId){
        return osmGraph.getNode(nodeId).getLongitude();
    }

    public double getNodeLatitude(int nodeId){
        return osmGraph.getNode(nodeId).getLatitude();
    }


    protected abstract void setEnvironmentGraph();
}

