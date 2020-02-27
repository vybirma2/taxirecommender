package domain;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.auxiliary.common.NullTermination;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.common.NullRewardFunction;
import burlap.mdp.singleagent.model.FactoredModel;
import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import utils.ChargingStation;
import utils.ChargingStationUtils;
import utils.GraphLoader;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TaxiRecommenderDomainGenerator extends GraphDefinedDomain {

    public static final String VAR_NODE = "node";
    public static final String VAR_STATE_OF_CHARGE = "state_of_charge";
    public static final String VAR_TIMESTAMP = "timestamp";

    public static final String CLASS_TIME = "time";
    public static final String CLASS_STATE_OF_CHARGE = "state_of_charge";

    private Graph<RoadNode, RoadEdge> graph;
    private Collection<RoadNode> nodes;
    private List<ChargingStation> chargingStations;

    private SADomain domain = null;


    public TaxiRecommenderDomainGenerator(String roadGraphInputFile, String chargingStationsInputFile) throws Exception {
        graph = GraphLoader.loadGraph(roadGraphInputFile);
        nodes = graph.getAllNodes();
        chargingStations = ChargingStationUtils.readChargingStations(chargingStationsInputFile, nodes);
        setTransitions();
        setRf(new NullRewardFunction());
        setTf(new NullTermination());
    }

    public SADomain getDomain(){
        if (this.domain == null){
            this.domain = this.generateDomain();
        }
        return domain;
    }

    @Override
    public SADomain generateDomain() {
        SADomain domain = new SADomain();
        Map<Integer, Map<Integer, Set<NodeTransitionProbability>>> ctd = this.copyTransitionDynamics();
        TaxiGraphStateModel stateModel = new TaxiGraphStateModel(ctd);
        FactoredModel model = new FactoredModel(stateModel, this.rf, this.tf);
        domain.setModel(model);

        for(int i = 0; i < this.maxActions; ++i) {
            domain.addActionType(new GraphDefinedDomain.GraphActionType(i, ctd));
        }

        return domain;
    }


    private void setTransitions() {

        for (RoadNode node : nodes) {

            // setting transition between node itself - action of staying in location, i.e. prob 1
            this.setTransition(node.getId(), Action.STAYING_IN_LOCATION.getValue(), node.getId(), 1.);

            // setting transitions between neighbouring nodes - action of going to next location, i.e. prob 1
            List<RoadEdge> edges = graph.getOutEdges(node);
            for (RoadEdge edge : edges){
                this.setTransition(edge.getFromId(), Action.TO_NEXT_LOCATION.getValue(), edge.getToId(), 1.);
            }

            // setting transitions between current node and all available charging stations
            for (ChargingStation station : chargingStations){
                this.setTransition(node.getId(), Action.GOING_TO_CHARGING_STATION.getValue(), station.getRoadNode().getId(), 1.);
            }

            // setting transition between node itself - action of charging if node connected with charging station, i.e. prob 1
            if (ChargingStationUtils.isChargingStationRoadNode(node.getId())){
                this.setTransition(node.getId(), Action.CHARGING_IN_CHARGING_STATION.getValue(), node.getId(), 1.);
            }
        }

    }


}
