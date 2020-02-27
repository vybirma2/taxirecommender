package utils;

import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import org.nustaq.serialization.FSTObjectInput;

import java.io.FileInputStream;

public class GraphLoader {
    public static Graph<RoadNode, RoadEdge> loadGraph(String sourceFile) throws Exception {
        FSTObjectInput objectInput = new FSTObjectInput(new FileInputStream(sourceFile));
        return (Graph<RoadNode, RoadEdge>) objectInput.readObject(Graph.class);
    }
}
