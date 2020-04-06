package domain.actions;

import domain.states.TaxiGraphState;
import parameterestimation.ParameterEstimator;

import java.util.*;

import static domain.actions.ActionUtils.*;
import static domain.actions.ActionUtils.notRunOutOfBattery;
import static utils.DistanceGraphUtils.getIntervalStart;

/**
 * Class with the main purpose of returning all available actions of picking up passenger in some node in the environment.
 */
public class PickUpPassengerActionType  extends TaxiActionType {

    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> taxiTripLengths;
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> taxiTripConsumptions;


    /**
     * @param parameterEstimator used parameter estimator to get passenger trips information - trip lengths, consumptions...
     */
    public PickUpPassengerActionType(int actionId, HashMap<Integer, ArrayList<Integer>> transitions, ParameterEstimator parameterEstimator) {
        super(actionId, transitions);
        taxiTripLengths = parameterEstimator.getTaxiTripLengths();
        taxiTripConsumptions = parameterEstimator.getTaxiTripConsumptions();
    }




    /**
     * @param state
     * @return list of all possible actions of picking up passenger in current state defined by transitions set
     * in TaxiRecommenderDomainGenerator - check on applicability - not running out of time/battery...
     */
    @Override
    public List<MeasurableAction> allApplicableActions(TaxiGraphState state) {
        List<MeasurableAction> actions = new ArrayList<>();

        ArrayList<Integer> trans = transitions.get(state.getNodeId());

        if (trans != null) {
            for (Integer neighbour : trans) {
                if (this.applicableInState(state, neighbour)) {
                    int startInterval = getIntervalStart(state.getTimeStamp());

                    actions.add(new PickUpPassengerAction(this.actionId, state.getNodeId(), neighbour, state.getTimeStamp(),
                            taxiTripLengths.get(startInterval).get(state.getNodeId()).get(neighbour).intValue(),
                            taxiTripConsumptions.get(startInterval).get(state.getNodeId()).get(neighbour).intValue()));
                }
            }
        }

        return actions;
    }

    @Override
    boolean applicableInState(TaxiGraphState state) {
        return transitions.containsKey(state.getNodeId());
    }


    private boolean applicableInState(TaxiGraphState state, int toNodeId){
        int startInterval = getIntervalStart(state.getTimeStamp());

        if (taxiTripLengths.get(startInterval).containsKey(state.getNodeId())){
            if (taxiTripLengths.get(startInterval).get(state.getNodeId()).containsKey(toNodeId)){
                return applicableInState(state) && shiftNotOver(state, taxiTripLengths.get(startInterval).get(state.getNodeId()).get(toNodeId).longValue()) &&
                        notRunOutOfBattery(state, taxiTripConsumptions.get(startInterval).get(state.getNodeId()).get(toNodeId).intValue());
            }
        }

        return false;
    }
}
