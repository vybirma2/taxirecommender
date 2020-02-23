package domain;

import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.oo.OODomain;
import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import utils.GraphLoader;

import java.io.File;

public class GraphDomain implements DomainGenerator {

    public static final String VAR_ROAD_NODE = "road_node";
    public static final String VAR_STATE_OF_CHARGE = "state_of_charge";
    public static final String VAR_TIMESTAMP = "timestamp";


    public static final String CLASS_LOCATION_NODE = "location";
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
