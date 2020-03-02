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
    int visitInterval = 60;


    public TaxiGraphStateModel(Map<Integer, Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>>> transitionDynamics) {
        super(transitionDynamics);
    }


    @Override
    public List<StateTransitionProb> stateTransitions(State state, Action action) {
        int actionId = ((GraphDefinedDomain.GraphActionType.GraphAction)action).aId;
        List<StateTransitionProb> result = new ArrayList();
        int nodeId = (int)state.get(VAR_NODE);
        Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>> actionMap = this.transitionDynamics.get(nodeId);
        Set<GraphDefinedDomain.NodeTransitionProbability> transitions = actionMap.get(actionId);

        for (GraphDefinedDomain.NodeTransitionProbability ntp : transitions) {

            if (actionId == ActionTypes.TO_NEXT_LOCATION.getValue()) {
                ((NextLocationAction)action).setToNodeId(ntp.transitionTo);
                if(!((NextLocationAction)action).applicableInState((TaxiGraphState) state)){
                    continue;
                }

                if (recentlyVisitedNodes.containsKey(ntp.transitionTo)){
                    if (((TaxiGraphState)state).getTimeStamp() +
                            ((NextLocationAction)action).getActionTime((TaxiGraphState)state) -
                            recentlyVisitedNodes.get(ntp.transitionTo) < visitInterval ){
                        continue;
                    } else {
                        recentlyVisitedNodes.replace(ntp.transitionTo, recentlyVisitedNodes.get(ntp.transitionTo), ((TaxiGraphState)state).getTimeStamp() +
                                ((NextLocationAction)action).getActionTime((TaxiGraphState)state));
                    }
                } else {
                    recentlyVisitedNodes.put(ntp.transitionTo, ((NextLocationAction)action).getActionTime((TaxiGraphState)state));
                }
            } else if (actionId == ActionTypes.GOING_TO_CHARGING_STATION.getValue()){
                if(!((GoingToChargingStationAction)action).applicableInState((TaxiGraphState) state)){
                    continue;
                }
            }



            State ns = state.copy();

            ((TaxiGraphState) ns).set(VAR_NODE, ntp.transitionTo);
            ((TaxiGraphState) ns).set(Utils.VAR_TIMESTAMP, this.getResultTimeStamp(state, action));
            ((TaxiGraphState) ns).set(Utils.VAR_STATE_OF_CHARGE, this.getResultStateOfCharge(state, action));
            ((TaxiGraphState) ns).set(Utils.VAR_PREVIOUS_ACTION, actionId);
            ((TaxiGraphState) ns).set(Utils.VAR_PREVIOUS_NODE, state.get(VAR_NODE));

            StateTransitionProb tp = new StateTransitionProb(ns, ntp.probability);
            result.add(tp);
        }

        return result;
    }

    @Override
    public State sample(State fromState, Action a) {
        TaxiGraphState toState = (TaxiGraphState) fromState.copy();
        int actionId = ((GraphDefinedDomain.GraphActionType.GraphAction)a).aId;
        int fromNodeId = (Integer)toState.get("node");
        Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>> actionMap = this.transitionDynamics.get(fromNodeId);
        Set<GraphDefinedDomain.NodeTransitionProbability> transitions = actionMap.get(actionId);

        int toNodeId;
        Iterator var12 = transitions.iterator();

        GraphDefinedDomain.NodeTransitionProbability ntp = (GraphDefinedDomain.NodeTransitionProbability)var12.next();
        toNodeId = ntp.transitionTo;
        toState.set("node", toNodeId);

        return toState;
    }


    private double getResultTimeStamp(State state, Action action){
        return ((MeasurableAction)action).getActionTime((TaxiGraphState) state) + ((TaxiGraphState)state).getTimeStamp();
    }

    private double getResultStateOfCharge(State state, Action action){
        return ((MeasurableAction)action).getActionEnergyConsumption((TaxiGraphState)state) + ((TaxiGraphState)state).getStateOfCharge();
    }
}
