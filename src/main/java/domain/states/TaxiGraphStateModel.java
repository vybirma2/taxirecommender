package domain.states;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import domain.Utils;

import java.util.*;

public class TaxiGraphStateModel extends GraphDefinedDomain.GraphStateModel {

    public TaxiGraphStateModel(Map<Integer, Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>>> transitionDynamics) {
        super(transitionDynamics);
    }


    @Override
    public List<StateTransitionProb> stateTransitions(State s, Action a) {
        int actionId = ((GraphDefinedDomain.GraphActionType.GraphAction)a).aId;
        List<StateTransitionProb> result = new ArrayList();
        int nodeId = (int)s.get(Utils.VAR_NODE);
        Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>> actionMap = this.transitionDynamics.get(nodeId);
        Set<GraphDefinedDomain.NodeTransitionProbability> transitions = actionMap.get(actionId);

        Iterator var8 = transitions.iterator();

        while(var8.hasNext()) {
            GraphDefinedDomain.NodeTransitionProbability ntp = (GraphDefinedDomain.NodeTransitionProbability)var8.next();
            State ns = s.copy();
            ((TaxiGraphState)ns).set(Utils.VAR_NODE, ntp.transitionTo);
            ((TaxiGraphState)ns).set(Utils.VAR_TIMESTAMP, ntp.transitionTo);
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
}
