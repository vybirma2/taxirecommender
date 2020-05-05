package domain.actions;

import domain.utils.Utils;
import domain.states.TaxiState;

import static domain.parameterestimation.EnergyConsumptionEstimator.getActionEnergyConsumption;
import static domain.utils.Utils.*;

/**
 * Class containing functions controlling whether concrete action is possible to do in given state
 */
public class ActionUtils {


    /*public static boolean notGoingToChargingPreviously(State state){
        return ((TaxiGraphState)state).isPossibleToGoToNextLocation();
    }*/


    public static boolean shiftNotOver(TaxiState state, int actionTime){
        return state.getTimeStamp() + actionTime < Utils.SHIFT_LENGTH + SHIFT_START_TIME;
    }


    public static boolean notOverCharging(TaxiState state, int energyCharged){
        return state.getStateOfCharge() + energyCharged <= 100;
    }


    public static boolean notChargedALot(int stateOfCharge){
        return stateOfCharge < MINIMAL_CHARGING_STATE_OF_CHARGE;
    }


    public static boolean notRunOutOfBattery(TaxiState state, int toNodeId){
        return state.getStateOfCharge() + getActionEnergyConsumption(state.getNodeId(), toNodeId) > MINIMAL_STATE_OF_CHARGE;
    }


    public static boolean notRunOutOfBattery(int stateOfCharge, int energyConsumption){
        return stateOfCharge + energyConsumption > MINIMAL_STATE_OF_CHARGE;
    }


/*
    public static boolean notReturningBack(TaxiGraphState state, Integer toNodeId){
        if (state.getPreviousNode() == null){
            return true;
        }
        return !state.getPreviousNode().equals(toNodeId);
    }
*/

/*
    public static boolean notRecentlyVisited(TaxiGraphState taxiGraphState, int toNodeId){
        int tripTime = DistanceGraphUtils.getTripTime(taxiGraphState.getNodeId(), toNodeId);

        if (taxiGraphState.getRecentlyVisitedNodes().containsKey(toNodeId)){

            if (taxiGraphState.getTimeStamp() + tripTime - taxiGraphState.getRecentlyVisitedNodes().get(toNodeId) < Utils.VISIT_INTERVAL &&
                    taxiGraphState.getTimeStamp() + tripTime - taxiGraphState.getRecentlyVisitedNodes().get(toNodeId) != 0){
                return false;
            } else {
                taxiGraphState.getRecentlyVisitedNodes().replace(toNodeId,
                        taxiGraphState.getTimeStamp() + tripTime);
            }
        } else {
            taxiGraphState.getRecentlyVisitedNodes().put(toNodeId, taxiGraphState.getTimeStamp() + tripTime);
        }
        return true;
    }
 */
}
