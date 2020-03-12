package domain.actions;

import burlap.mdp.core.state.State;
import utils.Utils;
import domain.states.TaxiGraphState;

import static utils.DistanceGraphUtils.getDistanceBetweenNodes;
import static utils.DistanceGraphUtils.getSpeedBetweenNodes;
import static utils.Utils.*;

public class ActionUtils {


    public static boolean notGoingToChargingPreviously(State state){
        return !(((TaxiGraphState)state).getPreviousAction() == ActionTypes.GOING_TO_CHARGING_STATION.getValue());
    }


    public static boolean notChargingInARow(State state){
        return !(((TaxiGraphState)state).getPreviousAction() == ActionTypes.CHARGING_IN_CHARGING_STATION.getValue());
    }


    public static boolean shiftNotOver(State state, double actionTime){
        return ((TaxiGraphState)state).getTimeStamp() + actionTime < Utils.SHIFT_LENGTH;
    }


    public static boolean notFullyCharged(State state){
        return ((TaxiGraphState)state).getStateOfCharge() < 100;
    }


    public static boolean notReturningBack(TaxiGraphState state, int toNodeId){
        return state.getPreviousNode() != toNodeId;
    }


    // TODO - repaire for speed and distance on every edge not average for whole trip
    public static double getMovingEnergyConsumption(int fromNodeId, int toNodeId){
        double speed = getSpeedBetweenNodes(fromNodeId, toNodeId);
        double distance = getDistanceBetweenNodes(fromNodeId, toNodeId);
        return - (RIDER_AGGRESSIVENESS * (ALPHA_1 * speed * speed + ALPHA_2*speed + ALPHA_3) * distance)/2000;
    }


    public static double getAuxiliaryEnergyConsumption(double actionTime){
        return - LOADING * (actionTime/60);
    }

}
