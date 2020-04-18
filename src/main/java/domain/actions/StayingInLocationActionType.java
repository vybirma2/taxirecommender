package domain.actions;

import domain.states.TaxiState;

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
    void addPreviousState(TaxiState previousState, int stateId) {
        previousState.addStayingPreviousState(stateId);
    }

    @Override
    public List<TaxiState> allReachableStates(TaxiState state) {
        List<TaxiState> states = new ArrayList<>();

        if (this.applicableInState(state)) {
            addNewState(states, state, state.getNodeId(), STAYING_INTERVAL, 0);
        }

        return states;
    }

    @Override
    public List<MeasurableAction> allApplicableActions(TaxiState state) {
        List<MeasurableAction> actions = new ArrayList<>();

        if (this.applicableInState(state)) {
            actions.add(new StayingInLocationAction(actionId, state.getNodeId(), state.getNodeId(), STAYING_INTERVAL));
        }

        return actions;
    }


    @Override
    boolean applicableInState(TaxiState state) {
        return shiftNotOver(state, STAYING_INTERVAL);
    }
}
