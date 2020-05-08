package domain;

import domain.charging.ChargingStation;
import domain.charging.ChargingStationReader;
import domain.charging.DistanceChargingStationStateOrder;
import cz.agents.basestructures.Graph;
import cz.agents.multimodalstructures.edges.RoadEdge;
import cz.agents.multimodalstructures.nodes.RoadNode;
import domain.actions.*;
import domain.environmentrepresentation.Environment;
import domain.environmentrepresentation.EnvironmentEdge;
import domain.environmentrepresentation.EnvironmentNode;
import domain.environmentrepresentation.gridworldenvironment.GridWorldEnvironment;
import domain.environmentrepresentation.kmeansenvironment.KMeansEnvironment;
import domain.environmentrepresentation.osmenvironment.OSMEnvironment;
import jdk.jshell.execution.Util;
import org.json.simple.parser.ParseException;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import domain.parameterestimation.ParameterEstimator;
import domain.parameterestimation.TaxiTrip;
import domain.utils.*;
import visualization.MapVisualizer;

import java.io.*;
import java.util.*;

import static domain.actions.ActionTypes.*;
import static domain.utils.Utils.ENVIRONMENT;
import static domain.utils.Utils.INPUT_STATION_FILE_NAME;

/**
 * Class responsible for loading all needed data, creating all objects needed for planning and generating domain
 * for the following planning
 */
public class TaxiRecommenderDomain implements Serializable {

    private final ArrayList<HashMap<Integer, ArrayList<Integer>>> transitions = new ArrayList<>(Utils.NUM_OF_ACTION_TYPES);
    private Environment<? extends EnvironmentNode, ? extends EnvironmentEdge> environment;
    private List<ChargingStation> chargingStations;
    private ArrayList<TaxiTrip>  taxiTrips;
    private Graph<RoadNode, RoadEdge> osmGraph;

    private final List<TaxiActionType> actionTypes = new ArrayList<>();

    private final String roadGraphInputFileFullPath;
    private final String chargingStationsInputFileFullPath;
    private final String chargingStationsInputFile;
    private final String environmentType;

    private ParameterEstimator parameterEstimator;



    public TaxiRecommenderDomain(String environmentType){
        this.roadGraphInputFileFullPath = "data/graphs/" + Utils.INPUT_GRAPH_FILE_NAME;
        this.chargingStationsInputFileFullPath = "data/chargingstations/" + INPUT_STATION_FILE_NAME;
        this.chargingStationsInputFile = INPUT_STATION_FILE_NAME;
        this.environmentType = environmentType;
        generateDomain();
    }


    /**
     * Loading all data from extern files, estimating parameters, setting all needed objects
     * @return generated domain
     */

