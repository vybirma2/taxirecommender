package domain.actions;

import domain.states.TaxiGraphState;

import java.util.*;

import static utils.Utils.STAYING_INTERVAL;
import static domain.actions.ActionUtils.shiftNotOver;

/**
 * Class with the main purpose of returning all available actions of staying in some node in the environment.
 */
public class StayingInLocationActionType extends TaxiActionType {


    public StayingInLocationActionType(int aId, HashMap<Integer, ArrayList<Integer>> transitions) {
        super(aId, transitions);
    }


    @Override
    void addPreviousState(TaxiGraphState previousState, int stateId) {
        previousState.addStayingPreviousState(stateId);
    }

    @Override
    public List<TaxiGraphState> allReachableStates(TaxiGraphState state) {
        List<TaxiGraphState> states = new ArrayList<>();

        if (this.applicableInState(state)) {
            addNewState(states, state, STAYING_INTERVAL, 0);
        }

        return states;
    }


    @Override
    boolean applicableInState(TaxiGraphState state) {
        return transitions.containsKey(state.getNodeId()) && shiftNotOver(state, STAYING_INTERVAL);
    }
}
