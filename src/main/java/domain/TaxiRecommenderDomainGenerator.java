package domain;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.model.FactoredModel;
import charging.ChargingStation;
import charging.ChargingStationReader;
import charging.DistanceChargingStationStateOrder;
import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.actions.*;
import domain.environmentrepresentation.Environment;
import domain.environmentrepresentation.EnvironmentEdge;
import domain.environmentrepresentation.EnvironmentNode;
import org.json.simple.parser.ParseException;
import org.nustaq.serialization.FSTObjectInput;
import parameterestimation.ParameterEstimator;
import parameterestimation.TaxiTrip;
import utils.DataSerialization;
import utils.DistanceGraphUtils;
import utils.GraphLoader;
import utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class responsible for loading all needed data, creating all objects needed for planning and generating domain
 * for the following planning
 */
public class TaxiRecommenderDomainGenerator extends GraphDefinedDomain {

    private final static Logger LOGGER = Logger.getLogger(TaxiRecommenderDomainGenerator.class.getName());

    private Environment<? extends EnvironmentNode, ? extends EnvironmentEdge> environment;
    private Graph<RoadNode, RoadEdge> osmGraph;
    private Collection<RoadNode> osmNodes;
    private List<ChargingStation> chargingStations;
    private ArrayList<TaxiTrip> taxiTrips;

    private String roadGraphInputFileFullPath;
    private String roadGraphInputFile;
    private String chargingStationsInputFileFullPath;
    private String chargingStationsInputFile;

    private ParameterEstimator parameterEstimator;
    private SADomain domain = null;


    public TaxiRecommenderDomainGenerator(String roadGraphInputFile, String chargingStationsInputFile,
                                          Environment<? extends EnvironmentNode, ? extends EnvironmentEdge> environment){
        super();
        this.roadGraphInputFileFullPath = "data/graphs/" + roadGraphInputFile;
        this.roadGraphInputFile = roadGraphInputFile;
        this.chargingStationsInputFileFullPath = "data/chargingstations/" + chargingStationsInputFile;
        this.chargingStationsInputFile = chargingStationsInputFile;
        this.environment = environment;
    }


    /**
     * @return generated domain
     */
    public SADomain getDomain(){
        if (this.domain == null){
            this.domain = this.generateDomain();
        }
        return domain;
    }


