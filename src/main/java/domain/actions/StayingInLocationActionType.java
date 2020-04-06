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
    public List<MeasurableAction> allApplicableActions(TaxiGraphState state) {
        List<MeasurableAction> actions = new ArrayList<>();

        if (this.applicableInState(state)) {
            actions.add(new StayingInLocationAction(this.actionId, state.getNodeId(), state.getNodeId(),
                    state.getTimeStamp(), STAYING_INTERVAL, 0));
        }

        return actions;
    }


    @Override
    boolean applicableInState(TaxiGraphState state) {
        return transitions.containsKey(state.getNodeId()) && shiftNotOver(state, STAYING_INTERVAL);
    }
}
