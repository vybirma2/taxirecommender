package domain.utils;

import domain.charging.*;
import domain.parameterestimation.DataSetReader;
import domain.parameterestimation.PragueDataSetReader;

import java.util.Comparator;


/**
 * Class containing all static parameters needed to be set to run evaluation, simulation...
 */
public class Utils {

    public static int SHIFT_START_TIME = 8*60;
    public static int SHIFT_LENGTH = 2 * 60;
    public static int STARTING_STATE_OF_CHARGE = 20;
    public static String ENVIRONMENT = "gridworld";
    public static DataSetReader DATA_SET_READER = new PragueDataSetReader();
    public static String INPUT_GRAPH_FILE_NAME = "prague.fst";
    public static String INPUT_STATION_FILE_NAME = "prague_charging_stations_full.json";
    public static String DATA_SET_NAME = "prague";
    public static int ONE_GRID_CELL_WIDTH = 2000;
    public static int ONE_GRID_CELL_HEIGHT = 2000;
    public static int NUM_OF_CLUSTERS = 125;


    public static void setUtilsParameters(int SHIFT_START_TIME, int SHIFT_LENGTH, int STARTING_STATE_OF_CHARGE, DataSetReader DATA_SET_READER,
                 String INPUT_GRAPH_FILE_NAME, String INPUT_STATION_FILE_NAME, String DATA_SET_NAME, String ENVIRONMENT) {
        Utils.SHIFT_START_TIME = SHIFT_START_TIME;
        Utils.SHIFT_LENGTH = SHIFT_LENGTH;
        Utils.STARTING_STATE_OF_CHARGE = STARTING_STATE_OF_CHARGE;
        Utils.DATA_SET_READER = DATA_SET_READER;
        Utils.INPUT_GRAPH_FILE_NAME = INPUT_GRAPH_FILE_NAME;
        Utils.INPUT_STATION_FILE_NAME = INPUT_STATION_FILE_NAME;
        Utils.DATA_SET_NAME = DATA_SET_NAME;
        Utils.ENVIRONMENT = ENVIRONMENT;
    }

    public static void setUtilsParameters(int SHIFT_START_TIME, int SHIFT_LENGTH, int STARTING_STATE_OF_CHARGE, DataSetReader DATA_SET_READER,
                                   String INPUT_GRAPH_FILE_NAME, String INPUT_STATION_FILE_NAME, String DATA_SET_NAME, String ENVIRONMENT,
                                   int ONE_GRID_CELL_HEIGHT, int ONE_GRID_CELL_WIDTH) {
        setUtilsParameters(SHIFT_START_TIME, SHIFT_LENGTH, STARTING_STATE_OF_CHARGE, DATA_SET_READER, INPUT_GRAPH_FILE_NAME,
                INPUT_STATION_FILE_NAME, DATA_SET_NAME, ENVIRONMENT);
        Utils.ONE_GRID_CELL_HEIGHT = ONE_GRID_CELL_HEIGHT;
        Utils.ONE_GRID_CELL_WIDTH = ONE_GRID_CELL_WIDTH;
    }


    public static void setUtilsParameters(int SHIFT_START_TIME, int SHIFT_LENGTH, int STARTING_STATE_OF_CHARGE, DataSetReader DATA_SET_READER,
                                   String INPUT_GRAPH_FILE_NAME, String INPUT_STATION_FILE_NAME, String DATA_SET_NAME, String ENVIRONMENT,
                                   int NUM_OF_CLUSTERS) {
        setUtilsParameters(SHIFT_START_TIME, SHIFT_LENGTH, STARTING_STATE_OF_CHARGE, DATA_SET_READER, INPUT_GRAPH_FILE_NAME,
                INPUT_STATION_FILE_NAME, DATA_SET_NAME, ENVIRONMENT);
        Utils.NUM_OF_CLUSTERS = NUM_OF_CLUSTERS;
    }

    public static final double TAXI_FARE_FOR_KM = 30;
    public static final double TAXI_START_JOURNEY_FEE = 30;
    public static final int ELECTRIC_VEHICLE_DRIVING_RANGE = 300;
    public static double BATTERY_CAPACITY = 40;
    public static double DISCOUNT_FACTOR_TRIP_CHOOSING = 0.5;


    public static final int STAYING_INTERVAL = 1;
    public static final int NUM_OF_ACTION_TYPES = 5;
    public static final int SIZE_OF_DATASET = 1000000;
    public static final double TRIP_OFFER_PROBABILITY = 0.05;
    public static final int NUM_OF_CHARGING_STATIONS = 75;
    public static final int MINIMAL_STATE_OF_CHARGE = 5;
    public static final int MAX_TRIP_LENGTH = 180;
    public static final int MAX_KMEANS_ITERATIONS = 1000;
    public static final int ESTIMATION_EPISODE_LENGTH = 30;
    public static final int NUM_OF_CHARGING_LENGTH_POSSIBILITIES = 10;
    public static final int NUM_OF_BEST_CHARGING_STATIONS_TO_GO_TO = 10;
    public static final Comparator<TripToChargingStation> tripToChargingStationComparator = new ClosestStationComparator();
    public static final Comparator<ChargingConnection> chargingConnectionComparator = new ChargingConnectionComparator();
    public static ChargingStationStateOrder CHARGING_STATION_STATE_ORDER = null;
    public static final int NUM_OF_SHIFTS_IN_EXPERIMENTS = 1000;


    public static void setChargingStationStateOrder(ChargingStationStateOrder chargingStationStateOrder){
        CHARGING_STATION_STATE_ORDER = chargingStationStateOrder;
    }
}

