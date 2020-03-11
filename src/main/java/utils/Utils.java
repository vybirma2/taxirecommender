package utils;

import parameterestimation.DataSetReader;
import parameterestimation.PragueDataSetReader;

public class Utils {
    public static final double SHIFT_LENGTH = 480;
    public static final double STAYING_INTERVAL = 30;
    public static final double CHARGING_INTERVAL = 60;

    public static final String VAR_NODE = "node";
    public static final String VAR_STATE_OF_CHARGE = "state_of_charge";
    public static final String VAR_TIMESTAMP = "timestamp";
    public static final String VAR_PREVIOUS_ACTION = "previous_action";
    public static final String VAR_PREVIOUS_NODE = "previous_node";

    public static final double COST_FOR_KW = 5.5;
    public static final double BATTERY_CAPACITY = 40;

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


}
