package evaluation;

import domain.TaxiModel;
import domain.actions.ActionTypes;
import domain.actions.MeasurableAction;
import domain.environmentrepresentation.EnvironmentNode;
import domain.states.TaxiState;
import parameterestimation.ParameterEstimator;
import parameterestimation.TaxiTrip;
import utils.DistanceGraphUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BaseMethodAgent extends Agent {

    private final int BATTERY_LOW_LEVEL_VALUE = 10;

    private ParameterEstimator parameterEstimator;
    private int centreNode;
    private HashMap<Integer, Integer> pickupsInNodes = new HashMap<>();
    private boolean inChargingStation = false;
    private boolean notChargingPreviously = false;


    private LinkedList<Integer> pathToCenterNode;

    public BaseMethodAgent(TaxiModel taxiModel, ParameterEstimator parameterEstimator) {
        super(taxiModel);
        this.parameterEstimator = parameterEstimator;
        init();
    }


    private void init(){
        int maxValue = 0;
        for (TaxiTrip trip : parameterEstimator.getTaxiTrips()){
            if (pickupsInNodes.containsKey(trip.getFromEnvironmentNode().getNodeId())){
                pickupsInNodes.replace(trip.getFromEnvironmentNode().getNodeId(), pickupsInNodes.get(trip.getFromEnvironmentNode().getNodeId()) + 1);
                if (maxValue < pickupsInNodes.get(trip.getFromEnvironmentNode().getNodeId()) + 1){
                    maxValue = pickupsInNodes.get(trip.getFromEnvironmentNode().getNodeId()) + 1;
                    centreNode = trip.getFromEnvironmentNode().getNodeId();
                }
            } else {
                pickupsInNodes.put(trip.getFromEnvironmentNode().getNodeId(), 1);
                if (maxValue < 1){
                    maxValue = 1;
                    centreNode = trip.getFromEnvironmentNode().getNodeId();
                }
            }
        }
    }


    @Override
    public MeasurableAction chooseAction(TaxiState currentState, List<MeasurableAction> actions) {
        if (inChargingStation && notChargingPreviously){
            return chooseCharging(actions);
        } else if (inChargingStation) {
            return chooseFromChargingStation(actions);
        } else if (currentState.getStateOfCharge() <= BATTERY_LOW_LEVEL_VALUE){
            return chooseGoingToCharging(actions);
        } else if (currentState.getNodeId() == centreNode){
            return chooseStaying(actions);
        } else {
            return chooseGoingToCenter(currentState, actions);
        }
    }


    private MeasurableAction chooseGoingToCharging(List<MeasurableAction> actions){
        pathToCenterNode = null;
        for (MeasurableAction action : actions){
            if (action.getActionId() == ActionTypes.GOING_TO_CHARGING_STATION.getValue()){
                inChargingStation = true;
                notChargingPreviously = true;
                return action;
            }
        }
        return chooseStaying(actions);
    }

    private MeasurableAction chooseCharging(List<MeasurableAction> actions){
        pathToCenterNode = null;
        int max = 0;
        MeasurableAction maxCharging = null;
        for (MeasurableAction action : actions){
            if (action.getActionId() == ActionTypes.CHARGING_IN_CHARGING_STATION.getValue()){
                if (action.getLength() > max){
                    max = action.getLength();
                    maxCharging = action;
                }
            }
        }
        if (maxCharging != null){
            inChargingStation = true;
            notChargingPreviously = false;
            return maxCharging;
        }
        return chooseStaying(actions);
    }

    private MeasurableAction chooseStaying(List<MeasurableAction> actions){
        for (MeasurableAction action : actions){
            if (action.getActionId() == ActionTypes.STAYING_IN_LOCATION.getValue()){
                inChargingStation = false;
                notChargingPreviously = false;
                return action;
            }
        }

        return actions.get(0);
    }

    private MeasurableAction chooseFromChargingStation(List<MeasurableAction> actions){
        for (MeasurableAction action : actions){
            if (action.getActionId() == ActionTypes.TO_NEXT_LOCATION.getValue()){
                inChargingStation = false;
                notChargingPreviously = false;
                return action;
            }
        }

        return actions.get(0);
    }


    private MeasurableAction chooseGoingToCenter(TaxiState state, List<MeasurableAction> actions){
        if (pathToCenterNode == null || pathToCenterNode.isEmpty()){
            pathToCenterNode = DistanceGraphUtils.aStarEnvironment(state.getNodeId(), centreNode);
            assert pathToCenterNode != null;
            pathToCenterNode.pollFirst();
        }

        Integer nextNode = pathToCenterNode.pollFirst();
        assert nextNode != null;

        for (MeasurableAction action : actions){
            if (action.getActionId() == ActionTypes.TO_NEXT_LOCATION.getValue() && action.getToNodeId() == nextNode){
                inChargingStation = false;
                notChargingPreviously = false;
                return action;
            }
        }

        return chooseStaying(actions);
    }


    @Override
    public boolean tripOffer(TaxiState currentState, Integer trip) {
        pathToCenterNode = null;
        return currentState.getStateOfCharge() > this.BATTERY_LOW_LEVEL_VALUE;
    }
}
