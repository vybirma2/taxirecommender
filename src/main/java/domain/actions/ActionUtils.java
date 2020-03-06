package domain.actions;

import burlap.mdp.core.state.State;
import utils.Utils;
import domain.states.TaxiGraphState;

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
}