    public void generateDomain() {
        try {
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() throws Exception {
        loadGraph();

        loadTaxiTripDataset();

        setEnvironment();

        setParameterEstimator();

        loadChargingStations();

        computeShortestPathsToChargingStations();

        setTransitions();

        addAllActionTypes();
    }

    private void addAllActionTypes(){
        actionTypes.add(new StayingInLocationActionType(STAYING_IN_LOCATION.getValue(), transitions.get(STAYING_IN_LOCATION.getValue())));
        actionTypes.add(new NextLocationActionType(TO_NEXT_LOCATION.getValue(), transitions.get(TO_NEXT_LOCATION.getValue())));
        actionTypes.add(new GoingToChargingStationActionType(GOING_TO_CHARGING_STATION.getValue(), transitions.get(GOING_TO_CHARGING_STATION.getValue())));
        actionTypes.add(new ChargingActionType(CHARGING_IN_CHARGING_STATION.getValue(), transitions.get(CHARGING_IN_CHARGING_STATION.getValue())));
        actionTypes.add(new PickUpPassengerActionType(PICK_UP_PASSENGER.getValue(), transitions.get(PICK_UP_PASSENGER.getValue()), parameterEstimator));
    }

    private void estimateParameters(File file) throws IOException {
        long startTime;
        long stopTime;

        System.out.println("Estimating parameters started...");
        startTime = System.nanoTime();

        this.parameterEstimator = new ParameterEstimator(taxiTrips);
        this.parameterEstimator.estimateParameters();
        stopTime  = System.nanoTime();

        FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream(file));
        out.writeObject(parameterEstimator);
        out.close();

        System.out.println("Estimating finished in " + (stopTime - startTime)/1000000000. + " s.");
    }

    private void readParameterEstimator(File file) throws IOException, ClassNotFoundException {
        FSTObjectInput in = new FSTObjectInput(new FileInputStream(file));
        parameterEstimator = (ParameterEstimator) in.readObject();
        in.close();
    }


    private void setParameterEstimator() throws IOException, ClassNotFoundException {
        File file = new File("data/programdata/estimated_parameters_tS" + Utils.SHIFT_START_TIME +
                "sL" + Utils.SHIFT_LENGTH + ".fst");

        if (!file.exists()){
            estimateParameters(file);
        } else {
            readParameterEstimator(file);
        }
    }

    private void setEnvironment() {
        long startTime;
        long stopTime;

        System.out.println("Setting environment..");
        startTime = System.nanoTime();

        createEnvironment();

        for (TaxiTrip trip : taxiTrips){
            trip.setFromEnvironmentNode(DistanceGraphUtils.chooseEnvironmentNode(trip.getPickUpLongitude(), trip.getPickUpLatitude()).getNodeId());
            trip.setToEnvironmentNode(DistanceGraphUtils.chooseEnvironmentNode(trip.getDestinationLongitude(), trip.getDestinationLatitude()).getNodeId());
        }

        stopTime  = System.nanoTime();
        System.out.println("Setting finished in " + (stopTime - startTime)/1000000000. + " s.");

    }


    private void loadGraph() throws Exception {
        long startTime;
        long stopTime;

        System.out.println("Loading graph...");
        startTime = System.nanoTime();
        osmGraph = GraphLoader.loadGraph(roadGraphInputFileFullPath);

        DistanceGraphUtils.setOsmGraph(osmGraph);
        Collection<RoadNode> osmNodes = osmGraph.getAllNodes();
        DistanceGraphUtils.setOsmNodes(osmNodes);

        stopTime  = System.nanoTime();

        System.out.println("Loading finished in " + (stopTime - startTime)/1000000000. + " s, loaded " + osmNodes.size() + " nodes, " + osmGraph.getAllEdges().size() + " edges.");
    }


    private void loadChargingStations() throws IOException, ParseException, ClassNotFoundException {
        long startTime;
        long stopTime;

        System.out.println("Loading domain.charging stations...");
        startTime = System.nanoTime();
        chargingStations = new ArrayList<>(ChargingStationReader.readChargingStations(chargingStationsInputFileFullPath, chargingStationsInputFile));
        DistanceGraphUtils.setChargingStations(chargingStations);
        stopTime  = System.nanoTime();
        System.out.println("Loading finished in " + (stopTime - startTime)/1000000000. + " s, loaded " + chargingStations.size() + " domain.charging stations.");
    }


    private void computeShortestPathsToChargingStations() throws IOException, ClassNotFoundException {
        long startTime;
        long stopTime;

        System.out.println( "Computing shortest paths to domain.charging stations...");
        startTime = System.nanoTime();
        AllDistancesSpeedsPair allDistancesSpeedsPair = getChargingStationDistanceSpeedTime("distance_speed_" + Utils.DATA_SET_NAME);
        DistanceGraphUtils.setChargingStationDistancesSpeedTime(allDistancesSpeedsPair.getDistanceSpeedTime());
        Utils.setChargingStationStateOrder(new DistanceChargingStationStateOrder(allDistancesSpeedsPair.getDistanceSpeedTime(), this
        .environment.getNodes()));
        stopTime  = System.nanoTime();
        System.out.println("Computing finished in " + (stopTime - startTime)/1000000000. + "s.");
    }


    private void loadTaxiTripDataset() throws IOException, ClassNotFoundException {
        long startTime;
        long stopTime;

        System.out.println("Reading taxi trip dataset...");
        startTime = System.nanoTime();
        taxiTrips = Utils.DATA_SET_READER.readDataSet();
        stopTime  = System.nanoTime();
        System.out.println("Reading finished in " + (stopTime - startTime)/1000000000. + "s.");
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

            // setting transitions between current node and all available domain.charging stations
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
        } else if (this.environment instanceof GridWorldEnvironment) {
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

    private void createEnvironment(){
        switch (environmentType){
            case "kmeans":
                this.environment = new KMeansEnvironment(osmGraph, taxiTrips);
                break;
            case "gridworld":
                this.environment = new GridWorldEnvironment(osmGraph, taxiTrips);
                break;
            case "osm":
                this.environment = new OSMEnvironment(osmGraph, taxiTrips);
                break;
            default:
                throw new IllegalArgumentException("Not known environment");
        }
    }

    public Environment<? extends EnvironmentNode, ? extends EnvironmentEdge> getEnvironment() {
        return environment;
    }

    public List<TaxiActionType> getActionTypes() {
        return actionTypes;
    }


    public  ArrayList<TaxiTrip> getTaxiTrips(){
        return taxiTrips;
    }

    public ParameterEstimator getParameterEstimator() {
        return parameterEstimator;
    }
}
