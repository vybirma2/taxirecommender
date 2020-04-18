package domain.actions;

import domain.states.TaxiState;
import parameterestimation.EnergyConsumptionEstimator;

import java.util.*;

import static domain.actions.ActionUtils.*;
import static utils.DistanceGraphUtils.getTripTime;

/**
 * Class with the main purpose of returning all available actions of moving to the neighbouring node in the environment.
 */
public class NextLocationActionType extends TaxiActionType {


    public NextLocationActionType(int actionId, HashMap<Integer, ArrayList<Integer>> transitions) {
        super(actionId, transitions);
    }


    @Override
    void addPreviousState(TaxiState previousState, int stateId) {
        previousState.addNextLocationPreviousState(stateId);
    }

    /**
     * @param state
     * @return list of all possible actions of moving to the neighbouring node defined by transitions set
     * in TaxiRecommenderDomainGenerator - check on applicability - not running out of time/battery...
     */
    @Override
    public List<TaxiState> allReachableStates(TaxiState state) {
        List<TaxiState> states = new ArrayList<>();

        ArrayList<Integer> trans = transitions.get(state.getNodeId());

        if (trans != null){
            for (int neighbour : trans){
                int time = getTripTime(state.getNodeId(), neighbour);
                if (this.applicableInState(state, neighbour, time)){
                    addNewState(states, state,neighbour, time,
                            getConsumption(state.getNodeId(), neighbour));
                }
            }
        }


        return states;
    }


    @Override
    public List<MeasurableAction> allApplicableActions(TaxiState state) {
        List<MeasurableAction> actions = new ArrayList<>();
        ArrayList<Integer> trans = transitions.get(state.getNodeId());

        if (trans != null){
            for (int neighbour : trans){
                int time = getTripTime(state.getNodeId(), neighbour);
                if (this.applicableInState(state, neighbour, time)){
                    actions.add(new NextLocationAction(actionId, state.getNodeId(), neighbour));
                }
            }
        }

        return actions;
    }


    private int getConsumption(int fromNodeId, int toNodeId) {
        return EnergyConsumptionEstimator.getActionEnergyConsumption(fromNodeId, toNodeId);
    }

    @Override
    protected boolean applicableInState(TaxiState state) {
        return transitions.containsKey(state.getNodeId());
    }


    public int getActionTime(TaxiState state, int toNodeId) {
        return getTripTime(state.getNodeId(), toNodeId);
    }


    private boolean applicableInState(TaxiState state, int toNodeId, int time){
        return shiftNotOver(state, time) &&
                notRunOutOfBattery(state, toNodeId);
    }
}
