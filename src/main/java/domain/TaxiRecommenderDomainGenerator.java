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
import domain.environmentrepresentation.Environment;
import domain.environmentrepresentation.EnvironmentEdge;
import domain.environmentrepresentation.EnvironmentNode;
import domain.environmentrepresentation.fullenvironment.FullEnvironment;
import domain.environmentrepresentation.fullenvironment.FullEnvironmentEdge;
import domain.environmentrepresentation.fullenvironment.FullEnvironmentNode;
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

    private Environment<? extends EnvironmentNode, ? extends EnvironmentEdge> environment;

    private Graph<RoadNode, RoadEdge> osmGraph;
    private Collection<RoadNode> osmNodes;
    private List<ChargingStation> chargingStations;
    private ArrayList<TaxiTrip> taxiTrips;


    private String roadGraphInputFile;
    private String chargingStationsInputFile;

    private ParameterEstimator parameterEstimator;

    private SADomain domain = null;


    public TaxiRecommenderDomainGenerator(String roadGraphInputFile, String chargingStationsInputFile,
                                          Environment<? extends EnvironmentNode, ? extends EnvironmentEdge> environment){
        super();
        this.roadGraphInputFile = roadGraphInputFile;
        this.chargingStationsInputFile = chargingStationsInputFile;
        this.environment = environment;
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
        osmGraph = GraphLoader.loadGraph(roadGraphInputFile);


        DistanceGraphUtils.setOsmGraph(osmGraph);
        osmNodes = osmGraph.getAllNodes();
        DistanceGraphUtils.setOsmNodes(osmNodes);

        environment.setOsmGraph(osmGraph);

        DistanceGraphUtils.setNodes(environment.getEnvironmentNodes());
        DistanceGraphUtils.setGraph(environment.getEnvironmentGraph());

        stopTime  = System.nanoTime();

        LOGGER.log(Level.INFO, "Loading finished in " + (stopTime - startTime)/1000000000. + " s, loaded " + osmNodes.size() + " nodes, " + osmGraph.getAllEdges().size() + " edges.");
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
        this.parameterEstimator.estimateParameters();
    }


    private void setTransitions() {
        for (EnvironmentNode node : environment.getEnvironmentNodes()) {

            // setting transition between node itself - action of staying in location, i.e. prob 1
            this.setTransition(node.getId(), ActionTypes.STAYING_IN_LOCATION.getValue(), node.getId(), 1.);


            // setting transitions between neighbouring nodes - action of going to next location, i.e. prob 1
            Set<Integer> neighbours = node.getNeighbours();
            for (Integer neighbour : neighbours) {
                this.setTransition(node.getId(), ActionTypes.TO_NEXT_LOCATION.getValue(), neighbour, 1.);
            }


            // setting transitions between current node and all available charging stations
           // for (ChargingStation station : chargingStations){
            this.setTransition(node.getId(), ActionTypes.GOING_TO_CHARGING_STATION.getValue(), chargingStations.get(0).getId(), 1.);
            //}
        }

        for (ChargingStation chargingStation : chargingStations){
            this.setTransition(chargingStation.getId(), ActionTypes.CHARGING_IN_CHARGING_STATION.getValue(), chargingStation.getId(), 1.);
        }
    }



    private DistanceSpeedPair getChargingStationDistancesAndSpeed(){
        HashMap<Integer, HashMap<Integer, Double>> resultDistances = new HashMap<>();
        HashMap<Integer, HashMap<Integer, Double>> resultSpeeds = new HashMap<>();

        for (ChargingStation chargingStation : chargingStations) {
            HashMap<Integer, Double> stationDistances = new HashMap<>();
            HashMap<Integer, Double> stationSpeeds = new HashMap<>();

            for (EnvironmentNode environmentNode : environment.getEnvironmentNodes()){
                LinkedList<Integer> nodePath = aStar(environmentNode.getId(), chargingStation.getRoadNode().getId());
                if (nodePath != null){

                    double distance = 0;
                    double speed = 0;
                    Integer current = nodePath.getFirst();

                    for (Integer node : nodePath){
                        distance += getDistanceBetweenOsmNodes(current, node);
                        speed += getSpeedBetweenOsmNodes(current, node);
                        current = node;
                    }

                    stationDistances.put(environmentNode.getId(), distance);
                    stationSpeeds.put(environmentNode.getId(), speed/(nodePath.size() - 1));

                } else {
                    throw new IllegalArgumentException("No connection between node: " + environmentNode.getId() + " and node: " + chargingStation.getId());
                }
            }

            resultDistances.put(chargingStation.getRoadNode().getId(), stationDistances);
            resultSpeeds.put(chargingStation.getRoadNode().getId(), stationSpeeds);
        }

        return new DistanceSpeedPair(resultDistances, resultSpeeds);
    }

}
