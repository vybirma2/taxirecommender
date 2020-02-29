package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import domain.TaxiRecommenderDomainGenerator;
import domain.states.TaxiGraphState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NextLocationActionType extends GraphDefinedDomain.GraphActionType {

    public NextLocationActionType(int aId, Map<Integer, Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>>> transitionDynamics) {
        super(aId, transitionDynamics);
    }


    @Override
    public String typeName() {
        return ActionTypes.TO_NEXT_LOCATION.getName();
    }


    @Override
    public burlap.mdp.core.action.Action associatedAction(String strRep) {
        return new NextLocationAction(this.aId, Integer.parseInt(strRep));
    }


    @Override
    public List<burlap.mdp.core.action.Action> allApplicableActions(State s) {
        List<Action> actions = new ArrayList<>();
        if (this.applicableInState(s)){
            for (Integer neighbour : TaxiRecommenderDomainGenerator.getNeighbours(((TaxiGraphState)s).getNodeId())){
                actions.add(new NextLocationAction(this.aId, neighbour));
            }
        }

        return actions;
    }
}
