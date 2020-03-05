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

import java.util.*;

import static domain.Utils.VAR_NODE;

public class TaxiGraphStateModel extends GraphDefinedDomain.GraphStateModel {

    HashMap<Integer, Double> recentlyVisitedNodes = new HashMap<>();
    int visitInterval = 1;


    public TaxiGraphStateModel(Map<Integer, Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>>> transitionDynamics) {
        super(transitionDynamics);
    }


    @Override
    public List<StateTransitionProb> stateTransitions(State state, Action action) {

        int actionId = ((GraphDefinedDomain.GraphActionType.GraphAction)action).aId;
        int nodeId = (int)state.get(VAR_NODE);

        List<StateTransitionProb> resultTransitions = new ArrayList();
        Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>> actionMap = this.transitionDynamics.get(nodeId);
        Set<GraphDefinedDomain.NodeTransitionProbability> transitions = actionMap.get(actionId);


        for (GraphDefinedDomain.NodeTransitionProbability ntp : transitions) {
            Action newAction;
            if (actionId == ActionTypes.TO_NEXT_LOCATION.getValue()) {
                newAction = addToNextLocationTransition((NextLocationAction) action, (TaxiGraphState)state, ntp);
                if (newAction == null){
                    continue;
                }
            } else if (actionId == ActionTypes.GOING_TO_CHARGING_STATION.getValue()){
                if(!((GoingToChargingStationAction)action).applicableInState((TaxiGraphState) state)){
                    continue;
                }
                newAction = action.copy();
            } else {
                newAction = action.copy();
            }

            State ns = state.copy();

            ((TaxiGraphState) ns).set(VAR_NODE, ntp.transitionTo);
            ((TaxiGraphState) ns).set(Utils.VAR_TIMESTAMP, this.getResultTimeStamp(state, newAction));
            ((TaxiGraphState) ns).set(Utils.VAR_STATE_OF_CHARGE, this.getResultStateOfCharge(state, newAction));
            ((TaxiGraphState) ns).set(Utils.VAR_PREVIOUS_ACTION, actionId);
            ((TaxiGraphState) ns).set(Utils.VAR_PREVIOUS_NODE, state.get(VAR_NODE));

            StateTransitionProb tp = new StateTransitionProb(ns, ntp.probability);
            resultTransitions.add(tp);
        }

        return resultTransitions;
    }


    private NextLocationAction addToNextLocationTransition(NextLocationAction action, TaxiGraphState state, GraphDefinedDomain.NodeTransitionProbability ntp){
        NextLocationAction newAction = (NextLocationAction) action.copy();
        newAction.setToNodeId(ntp.transitionTo);
        if(!newAction.applicableInState(state)){
            return null;
        }

        if (recentlyVisitedNodes.containsKey(ntp.transitionTo)){
            if (state.getTimeStamp() + newAction.getActionTime(state) - recentlyVisitedNodes.get(ntp.transitionTo) < visitInterval ){
                return null;
            } else {
                recentlyVisitedNodes.replace(ntp.transitionTo, recentlyVisitedNodes.get(ntp.transitionTo), state.getTimeStamp() +
                        (newAction).getActionTime(state));
            }
        } else {
            recentlyVisitedNodes.put(ntp.transitionTo, newAction.getActionTime(state));
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
