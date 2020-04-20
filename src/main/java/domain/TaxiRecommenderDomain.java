package domain;

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
import domain.environmentrepresentation.gridenvironment.GridEnvironment;
import domain.environmentrepresentation.kmeansenvironment.KMeansEnvironment;
import org.json.simple.parser.ParseException;
import org.nustaq.serialization.FSTObjectInput;
import parameterestimation.ParameterEstimator;
import parameterestimation.TaxiTrip;
import utils.*;
import visualization.MapVisualizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static domain.actions.ActionTypes.*;

/**
 * Class responsible for loading all needed data, creating all objects needed for planning and generating domain
 * for the following planning
 */
public class TaxiRecommenderDomain {

    private final static Logger LOGGER = Logger.getLogger(TaxiRecommenderDomain.class.getName());
    private static ArrayList<TaxiTrip> taxiTrips;


    private Environment<? extends EnvironmentNode, ? extends EnvironmentEdge> environment;
    private Graph<RoadNode, RoadEdge> osmGraph;
    private Collection<RoadNode> osmNodes;
    private List<ChargingStation> chargingStations;

    private List<TaxiActionType> actionTypes = new ArrayList<>();

    private String roadGraphInputFileFullPath;
    private String roadGraphInputFile;
    private String chargingStationsInputFileFullPath;
    private String chargingStationsInputFile;

    private ParameterEstimator parameterEstimator;
    private TaxiModel taxiModel;

    private ArrayList<HashMap<Integer, ArrayList<Integer>>> transitions = new ArrayList<>(Utils.NUM_OF_ACTION_TYPES);


    public TaxiRecommenderDomain(String roadGraphInputFile, String chargingStationsInputFile,
                                 Environment<? extends EnvironmentNode, ? extends EnvironmentEdge> environment){
        this.roadGraphInputFileFullPath = "data/graphs/" + roadGraphInputFile;
        this.roadGraphInputFile = roadGraphInputFile;
        this.chargingStationsInputFileFullPath = "data/chargingstations/" + chargingStationsInputFile;
        this.chargingStationsInputFile = chargingStationsInputFile;
        this.environment = environment;
        generateDomain();
    }


    /**
     * Loading all data from extern files, estimating parameters, setting all needed objects
     * @return generated domain
     */

