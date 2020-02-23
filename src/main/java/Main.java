import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;

import java.io.File;
import java.util.Collection;

public class Main {
    public static void main(String[] args) {
        Graph<RoadNode, RoadEdge> graph = GraphLoader.loadGraph(new File("data/graphs/prague_small.fst"));

        Collection<RoadNode> nodes = graph.getAllNodes();


        for (RoadNode node : nodes){
            System.out.println(node.id);
        }
        System.out.println();
    }
}
