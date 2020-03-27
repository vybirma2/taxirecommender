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
            TaxiGraphState previousState = setPreviousStateReward(state);
            if (previousState != null && !visited.contains(previousState)){
                openedSet.add(previousState);
                visited.add(previousState);
            }
        }

        System.out.println("ddd");

    }


    private PriorityQueue<TaxiGraphState> getSortedTerminalStates(List<State> states){
        PriorityQueue<TaxiGraphState> terminalStates = new PriorityQueue<>(new TaxiGraphStateComparator());
        for (State state : states){
            if (((TaxiGraphState)state).getStateOfCharge() == 78 && ((TaxiGraphState)state).getTimeStamp() == 649 && ((TaxiGraphState)state).getNodeId() == 69739 && ((TaxiGraphState)state).getPreviousNode() == 92162){
                System.out.println("shgvd");
            }
            if (terminalFunction.isTerminal(state)){
                terminalStates.add((TaxiGraphState)state);
            }

        }

        return terminalStates;
    }


    private TaxiGraphState setPreviousStateReward(TaxiGraphState state){
        if (state.getStateOfCharge() == 79 && state.getTimeStamp() == 658 && state.getNodeId() == 92162){
            System.out.println("shgvd");
        }
        if (state.getPreviousActionId() == null){
            return null;
        }

        switch (state.getPreviousActionId()){
            case 0:
            case 1:
                state.getPreviousState().setActionReward(state.getPreviousAction(), getStayingAndNextLocationReward(state));
                return state.getPreviousState();
            case 2:
                state.getPreviousState().setActionReward(state.getPreviousAction(), getGoingToChargingStationReward(state));
                return state.getPreviousState();
            case 3:
                state.getPreviousState().setActionReward(state.getPreviousAction(), getChargingReward(state));
                return state.getPreviousState();
            case 4:
                state.getPreviousState().addAfterTaxiTripStateReward(state.getNodeId(), state.getReward());
                return state.getPreviousState();
            default:
                throw new IllegalStateException("Unexpected value: " + state.getPreviousActionId());
        }
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

    private double getChargingReward(TaxiGraphState state) {
        return state.getReward() + ((ChargingAction)state.getPreviousAction()).getChargingCost();
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
        if (state.getAfterTaxiTripStateReward(toNodeId) == null){
            System.out.println("sjfjes");

        }
        return state.getAfterTaxiTripStateReward(toNodeId);
    }


}
