package domain.environmentrepresentation;

import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.environmentrepresentation.gridenvironment.GridEnvironmentEdge;
import domain.environmentrepresentation.gridenvironment.GridEnvironmentNode;

import java.util.Collection;
import java.util.Set;

public abstract class Environment<TNode extends EnvironmentNode, TEdge extends EnvironmentEdge> {
    private Graph<RoadNode, RoadEdge> osmGraph;

    protected EnvironmentGraph<TNode, TEdge> environmentGraph;


    public void setOsmGraph(Graph<RoadNode, RoadEdge> osmGraph) {
        this.osmGraph = osmGraph;
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


    protected abstract void setEnvironmentGraph();


}
