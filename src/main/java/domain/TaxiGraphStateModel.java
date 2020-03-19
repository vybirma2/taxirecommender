package domain;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import domain.actions.ActionTypes;
import domain.actions.GoingToChargingStationAction;
import domain.actions.MeasurableAction;
import domain.actions.NextLocationAction;
import domain.states.TaxiGraphState;
import utils.Utils;

import java.util.*;

import static utils.Utils.VAR_NODE;

public class TaxiGraphStateModel extends GraphDefinedDomain.GraphStateModel {

    int visitInterval = 60;


    public TaxiGraphStateModel(Map<Integer, Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>>> transitionDynamics) {
        super(transitionDynamics);
    }


    @Override
    public List<StateTransitionProb> stateTransitions(State state, Action action) {

        int actionId = ((GraphDefinedDomain.GraphActionType.GraphAction)action).aId;
        int nodeId = (int)state.get(VAR_NODE);
        int toNodeId;

        List<StateTransitionProb> resultTransitions = new ArrayList<>();

        Action newAction;
        if (actionId == ActionTypes.TO_NEXT_LOCATION.getValue()) {
            toNodeId = ((NextLocationAction)action).getToNodeId();
            newAction = addToNextLocationTransition((NextLocationAction) action, (TaxiGraphState)state, toNodeId);
            if (newAction == null){
                return resultTransitions;
            }
        } else if (actionId == ActionTypes.GOING_TO_CHARGING_STATION.getValue()){
            toNodeId = ((GoingToChargingStationAction)action).getToNodeId();
            if(!((GoingToChargingStationAction)action).applicableInState((TaxiGraphState) state)){
                return resultTransitions;
            }
            newAction = action.copy();
        } else if (actionId == ActionTypes.CHARGING_IN_CHARGING_STATION.getValue()){
            if (((TaxiGraphState)state).getPreviousActionId() == ActionTypes.CHARGING_IN_CHARGING_STATION.getValue()){
                return resultTransitions;
            }
            toNodeId = nodeId;
            newAction = action.copy();
        } else {
            if (((TaxiGraphState)state).getPreviousActionId() == ActionTypes.CHARGING_IN_CHARGING_STATION.getValue()){
                return resultTransitions;
            }
            toNodeId = nodeId;
            newAction = action.copy();
        }

        State ns = state.copy();

        ((TaxiGraphState) ns).set(VAR_NODE, toNodeId);
        ((TaxiGraphState) ns).set(Utils.VAR_TIMESTAMP, this.getResultTimeStamp(state, newAction));
        ((TaxiGraphState) ns).set(Utils.VAR_STATE_OF_CHARGE, this.getResultStateOfCharge(state, newAction));
        ((TaxiGraphState) ns).set(Utils.VAR_PREVIOUS_ACTION, action);
        ((TaxiGraphState) ns).set(Utils.VAR_PREVIOUS_STATE, state);

        StateTransitionProb tp = new StateTransitionProb(ns, 1.);
        resultTransitions.add(tp);

        return resultTransitions;
    }


    public State sample(State state, Action action) {
        state = state.copy();
        int actionId = ((GraphDefinedDomain.GraphActionType.GraphAction)action).aId;


        switch (actionId){
            case 0:
                setStateProperties(state, action, ((NextLocationAction)action).getToNodeId(),
                        this.getResultTimeStamp(state, action), this.getResultStateOfCharge(state, action), state);
                break;
            case 2:
                setStateProperties(state, action, ((GoingToChargingStationAction)action).getToNodeId(),
                        this.getResultTimeStamp(state, action), this.getResultStateOfCharge(state, action), state);
                break;
            default:
                setStateProperties(state, action, ((TaxiGraphState)state).getNodeId(),
                        this.getResultTimeStamp(state, action), this.getResultStateOfCharge(state, action), state);
                break;
        }

        return state;
    }


    private void setStateProperties(State state, Action action, int toNodeId, double resultTime, double resultStateOfCharge ,State previousState){
        ((TaxiGraphState) state).set(VAR_NODE, toNodeId);
        ((TaxiGraphState) state).set(Utils.VAR_TIMESTAMP, resultTime);
        ((TaxiGraphState) state).set(Utils.VAR_STATE_OF_CHARGE, resultStateOfCharge);
        ((TaxiGraphState) state).set(Utils.VAR_PREVIOUS_ACTION, action);
        ((TaxiGraphState) state).set(Utils.VAR_PREVIOUS_STATE, previousState);
    }


    private NextLocationAction addToNextLocationTransition(NextLocationAction action, TaxiGraphState state, int toNodeId){
        NextLocationAction newAction = (NextLocationAction) action.copy();

        if (state.getPreviousNode() == action.getToNodeId()){
            return null;
        }

        if (state.getRecentlyVisitedNodes().containsKey(toNodeId)){

            if (state.getTimeStamp() + newAction.getActionTime(state) - state.getRecentlyVisitedNodes().get(toNodeId) < visitInterval ){
                return null;
            } else {
                state.getRecentlyVisitedNodes().replace(toNodeId, state.getRecentlyVisitedNodes().get(toNodeId), state.getTimeStamp() +
                        (newAction).getActionTime(state));
            }
        } else {
            state.getRecentlyVisitedNodes().put(toNodeId, newAction.getActionTime(state));
        }

        return newAction;
    }


    private double getResultTimeStamp(State state, Action action){
        return ((MeasurableAction)action).getActionTime((TaxiGraphState) state) + ((TaxiGraphState)state).getTimeStamp();
    }


    private double getResultStateOfCharge(State state, Action action){
        return ((MeasurableAction)action).getActionEnergyConsumption((TaxiGraphState)state) + ((TaxiGraphState)state).getStateOfCharge();
    }
}
