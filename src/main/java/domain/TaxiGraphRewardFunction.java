package domain;

import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.ActionType;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;
import domain.actions.ChargingAction;
import domain.states.TaxiGraphState;
import domain.states.TaxiGraphStateComparator;
import parameterestimation.ParameterEstimator;
import utils.Utils;

import java.util.*;

import static domain.actions.ActionUtils.*;
import static utils.DistanceGraphUtils.getIntervalStart;


public class TaxiGraphRewardFunction implements RewardFunction {

    private TerminalFunction terminalFunction;
    private ParameterEstimator parameterEstimator;

    public TaxiGraphRewardFunction(TerminalFunction terminalFunction, ParameterEstimator parameterEstimator) {
        this.terminalFunction = terminalFunction;
        this.parameterEstimator = parameterEstimator;
    }

    @Override
    public double reward(State state, Action action, State state1) {
        return 0;
    }

    public void computeRewardForStates(List<State> states, List<ActionType> actionTypes){

        PriorityQueue<TaxiGraphState> openedSet = getSortedTerminalStates(states);
        HashSet<TaxiGraphState> visited = new HashSet<>(openedSet);

        while (!openedSet.isEmpty()){
            TaxiGraphState state = openedSet.poll();
            Set<TaxiGraphState> previousState = setPreviousStateReward(state);

            if (previousState == null){
                continue;
            }
            for (TaxiGraphState taxiGraphState : previousState){
                if (!visited.contains(taxiGraphState)){
                    openedSet.add(taxiGraphState);
                    visited.add(taxiGraphState);
                }
            }

        }

        System.out.println("ddd");

    }


    private PriorityQueue<TaxiGraphState> getSortedTerminalStates(List<State> states){
        PriorityQueue<TaxiGraphState> terminalStates = new PriorityQueue<>(new TaxiGraphStateComparator());
        for (State state : states){
            if (terminalFunction.isTerminal(state)){
                terminalStates.add((TaxiGraphState)state);
            }

        }

        return terminalStates;
    }