    /**
     * Loading all data from extern files, estimating parameters, setting all needed objects
     * @return generated domain
     */
    @Override
    public SADomain generateDomain() {
        SADomain domain = null;
        try {
            loadData();

            Map<Integer, Map<Integer, Set<NodeTransitionProbability>>> ctd = this.copyTransitionDynamics();
            TaxiGraphStateModel stateModel = new TaxiGraphStateModel(ctd);
            domain = new SADomain();

            addAllActionTypes(domain, ctd);
            setTf(new TaxiGraphTerminalFunction(domain.getActionTypes()));
            setRf(new TaxiGraphRewardFunction(this.getTf(), parameterEstimator));

            FactoredModel model = new FactoredModel(stateModel, this.rf, this.tf);
            domain.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return domain;
    }


    private void addAllActionTypes(SADomain domain, Map<Integer, Map<Integer, Set<NodeTransitionProbability>>> ctd){
        domain.addActionType(new StayingInLocationActionType(ActionTypes.STAYING_IN_LOCATION.getValue(), ctd));
        domain.addActionType(new NextLocationActionType(ActionTypes.TO_NEXT_LOCATION.getValue(),ctd));
        domain.addActionType(new GoingToChargingStationActionType(ActionTypes.GOING_TO_CHARGING_STATION.getValue(),ctd));
        domain.addActionType(new ChargingActionType(ActionTypes.CHARGING_IN_CHARGING_STATION.getValue(), ctd));
        domain.addActionType(new PickUpPassengerActionType(ActionTypes.PICK_UP_PASSENGER.getValue(), ctd, parameterEstimator));
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
        osmGraph = GraphLoader.loadGraph(roadGraphInputFileFullPath);

        DistanceGraphUtils.setOsmGraph(osmGraph);
        osmNodes = osmGraph.getAllNodes();
        DistanceGraphUtils.setOsmNodes(osmNodes);

        environment.setOsmGraph(osmGraph);

        DistanceGraphUtils.setNodes(environment.getEnvironmentNodes());
        DistanceGraphUtils.setGraph(environment.getEnvironmentGraph());

        stopTime  = System.nanoTime();

        LOGGER.log(Level.INFO, "Loading finished in " + (stopTime - startTime)/1000000000. + " s, loaded " + osmNodes.size() + " nodes, " + osmGraph.getAllEdges().size() + " edges.");
    }


    private void loadChargingStations() throws IOException, ParseException, ClassNotFoundException {
        long startTime;
        long stopTime;

        LOGGER.log(Level.INFO, "Loading charging stations...");
        startTime = System.nanoTime();
        chargingStations = ChargingStationReader.readChargingStations(chargingStationsInputFileFullPath, chargingStationsInputFile);
        DistanceGraphUtils.setChargingStations(chargingStations);
        stopTime  = System.nanoTime();
        LOGGER.log(Level.INFO, "Loading finished in " + (stopTime - startTime)/1000000000. + " s, loaded " + chargingStations.size() + " charging stations.");
    }


    private void computeShortestPathsToChargingStations() throws IOException, ClassNotFoundException {
        long startTime;
        long stopTime;

        LOGGER.log(Level.INFO, "Computing shortest paths to charging stations...");
        startTime = System.nanoTime();
        AllDistancesSpeedsPair allDistancesSpeedsPair = getChargingStationDistancesAndSpeed("distance_speed_" + roadGraphInputFile);
        DistanceGraphUtils.setChargingStationDistances(allDistancesSpeedsPair.getDistances());
        DistanceGraphUtils.setChargingStationSpeeds(allDistancesSpeedsPair.getSpeeds());
        Utils.setChargingStationStateOrder(new DistanceChargingStationStateOrder(allDistancesSpeedsPair.getDistances(), this
        .environment.getNodes()));
        stopTime  = System.nanoTime();
        LOGGER.log(Level.INFO, "Computing finished in " + (stopTime - startTime)/1000000000. + "s.");
    }


    private void loadTaxiTripDataset() throws IOException, ClassNotFoundException {
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
            setToNextLocationTransitions(node);

            // setting transitions between current node and all available charging stations
            for (ChargingStation station : chargingStations) {
                this.setTransition(node.getId(), ActionTypes.GOING_TO_CHARGING_STATION.getValue(), station.getRoadNode().getId(), 1.);
            }
        }

        for (ChargingStation chargingStation : chargingStations){
            EnvironmentNode node = DistanceGraphUtils.chooseEnvironmentNode(chargingStation.getRoadNode().getLongitude(), chargingStation.getRoadNode().getLatitude());

            this.setTransition(chargingStation.getRoadNode().getId(), ActionTypes.CHARGING_IN_CHARGING_STATION.getValue(), chargingStation.getRoadNode().getId(), 1.);
            this.setTransition(chargingStation.getRoadNode().getId(), ActionTypes.TO_NEXT_LOCATION.getValue(), node.getId(), 1.);
        }
    }


    private void setToNextLocationTransitions(EnvironmentNode node){
        Set<Integer> neighbours = node.getNeighbours();

        for (Integer neighbour : neighbours) {
            this.setTransition(node.getId(), ActionTypes.TO_NEXT_LOCATION.getValue(), neighbour, 1.);
        }

        HashMap<Integer, Double> destinationProbabilities = parameterEstimator.getDestinationProbabilitiesInNode(node.getId());
        if (destinationProbabilities != null){
            for (Map.Entry<Integer, Double> destination : destinationProbabilities.entrySet()){
                this.setTransition(node.getId(), ActionTypes.PICK_UP_PASSENGER.getValue(), destination.getKey(), 1.);
            }
        }
    }


    private AllDistancesSpeedsPair getChargingStationDistancesAndSpeed(String inputFile) throws IOException, ClassNotFoundException {
        File file = new File("data/programdata/" + Utils.ONE_GRID_CELL_HEIGHT + "x" + Utils.ONE_GRID_CELL_WIDTH + "_" + inputFile);

        if(!file.exists()){
            DataSerialization.serializeChargingStationDistancesAndSpeed(chargingStations, environment.getEnvironmentNodes(), file.getPath());
        }

        FSTObjectInput in = new FSTObjectInput(new FileInputStream(file));
        AllDistancesSpeedsPair result = (AllDistancesSpeedsPair) in.readObject();
        in.close();
        return result;
    }


    public Environment<? extends EnvironmentNode, ? extends EnvironmentEdge> getEnvironment() {
        return environment;
    }
}
