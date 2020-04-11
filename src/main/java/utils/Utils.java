package utils;

import charging.*;
import parameterestimation.DataSetReader;
import parameterestimation.PragueDataSetReader;

import java.util.Comparator;

public class Utils {

    public static final int NUM_OF_CLUSTERS = 100;


    public static final int SHIFT_LENGTH = 4 * 60;
    public static final int STAYING_INTERVAL = 10;

    public static final double COST_FOR_KW = 5.5;
    public static final double BATTERY_CAPACITY = 40;
    public static final double MINIMAL_CHARGING_STATE_OF_CHARGE = 80;

    public static final int NUM_OF_ACTION_TYPES = 5;

    public static final double TAXI_COST_FOR_KM = 30;
    public static final double TAXI_START_JOURNEY_FEE = 30;

    public static final int VISIT_INTERVAL = 30;

    public static final int NUM_OF_NEIGHBOURS = 8;


    public static final boolean VISUALIZE_ENVIRONMENT = false;


    public static final int MAX_KMEANS_ITERATIONS = 1000;

    public static final int ONE_GRID_CELL_WIDTH = 10000;
    public static final int ONE_GRID_CELL_HEIGHT = 10000;

    public static final double MAX_NODE_FITTING_DISTANCE = 0.2;

    public static final DataSetReader DATA_SET_READER = new PragueDataSetReader();

    public static final int ESTIMATION_EPISODE_LENGTH = 30;

    public static final int SHIFT_START_TIME = 7*60;

    public static final int CAR_FULL_BATTERY_DISTANCE = 300;

    public static final int NUM_OF_CHARGING_LENGTH_POSSIBILITIES = 3;

    public static final Comparator<TripToChargingStation> tripToChargingStationComparator = new ClosestStationComparator();

    public static final Comparator<ChargingConnection> chargingConnectionComparator = new ChargingConnectionComparator();

    public static final int NUM_OF_BEST_CHARGING_STATIONS_TO_GO_TO = 3;


    public static ChargingStationStateOrder CHARGING_STATION_STATE_ORDER = null;

    public static void setChargingStationStateOrder(ChargingStationStateOrder chargingStationStateOrder){
        CHARGING_STATION_STATE_ORDER = chargingStationStateOrder;
    }
}

