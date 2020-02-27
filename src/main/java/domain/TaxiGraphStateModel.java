package domain;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TaxiGraphStateModel extends GraphDefinedDomain.GraphStateModel {
    public TaxiGraphStateModel(Map<Integer, Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>>> transitionDynamics) {
        super(transitionDynamics);
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
