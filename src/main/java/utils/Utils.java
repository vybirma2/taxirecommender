package utils;

import charging.ChargingConnection;
import charging.ChargingConnectionComparator;
import charging.ClosestStationComparator;
import charging.TripToChargingStation;
import parameterestimation.DataSetReader;
import parameterestimation.PragueDataSetReader;

import java.util.Comparator;

public class Utils {
    public static final double SHIFT_LENGTH = 12 * 60;
    public static final double STAYING_INTERVAL = 10;

    public static final String VAR_NODE = "node";
    public static final String VAR_STATE_OF_CHARGE = "state_of_charge";
    public static final String VAR_TIMESTAMP = "timestamp";
    public static final String VAR_PREVIOUS_ACTION = "previous_action";
    public static final String VAR_PREVIOUS_STATE = "previous_node";

    public static final double COST_FOR_KW = 5.5;
    public static final double BATTERY_CAPACITY = 40;
    public static final double MINIMAL_CHARGING_STATE_OF_CHARGE = 80;

    public static final int ONE_GRID_CELL_WIDTH = 20000;
    public static final int ONE_GRID_CELL_HEIGHT = 15000;

    public static final double RIDER_AGGRESSIVENESS = 1.2;
    public static final double ALPHA_1 = 0.1554;
    public static final double ALPHA_2 = -5.4634;
    public static final double ALPHA_3 = 189.297;

    public static final double LOADING = 1.;

    public static final double DEFAULT_SPEED = 50;


    public static final double MAX_NODE_FITTING_DISTANCE = 0.2;


    public static final DataSetReader DATA_SET_READER = new PragueDataSetReader();


    public static final int ESTIMATION_EPISODE_LENGTH = 60;

    public static final int SHIFT_START_TIME = 8*60;


    public static final int CAR_FULL_BATTERY_DISTANCE = 300;

    public static final int NUM_OF_CHARGING_LENGTH_POSSIBILITIES = 3;

    public static Comparator<TripToChargingStation> tripToChargingStationComparator = new ClosestStationComparator();

    public static Comparator<ChargingConnection> chargingConnectionComparator = new ChargingConnectionComparator();

    public static int NUM_OF_BEST_CHARGING_STATIONS_TO_GO_TO = 1;
}
