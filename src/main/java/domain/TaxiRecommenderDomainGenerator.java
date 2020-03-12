package domain;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.model.FactoredModel;
import charging.ChargingStation;
import charging.ChargingStationUtils;
import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.actions.*;
import org.json.simple.parser.ParseException;
import parameterestimation.ParameterEstimator;
import parameterestimation.TaxiTrip;
import utils.DistanceGraphUtils;
import utils.GraphLoader;
import utils.Utils;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static utils.DistanceGraphUtils.*;

public class TaxiRecommenderDomainGenerator extends GraphDefinedDomain {

    private final static Logger LOGGER = Logger.getLogger(TaxiRecommenderDomainGenerator.class.getName());

    private static Graph<RoadNode, RoadEdge> graph;
    private static Collection<RoadNode> nodes;
    private static List<ChargingStation> chargingStations;
    private static ArrayList<TaxiTrip> taxiTrips;


    private String roadGraphInputFile;
    private String chargingStationsInputFile;

    private ParameterEstimator parameterEstimator;

    private SADomain domain = null;


    public TaxiRecommenderDomainGenerator(String roadGraphInputFile, String chargingStationsInputFile){
        super();
        this.roadGraphInputFile = roadGraphInputFile;
        this.chargingStationsInputFile = chargingStationsInputFile;
    }


    public SADomain getDomain(){
        if (this.domain == null){
            this.domain = this.generateDomain();
        }

        return domain;
    }


    @Override
    public SADomain generateDomain() {
        SADomain domain = null;
        try {
            loadData();

            domain = new SADomain();
            Map<Integer, Map<Integer, Set<NodeTransitionProbability>>> ctd = this.copyTransitionDynamics();
            TaxiGraphStateModel stateModel = new TaxiGraphStateModel(ctd);

            domain.addActionType(new StayingInLocationActionType(ActionTypes.STAYING_IN_LOCATION.getValue(), ctd));
            domain.addActionType(new NextLocationActionType(ActionTypes.TO_NEXT_LOCATION.getValue(),ctd));
            domain.addActionType(new GoingToChargingStationActionType(ActionTypes.GOING_TO_CHARGING_STATION.getValue(),ctd));
            domain.addActionType(new ChargingActionType(ActionTypes.CHARGING_IN_CHARGING_STATION.getValue(), ctd));

            setRf(new TaxiGraphRewardFunction());
            setTf(new TaxiGraphTerminalFunction(domain.getActionTypes()));

            FactoredModel model = new FactoredModel(stateModel, this.rf, this.tf);
            domain.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return domain;
    }


    private void loadData() throws Exception {
        loadGraph();

        loadChargingStations();

        computeShortestPathsToChargingStations();

        loadTaxiTripDataset();

        setTransitions();
    }


    private void loadGraph() throws Exception {
        long startTime;
        long stopTime;

        LOGGER.log(Level.INFO, "Loading graph...");
        startTime = System.nanoTime();
        graph = GraphLoader.loadGraph(roadGraphInputFile);
        DistanceGraphUtils.setGraph(graph);
        stopTime  = System.nanoTime();
        nodes = graph.getAllNodes();
        DistanceGraphUtils.setNodes(nodes);


        LOGGER.log(Level.INFO, "Loading finished in " + (stopTime - startTime)/1000000000. + " s, loaded " + nodes.size() + " nodes, " + graph.getAllEdges().size() + " edges.");
    }


    private void loadChargingStations() throws IOException, ParseException {
        long startTime;
        long stopTime;

        LOGGER.log(Level.INFO, "Loading charging stations...");
        startTime = System.nanoTime();
        chargingStations = ChargingStationUtils.readChargingStations(chargingStationsInputFile);
        DistanceGraphUtils.setChargingStations(chargingStations);
        stopTime  = System.nanoTime();
        LOGGER.log(Level.INFO, "Loading finished in " + (stopTime - startTime)/1000000000. + " s, loaded " + chargingStations.size() + " charging stations.");
    }


    private void computeShortestPathsToChargingStations(){
        long startTime;
        long stopTime;

        LOGGER.log(Level.INFO, "Computing shortest paths to charging stations...");
        startTime = System.nanoTime();
        DistanceSpeedPair distanceSpeedPair = getChargingStationDistancesAndSpeed();
        DistanceGraphUtils.setChargingStationDistances(distanceSpeedPair.getDistances());
        DistanceGraphUtils.setChargingStationSpeeds(distanceSpeedPair.getSpeeds());
        stopTime  = System.nanoTime();
        LOGGER.log(Level.INFO, "Computing finished in " + (stopTime - startTime)/1000000000. + "s.");
    }


    private void loadTaxiTripDataset(){
        long startTime;
        long stopTime;

        LOGGER.log(Level.INFO, "Reading taxi trip dataset...");
        startTime = System.nanoTime();
        taxiTrips = Utils.DATA_SET_READER.readDataSet();
        stopTime  = System.nanoTime();
        LOGGER.log(Level.INFO, "Reading finished in " + (stopTime - startTime)/1000000000. + "s.");

        this.parameterEstimator = new ParameterEstimator(taxiTrips);
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



    private DistanceSpeedPair getChargingStationDistancesAndSpeed(){
        HashMap<Integer, HashMap<Integer, Double>> resultDistances = new HashMap<>();
        HashMap<Integer, HashMap<Integer, Double>> resultSpeeds = new HashMap<>();

        for (ChargingStation chargingStation : chargingStations) {
            HashMap<Integer, Double> stationDistances = new HashMap<>();
            HashMap<Integer, Double> stationSpeeds = new HashMap<>();

            for (RoadNode roadNode : nodes){
                LinkedList<Integer> nodePath = aStar(roadNode, chargingStation.getRoadNode());
                if (nodePath != null){

                    double distance = 0;
                    double speed = 0;
                    Integer current = nodePath.getFirst();

                    for (Integer node : nodePath){
                        distance += getDistanceBetweenNodes(current, node);
                        speed += getSpeedBetweenNodes(current, node);
                        current = node;
                    }

                    stationDistances.put(roadNode.getId(), distance);
                    stationSpeeds.put(roadNode.getId(), speed/(nodePath.size() - 1));

                } else {
                    throw new IllegalArgumentException("No connection between node: " + roadNode.getId() + " and node: " + chargingStation.getId());
                }
            }
            resultDistances.put(chargingStation.getRoadNode().getId(), stationDistances);
            resultSpeeds.put(chargingStation.getRoadNode().getId(), stationSpeeds);
        }

        return new DistanceSpeedPair(resultDistances, resultSpeeds);
    }

}
