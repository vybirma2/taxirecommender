package domain.actions;

import burlap.mdp.core.state.State;
import utils.DistanceGraphUtils;
import utils.Utils;
import domain.states.TaxiGraphState;

import static utils.DistanceGraphUtils.getDistanceBetweenNodes;
import static utils.Utils.*;

public class ActionUtils {




    public static boolean notGoingToChargingPreviously(State state){
        if (((TaxiGraphState)state).getPreviousActionId() == null){
            return true;
        }
        return !(((TaxiGraphState)state).getPreviousActionId() == ActionTypes.GOING_TO_CHARGING_STATION.getValue());
    }


    public static boolean notChargingInARow(State state){
        if (((TaxiGraphState)state).getPreviousActionId() == null){
            return true;
        }
        return !(((TaxiGraphState)state).getPreviousActionId() == ActionTypes.CHARGING_IN_CHARGING_STATION.getValue());
    }


    public static boolean shiftNotOver(State state, double actionTime){
        return ((TaxiGraphState)state).getTimeStamp() + actionTime < Utils.SHIFT_LENGTH + SHIFT_START_TIME;
    }


    public static boolean notPickUpPrevious(State state){
        if (((TaxiGraphState)state).getPreviousActionId() == null){
            return true;
        }
        return ((TaxiGraphState)state).getPreviousActionId() !=  ActionTypes.PICK_UP_PASSENGER.getValue();
    }


    public static boolean notOverCharging(State state, double energyCharged){
        return ((TaxiGraphState)state).getStateOfCharge() + energyCharged <= 100;
    }


    public static boolean notChargedALot(State state){
        return ((TaxiGraphState)state).getStateOfCharge() < MINIMAL_CHARGING_STATE_OF_CHARGE;
    }


    public static boolean notReturningBack(TaxiGraphState state, Integer toNodeId){
        if (state.getPreviousNode() == null){
            return true;
        }
        return !state.getPreviousNode().equals(toNodeId);
    }


    // TODO - get some good energy consumption estimate
    public static int getMovingEnergyConsumption(int fromNodeId, int toNodeId){
        double distance = getDistanceBetweenNodes(fromNodeId, toNodeId);
        return - (int)Math.ceil((distance/CAR_FULL_BATTERY_DISTANCE) * 100);
    }

    public static int getMovingEnergyConsumption(double distance){
        return - (int)Math.ceil((distance/CAR_FULL_BATTERY_DISTANCE) * 100);
    }


    public static int getActionEnergyConsumption(TaxiGraphState state, int toNodeId, double actionTime) {
        return ActionUtils.getMovingEnergyConsumption(state.getNodeId(), toNodeId);
    }

    public static int getEnergyConsumption(double distance) {
        return ActionUtils.getMovingEnergyConsumption(distance);
    }


    public static boolean notRunOutOfBattery(State state, int toNodeId, double actionTime){
        return ((TaxiGraphState)state).getStateOfCharge() + getActionEnergyConsumption((TaxiGraphState) state, toNodeId, actionTime) > 0;
    }

    public static boolean notRunOutOfBattery(State state, int energyConsumption){
        return ((TaxiGraphState)state).getStateOfCharge() + energyConsumption > 0;
    }


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
}