    private Set<TaxiGraphState> setPreviousStateReward(TaxiGraphState state){

        if (state.isStartingState()){
            return null;
        }
        HashSet<TaxiGraphState> visitedStates = new HashSet<>();


        for (Integer actionId : state.getPreviousActions()){
            for (Map.Entry<Action, TaxiGraphState> entry : state.getPreviousStatesOfAction(actionId).entrySet()){
                switch (actionId){
                    case 0:
                    case 1:
                        entry.getValue().setActionReward(entry.getKey(), getStayingAndNextLocationReward(state));
                        visitedStates.add(entry.getValue());
                        break;
                    case 2:
                        entry.getValue().setActionReward(entry.getKey(), getGoingToChargingStationReward(state));
                        visitedStates.add(entry.getValue());
                        break;
                    case 3:
                        entry.getValue().setActionReward(entry.getKey(), getChargingReward(state, (ChargingAction)entry.getKey()));
                        visitedStates.add(entry.getValue());
                        break;
                    case 4:
                        entry.getValue().addAfterTaxiTripStateReward(state.getNodeId(), state.getReward());
                        visitedStates.add(entry.getValue());
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + actionId);
                }
            }

        }
        return visitedStates;
    }


    private double getStayingAndNextLocationReward(TaxiGraphState state){
        HashMap<Integer, Double> destinationProbabilities = parameterEstimator.getDestinationProbabilitiesInNode(state.getNodeId(), state.getTimeStamp());
        Set<Double> outOfChargeTimeDestinationProbabilities = getOutOfChargeTimeProbabilities(state, destinationProbabilities);

        double sumOutOfChargeTimeProbabilities = outOfChargeTimeDestinationProbabilities.stream().reduce(0., Double::sum);
        double pickUpProbability = parameterEstimator.getPickUpProbabilityInNode(state.getNodeId(), state.getTimeStamp());

        double notPickingPassengerReward = state.getReward() * (1 - pickUpProbability + pickUpProbability * sumOutOfChargeTimeProbabilities) ;
        double pickUpPassengerReward = getPickupPassengerReward(state, destinationProbabilities, pickUpProbability);

        return notPickingPassengerReward + pickUpPassengerReward;
    }


    private double getGoingToChargingStationReward(TaxiGraphState state) {
        return state.getReward();
    }

    private double getChargingReward(TaxiGraphState state, ChargingAction action) {
        return state.getReward() + action.getChargingCost();
    }

    private double getPickupPassengerReward(TaxiGraphState state, HashMap<Integer, Double> destinationProbabilities,
                                            double pickUpProbability){
        SuccessfulPickUpParameters successfulPickUpParameters = getSuccessfulPickUpParameters(state, destinationProbabilities);

        ArrayList<Double> probabilities = successfulPickUpParameters.getProbabilities();
        ArrayList<Double> tripReward = successfulPickUpParameters.getTripReward();
        ArrayList<Double> futureStateReward = successfulPickUpParameters.getFutureStateReward();


        double resultReward = 0;

        for (int i = 0; i < probabilities.size(); i++){
            resultReward += probabilities.get(i) * (tripReward.get(i) + futureStateReward.get(i));
        }

        return resultReward * pickUpProbability;
    }


    private Set<Double> getOutOfChargeTimeProbabilities(TaxiGraphState state , HashMap<Integer, Double> destinationProbabilities){
        HashSet<Double> result = new HashSet<>();
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Long>>> tripLengths = parameterEstimator.getTaxiTripLengths();
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> tripConsumptions = parameterEstimator.getTaxiTripConsumptions();

        int startInterval = getIntervalStart(state.getTimeStamp());

        if (destinationProbabilities != null){
            for (Map.Entry<Integer, Double> entry : destinationProbabilities.entrySet()){
                if (!shiftNotOver(state, tripLengths.get(startInterval).get(state.getNodeId()).get(entry.getKey()))
                        || !notRunOutOfBattery(state, tripConsumptions.get(startInterval).get(state.getNodeId()).get(entry.getKey()))) {
                    result.add(entry.getValue());
                }
            }
        }

        return result;
    }

    private SuccessfulPickUpParameters getSuccessfulPickUpParameters(TaxiGraphState state , HashMap<Integer, Double> destinationProbabilities){
        ArrayList<Double> probabilities = new ArrayList<>();
        ArrayList<Double> tripReward = new ArrayList<>();
        ArrayList<Double> futureStateReward = new ArrayList<>();

        HashMap<Integer, HashMap<Integer, HashMap<Integer, Long>>> tripLengths = parameterEstimator.getTaxiTripLengths();
        HashMap<Integer,  HashMap<Integer, HashMap<Integer, Integer>>> tripConsumptions = parameterEstimator.getTaxiTripConsumptions();
        HashMap<Integer,  HashMap<Integer, HashMap<Integer, Double>>> tripDistances = parameterEstimator.getTaxiTripDistances();

        int startInterval = getIntervalStart(state.getTimeStamp());

        if (destinationProbabilities != null){
            for (Map.Entry<Integer, Double> entry : destinationProbabilities.entrySet()){
                int consumption = tripConsumptions.get(startInterval).get(state.getNodeId()).get(entry.getKey());
                long tripLength = tripLengths.get(startInterval).get(state.getNodeId()).get(entry.getKey());

                if (shiftNotOver(state, tripLength) && notRunOutOfBattery(state, consumption)){
                    probabilities.add(entry.getValue());
                    tripReward.add(getTripReward(tripDistances.get(startInterval).get(state.getNodeId()).get(entry.getKey())));
                    futureStateReward.add(getAfterPickUpStateReward(state, entry.getKey()));
                }
            }
        }

        return new SuccessfulPickUpParameters(probabilities, tripReward, futureStateReward);
    }


    private double getTripReward(double distance){
        return distance * Utils.TAXI_COST_FOR_KM + Utils.TAXI_START_JOURNEY_FEE;
    }


    private double getAfterPickUpStateReward(TaxiGraphState state, Integer toNodeId){
        return state.getAfterTaxiTripStateReward(toNodeId);
    }


}
