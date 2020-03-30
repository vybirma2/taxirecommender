package domain.actions;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import domain.states.TaxiGraphState;
import parameterestimation.ParameterEstimator;

import java.util.*;

import static domain.actions.ActionUtils.*;
import static domain.actions.ActionUtils.notRunOutOfBattery;
import static utils.DistanceGraphUtils.getIntervalStart;

/**
 * Class with the main purpose of returning all available actions of picking up passenger in some node in the environment.
 */
public class PickUpPassengerActionType  extends GraphDefinedDomain.GraphActionType {

    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> taxiTripLengths;
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> taxiTripDistances;
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Double>>> taxiTripConsumptions;


    /**
     * @param aId
     * @param transitionDynamics
     * @param parameterEstimator used parameter estimator to get passenger trips information - trip lengths, consumptions...
     */
    public PickUpPassengerActionType(int aId, Map<Integer, Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>>> transitionDynamics,
                                     ParameterEstimator parameterEstimator) {
        super(aId, transitionDynamics);
        taxiTripLengths = parameterEstimator.getTaxiTripLengths();
        taxiTripConsumptions = parameterEstimator.getTaxiTripConsumptions();
        taxiTripDistances = parameterEstimator.getTaxiTripDistances();
    }


    @Override
    public String typeName() {
        return ActionTypes.PICK_UP_PASSENGER.getName();
    }


    /**
     * @param state
     * @return list of all possible actions of picking up passenger in current state defined by transitions set
     * in TaxiRecommenderDomainGenerator - check on applicability - not running out of time/battery...
     */
    @Override
    public List<Action> allApplicableActions(State state) {
        List<Action> actions = new ArrayList<>();

        int node = (Integer)state.get("node");
        Map<Integer, Set<GraphDefinedDomain.NodeTransitionProbability>> actionMap = this.transitionDynamics.get(node);
        Set<GraphDefinedDomain.NodeTransitionProbability> transitions = actionMap.get(this.aId);

        if (transitions != null){
            for (GraphDefinedDomain.NodeTransitionProbability neighbour : transitions){
                if (this.applicableInState((TaxiGraphState) state, neighbour.transitionTo)){

                    int startInterval = getIntervalStart(((TaxiGraphState)state).getTimeStamp());
                    actions.add(new PickUpPassengerAction(this.aId, node, neighbour.transitionTo,
                            taxiTripLengths.get(startInterval).get(((TaxiGraphState)state).getNodeId()).get(neighbour.transitionTo).longValue(),
                            taxiTripConsumptions.get(startInterval).get(((TaxiGraphState)state).getNodeId()).get(neighbour.transitionTo).intValue(),
                            ((TaxiGraphState)state).getTimeStamp()));
                }
            }
        }
        return actions;
    }


    @Override
    protected boolean applicableInState(State state) {
        return super.applicableInState(state);
    }


    private boolean applicableInState(TaxiGraphState state, int toNodeId){
        int startInterval = getIntervalStart(state.getTimeStamp());

        if (taxiTripLengths.get(startInterval).containsKey(state.getNodeId())){
            if (taxiTripLengths.get(startInterval).get(state.getNodeId()).containsKey(toNodeId)){
                return applicableInState(state) && shiftNotOver(state, taxiTripLengths.get(startInterval).get(state.getNodeId()).get(toNodeId)) &&
                        notRunOutOfBattery(state, taxiTripConsumptions.get(startInterval).get(state.getNodeId()).get(toNodeId).intValue());
            }
        }

        return false;
    }
}
