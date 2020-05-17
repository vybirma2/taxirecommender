package domain.actions;

import domain.states.TaxiState;

import java.util.*;

import static domain.utils.Utils.STAYING_INTERVAL;
import static domain.actions.ActionUtils.shiftNotOver;

/**
 * Class with the main purpose of returning all available actions of staying in some node in the environment.
 */
public class StayingInLocationActionType extends TaxiActionType {


    public StayingInLocationActionType(int aId, HashMap<Integer, ArrayList<Integer>> transitions) {
        super(aId, transitions);
    }


    @Override
    public void createConnections(TaxiState state) {
        if (this.applicableInState(state)) {
            createConnectionBetweenStates(state, state.getNodeId(), STAYING_INTERVAL, 0, actionId);
        }
    }

    @Override
    boolean applicableInState(TaxiState state) {
        return shiftNotOver(state, STAYING_INTERVAL);
    }
}
