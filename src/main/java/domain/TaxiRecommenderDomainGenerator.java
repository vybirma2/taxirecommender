package domain;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.common.NullRewardFunction;
import burlap.mdp.singleagent.model.FactoredModel;
import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.actions.*;
import utils.ChargingStation;
import utils.ChargingStationUtils;
import utils.GraphLoader;

import java.util.*;

public class TaxiRecommenderDomainGenerator extends GraphDefinedDomain {

    private static Graph<RoadNode, RoadEdge> graph;
    private static Collection<RoadNode> nodes;
    private static List<ChargingStation> chargingStations;

    private SADomain domain = null;


    public TaxiRecommenderDomainGenerator(String roadGraphInputFile, String chargingStationsInputFile) throws Exception {
        super();
        graph = GraphLoader.loadGraph(roadGraphInputFile);
        nodes = graph.getAllNodes();
        chargingStations = ChargingStationUtils.readChargingStations(chargingStationsInputFile, nodes);
        setTransitions();
        setRf(new NullRewardFunction());
        setTf(new TaxiGraphTerminalFunction());
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

        domain.addActionType(new StayingInLocationActionType(ActionTypes.STAYING_IN_LOCATION.getValue(), ctd));
        domain.addActionType(new NextLocationActionType(ActionTypes.TO_NEXT_LOCATION.getValue(),ctd));
        domain.addActionType(new GoingToChargingStationActionType(ActionTypes.GOING_TO_CHARGING_STATION.getValue(),ctd));
        domain.addActionType(new ChargingActionType(ActionTypes.CHARGING_IN_CHARGING_STATION.getValue(), ctd));

        return domain;
    }


    private void setTransitions() {
        for (RoadNode node : nodes) {

            // setting transition between node itself - action of staying in location, i.e. prob 1
            this.setTransition(node.getId(), ActionTypes.STAYING_IN_LOCATION.getValue(), node.getId(), 1.);

            // setting transitions between neighbouring nodes - action of going to next location, i.e. prob 1
            List<RoadEdge> edges = graph.getOutEdges(node);
            for (RoadEdge edge : edges) {
                this.setTransition(edge.getFromId(), ActionTypes.TO_NEXT_LOCATION.getValue(), edge.getToId(), 1.);
            }

            // setting transitions between current node and all available charging stations
            for (ChargingStation station : chargingStations){
                this.setTransition(node.getId(), ActionTypes.GOING_TO_CHARGING_STATION.getValue(), station.getRoadNode().getId(), 1.);
            }

            // setting transition between node itself - action of charging if node connected with charging station, i.e. prob 1
            if (ChargingStationUtils.isChargingStationRoadNode(node.getId())){
                    this.setTransition(node.getId(), ActionTypes.CHARGING_IN_CHARGING_STATION.getValue(), node.getId(), 1.);
            }
        }
    }


    public static Set<Integer> getNeighbours(int node){
        List<RoadEdge> edges = graph.getOutEdges(node);
        Set<Integer> neighbours = new HashSet<>();
        for (RoadEdge edge : edges){
            neighbours.add(edge.getToId());
        }

        return neighbours;
    }


    public static List<ChargingStation> getChargingStations(){
        return chargingStations;
    }


    public static double getTripTime(int fromNodeId, int toNodeId){
        return (getDistanceBetweenNodes(fromNodeId, toNodeId)/getSpeedBetweenNodes(fromNodeId, toNodeId))*60
                + getDelay(fromNodeId, toNodeId);
    }


    // TODO - implement real distance - used euclidean now
    public static double getDistanceBetweenNodes(int fromNodeId, int toNodeId){
        RoadNode fromNode = graph.getNode(fromNodeId);
        RoadNode toNode = graph.getNode(toNodeId);
        return ChargingStationUtils.getDistance(fromNode.getLongitude(), fromNode.getLatitude(),
                toNode.getLongitude(), toNode.getLatitude());
    }


    // TODO - further development - delay function
    public static double getDelay(int fromNodeId, int toNodeId){
        return 0;
    }


    // TODO - estimate from data
    public static double getSpeedBetweenNodes(int fromNodeId, int toNodeId){
        return 30;
    }
}
