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

import static domain.actions.ActionUtils.*;

public class NextLocationActionType extends GraphDefinedDomain.GraphActionType {


    public NextLocationActionType(int aId, Map<Integer, Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>>> transitionDynamics) {
        super(aId, transitionDynamics);
    }


    @Override
    public String typeName() {
        return ActionTypes.TO_NEXT_LOCATION.getName();
    }


    @Override
    public Action associatedAction(String strRep) {
        return new NextLocationAction(this.aId, Integer.parseInt(strRep));
    }


    @Override
    public List<Action> allApplicableActions(State state) {
        List<Action> actions = new ArrayList<>();
        Set<Integer> neighbours = TaxiRecommenderDomainGenerator.getNeighbours(((TaxiGraphState)state).getNodeId());

        for (Integer neighbour : neighbours){
            if (this.applicableInState((TaxiGraphState) state, neighbour)){
                actions.add(new NextLocationAction(this.aId, neighbour));
            }
        }

        return actions;
    }


    @Override
    protected boolean applicableInState(State state) {
        return notGoingToChargingPreviously(state) && super.applicableInState(state);
    }


    public double getActionTime(TaxiGraphState state, int toNodeId) {
        return TaxiRecommenderDomainGenerator.getTripTime(state.getNodeId(), toNodeId);
    }


    private boolean applicableInState(TaxiGraphState state, int toNodeId){
        return applicableInState(state) && notReturningBack(state, toNodeId) && shiftNotOver(state, this.getActionTime(state, toNodeId));
    }
}
