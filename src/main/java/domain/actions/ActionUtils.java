package domain.actions;

import burlap.mdp.core.state.State;
import utils.Utils;
import domain.states.TaxiGraphState;

import static utils.DistanceGraphUtils.getDistanceBetweenNodes;
import static utils.DistanceGraphUtils.getSpeedBetweenNodes;
import static utils.Utils.*;

public class ActionUtils {


    public static boolean notGoingToChargingPreviously(State state){
        return !(((TaxiGraphState)state).getPreviousActionId() == ActionTypes.GOING_TO_CHARGING_STATION.getValue());
    }


    public static boolean notChargingInARow(State state){
        return !(((TaxiGraphState)state).getPreviousActionId() == ActionTypes.CHARGING_IN_CHARGING_STATION.getValue());
    }


    public static boolean shiftNotOver(State state, double actionTime){
        return ((TaxiGraphState)state).getTimeStamp() + actionTime < Utils.SHIFT_LENGTH + SHIFT_START_TIME;
    }


    public static boolean notOverCharging(State state, double energyCharged){
        return ((TaxiGraphState)state).getStateOfCharge() + energyCharged <= 100;
    }


    public static boolean notChargedALot(State state){
        return ((TaxiGraphState)state).getStateOfCharge() < MINIMAL_CHARGING_STATE_OF_CHARGE;
    }


    public static boolean notReturningBack(TaxiGraphState state, int toNodeId){
        return state.getPreviousNode() != toNodeId;
    }


    // TODO - get some good energy consumption estimate
    public static double getMovingEnergyConsumption(int fromNodeId, int toNodeId){
        double distance = getDistanceBetweenNodes(fromNodeId, toNodeId);
        return - (distance/CAR_FULL_BATTERY_DISTANCE) * 100;
    }



    public static double getActionEnergyConsumption(TaxiGraphState state, int toNodeId, double actionTime) {
        return ActionUtils.getMovingEnergyConsumption(state.getNodeId(), toNodeId);
    }


    public static boolean notRunOutOfBattery(State state, int toNodeId, double actionTime){
        return ((TaxiGraphState)state).getStateOfCharge() + getActionEnergyConsumption((TaxiGraphState) state, toNodeId, actionTime) > 0;
    }
}
