package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import domain.TaxiRecommenderDomainGenerator;
import domain.Utils;
import domain.states.TaxiGraphState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static domain.Utils.VAR_NODE;

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
    public List<burlap.mdp.core.action.Action> allApplicableActions(State state) {
        List<Action> actions = new ArrayList<>();
        if (this.applicableInState(state)){
            for (Integer neighbour : TaxiRecommenderDomainGenerator.getNeighbours(((TaxiGraphState)state).getNodeId())){
                if (Utils.SHIFT_LENGTH >=  ((TaxiGraphState)state).getTimeStamp() + TaxiRecommenderDomainGenerator.getTripTime(((TaxiGraphState)state).getNodeId(), neighbour)){
                    actions.add(new NextLocationAction(this.aId, neighbour));
                }
            }
        }

        return actions;
    }

    @Override
    protected boolean applicableInState(State s) {
        if (((TaxiGraphState)s).getPreviousNode() == (int)s.get(VAR_NODE)){
            return false;
        }
        return super.applicableInState(s);
    }
}
