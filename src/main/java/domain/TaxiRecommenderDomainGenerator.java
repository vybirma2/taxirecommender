package domain;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.singleagent.SADomain;
import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import utils.GraphLoader;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class TaxiRecommenderDomainGenerator {

    private File inputFile;

    private Graph<RoadNode, RoadEdge> graph;
    private Collection<RoadNode> nodes;
    private GraphDefinedDomain domain;

    public TaxiRecommenderDomainGenerator(File inputFile) {
        this.inputFile = inputFile;
    }

    public SADomain getDomain() throws Exception {
        graph = GraphLoader.loadGraph(inputFile);
        nodes = graph.getAllNodes();
        domain = new GraphDefinedDomain(nodes.size());

        setTransitions();
        setRewardFunction();
        setTerminalFunction();
        return domain.generateDomain();
    }


    private void setTransitions() {
        for (RoadNode node : nodes){

            // setting transition between node itself - action of staying in location, i.e. prob 1
            domain.setTransition(node.getId(), Action.STAYING_IN_LOCATION.getValue(), node.getId(), 1.);

            // setting transitions between neighbouring nodes - action of going to next location, i.e. prob 1
            List<RoadEdge> edges = graph.getOutEdges(node);
            for (RoadEdge edge : edges){
                domain.setTransition(edge.getFromId(), Action.TO_NEXT_LOCATION.getValue(), edge.getToId(), 1.);
            }
        }
    }

    private void setRewardFunction() {

    }

    private void setTerminalFunction() {

    }

}
