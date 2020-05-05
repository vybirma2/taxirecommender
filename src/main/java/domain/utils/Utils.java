package domain.utils;

import domain.charging.*;
import domain.environmentrepresentation.Environment;
import domain.environmentrepresentation.kmeansenvironment.KMeansEnvironment;
import domain.parameterestimation.DataSetReader;
import domain.parameterestimation.PragueDataSetReader;

import java.util.Comparator;

public class Utils {

    public static final int NUM_OF_CLUSTERS = 100;

    public static final int SHIFT_LENGTH = 12 * 60;
    public static final int STARTING_STATE_OF_CHARGE = 20;


    public static final int STAYING_INTERVAL = 1;

    public static final double COST_FOR_KW = 5.5;
    public static final double BATTERY_CAPACITY = 40;
    public static final double MINIMAL_CHARGING_STATE_OF_CHARGE = 80;

    public static final int NUM_OF_ACTION_TYPES = 5;

    public static final int SIZE_OF_DATASET = 1000000;

    public static final double TRIP_OFFER_PROBABILITY = 0.5;

    public static final int NUM_OF_NODES = NUM_OF_CLUSTERS + 123;

    public static final double TAXI_COST_FOR_KM = 30;
    public static final double TAXI_START_JOURNEY_FEE = 30;

    public static final int VISIT_INTERVAL = 30;

    public static final int NUM_OF_NEIGHBOURS = 8;

    public static final int NUM_OF_CHARGING_STATIONS = 75;


    public static final int MINIMAL_STATE_OF_CHARGE = 5;


    public static final boolean VISUALIZE_ENVIRONMENT = true;

    public static final int MAX_TRIP_LENGTH = 180;


    public static final int MAX_KMEANS_ITERATIONS = 1000;

    public static final int ONE_GRID_CELL_WIDTH = 2000;
    public static final int ONE_GRID_CELL_HEIGHT = 2000;

    public static final double MAX_NODE_FITTING_DISTANCE = 0.2;

    public static final Environment ENVIRONMENT = new KMeansEnvironment();
    public static final DataSetReader DATA_SET_READER = new PragueDataSetReader();
    public static final String INPUT_GRAPH_FILE_NAME = "prague.fst";
    public static final String INPUT_STATION_FILE_NAME = "prague_charging_stations_full.json";
    public static final String DATA_SET_NAME = "prague";


    public static final int ESTIMATION_EPISODE_LENGTH = 30;

    public static final int SHIFT_START_TIME = 1*60;

    public static final int CAR_FULL_BATTERY_DISTANCE = 300;

    public static final int NUM_OF_CHARGING_LENGTH_POSSIBILITIES = 10;

    public static final Comparator<TripToChargingStation> tripToChargingStationComparator = new ClosestStationComparator();

    public static final Comparator<ChargingConnection> chargingConnectionComparator = new ChargingConnectionComparator();

    public static double ejnhd = 87.9;
    public static final int NUM_OF_BEST_CHARGING_STATIONS_TO_GO_TO = 10;


    public static ChargingStationStateOrder CHARGING_STATION_STATE_ORDER = null;

    public static void setChargingStationStateOrder(ChargingStationStateOrder chargingStationStateOrder){
        CHARGING_STATION_STATE_ORDER = chargingStationStateOrder;
    }
}

