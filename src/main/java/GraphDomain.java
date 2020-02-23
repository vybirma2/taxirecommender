import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.Domain;
import burlap.mdp.core.oo.OODomain;
import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;

import java.io.File;

public class GraphDomain implements DomainGenerator {

    public static final String CLASS_LOCATION = "location";
    public static final String CLASS_TIME = "time";
    public static final String CLASS_STATE_OF_CHARGE = "state_of_charge";

    public static final String ACTION_NEXT_LOCATION = "next_location";
    public static final String ACTION_STAYING_IN_LOCATION = "staying_in";
    public static final String ACTION_GOING_TO_CHARGING_STATION = "going_charging";
    public static final String ACTION_CHARGING_AT_STATION = "charging";


    protected Graph<RoadNode, RoadEdge> graph;

    public GraphDomain(File inputFile) {
        graph = GraphLoader.loadGraph(inputFile);
    }

    @Override
    public OODomain generateDomain() {
        return null;
    }
}