    public void generateDomain() {
        try {
            loadData();
            this.taxiModel =  new TaxiModel(actionTypes);
            addAllActionTypes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void addAllActionTypes(){
        actionTypes.add(new StayingInLocationActionType(STAYING_IN_LOCATION.getValue(), transitions.get(STAYING_IN_LOCATION.getValue())));
        actionTypes.add(new NextLocationActionType(TO_NEXT_LOCATION.getValue(), transitions.get(TO_NEXT_LOCATION.getValue())));
        actionTypes.add(new GoingToChargingStationActionType(GOING_TO_CHARGING_STATION.getValue(), transitions.get(GOING_TO_CHARGING_STATION.getValue())));
        actionTypes.add(new ChargingActionType(CHARGING_IN_CHARGING_STATION.getValue(), transitions.get(CHARGING_IN_CHARGING_STATION.getValue())));
        actionTypes.add(new PickUpPassengerActionType(PICK_UP_PASSENGER.getValue(), transitions.get(PICK_UP_PASSENGER.getValue()), parameterEstimator));
    }

    private void visualizeEnvironment(){

        new Thread() {
            @Override
            public void run() {
                MapVisualizer.main(null);
            }
        }.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MapVisualizer.addRoadNodesToMap(this.osmNodes);

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private void loadData() throws Exception {
        loadGraph();

        //visualizeEnvironment();

        loadTaxiTripDataset();

        setEnvironment();

        loadChargingStations();

        computeShortestPathsToChargingStations();

        setTransitions();
    }


    private void setEnvironment() throws IOException, ClassNotFoundException {
        this.environment.setOsmGraph(osmGraph);

        DistanceGraphUtils.setNodes(this.environment.getEnvironmentNodes());
        DistanceGraphUtils.setGraph(this.environment.getEnvironmentGraph());
        environment.setTaxiTripEnvironmentNodes(taxiTrips);

        this.parameterEstimator = new ParameterEstimator(taxiTrips);
        this.parameterEstimator.estimateParameters();
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
        AllDistancesSpeedsPair allDistancesSpeedsPair = getChargingStationDistanceSpeedTime("distance_speed_" + roadGraphInputFile);
        DistanceGraphUtils.setChargingStationDistancesSpeedTime(allDistancesSpeedsPair.getDistanceSpeedTime());
        Utils.setChargingStationStateOrder(new DistanceChargingStationStateOrder(allDistancesSpeedsPair.getDistanceSpeedTime(), this
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
    }


    private void setTransitions() {
        for (int i = 0; i < Utils.NUM_OF_ACTION_TYPES; i++){
            transitions.add(new HashMap<>());
        }

        ArrayList<Integer> trans;
        for (EnvironmentNode node : environment.getEnvironmentNodes()) {

            // setting transition between node itself - action of staying in location, i.e. prob 1
            trans = new ArrayList<>();
            trans.add(node.getNodeId());
            this.transitions.get(STAYING_IN_LOCATION.getValue()).put(node.getNodeId(), trans);

            // setting transitions between neighbouring nodes - action of going to next location, i.e. prob 1
            setToNextLocationTransitions(node);

            // setting transitions between current node and all available charging stations
            trans = new ArrayList<>();
            this.transitions.get(GOING_TO_CHARGING_STATION.getValue()).put(node.getNodeId(), trans);
            for (ChargingStation station : chargingStations) {
                trans.add(station.getRoadNode().getId());
            }
        }


        for (ChargingStation chargingStation : chargingStations){
            EnvironmentNode node = DistanceGraphUtils.chooseEnvironmentNode(chargingStation.getRoadNode().getLongitude(), chargingStation.getRoadNode().getLatitude());

            trans = new ArrayList<>();
            trans.add(chargingStation.getRoadNode().getId());
            this.transitions.get(ActionTypes.CHARGING_IN_CHARGING_STATION.getValue()).put(chargingStation.getRoadNode().getId(), trans);
            trans = new ArrayList<>();
            trans.add(node.getNodeId());
            this.transitions.get(TO_NEXT_LOCATION.getValue()).put(chargingStation.getRoadNode().getId(), trans);
        }
    }


    private void setToNextLocationTransitions(EnvironmentNode node){
        Set<Integer> neighbours = node.getNeighbours();
        ArrayList<Integer> trans = new ArrayList<>();
        this.transitions.get(TO_NEXT_LOCATION.getValue()).put(node.getNodeId(), trans);
        trans.addAll(neighbours);

        HashMap<Integer, Double> destinationProbabilities = parameterEstimator.getDestinationProbabilitiesInNode(node.getNodeId());
        trans = new ArrayList<>();
        this.transitions.get(ActionTypes.PICK_UP_PASSENGER.getValue()).put(node.getNodeId(), trans);
        if (destinationProbabilities != null){
            trans.addAll(destinationProbabilities.keySet());
        }
    }


    private AllDistancesSpeedsPair getChargingStationDistanceSpeedTime(String inputFile) throws IOException, ClassNotFoundException {
        File file;
        if (this.environment instanceof KMeansEnvironment) {
            file = new File("data/programdata/" + Utils.NUM_OF_CLUSTERS + "_" + inputFile);
        } else if (this.environment instanceof GridEnvironment) {
            file = new File("data/programdata/" + Utils.ONE_GRID_CELL_HEIGHT + "x" + Utils.ONE_GRID_CELL_WIDTH + "_" + inputFile);
        } else {
            file = new File("data/programdata/fullenvironment_" + inputFile);
        }

        if(!file.exists()){
            DataSerialization.serializeChargingStationDistancesAndSpeed(chargingStations, environment.getEnvironmentNodes(), file.getPath());
        }

        FSTObjectInput in = new FSTObjectInput(new FileInputStream(file));
        AllDistancesSpeedsPair result = (AllDistancesSpeedsPair) in.readObject();
        in.close();
        return result;
    }


    public TaxiModel getTaxiModel() {
        return taxiModel;
    }


    public Environment<? extends EnvironmentNode, ? extends EnvironmentEdge> getEnvironment() {
        return environment;
    }

    public List<TaxiActionType> getActionTypes() {
        return actionTypes;
    }


    public static  ArrayList<TaxiTrip> getTaxiTrips(){
        return taxiTrips;
    }

    public ParameterEstimator getParameterEstimator() {
        return parameterEstimator;
    }
}
