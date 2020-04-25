package evaluation.chargingrecommenderagent;

import domain.TaxiRecommenderDomain;
import domain.TaxiRewardFunction;
import domain.actions.ActionTypes;
import domain.actions.MeasurableAction;
import domain.actions.TaxiActionType;
import domain.states.TaxiState;
import evaluation.Agent;
import org.nustaq.serialization.FSTObjectInput;
import utils.DistanceGraphUtils;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ChargingRecommenderAgent extends Agent {

    private ReachableStatesGenerator reachableStatesGenerator;
    private TaxiRewardFunction rewardFunction;
    private final TaxiState startingState;
    private TaxiState currentState;



    public ChargingRecommenderAgent(TaxiRecommenderDomain domain, TaxiState startingState) throws IOException, ClassNotFoundException {
        super(domain);
        this.startingState = startingState;
        init();
    }

    private void init() throws IOException, ClassNotFoundException {

        generateReachableStates();

        currentState = startingState;
    }


    private void printCurrentPolicy(){
        TaxiState state = currentState;
        while (state.getMaxRewardStateId() != -1){
            System.out.println("FROM: " + state);
            System.out.println("TO: " + reachableStatesGenerator.getReachableStates().get(state.getMaxRewardStateId()));
            System.out.println("ACTION: " + ActionTypes.getNameOfAction(state.getMaxRewardActionId()));
            System.out.println();
            state = reachableStatesGenerator.getReachableStates().get(state.getMaxRewardStateId());
        }
    }


    private void generateReachableStates() throws IOException, ClassNotFoundException {
        reachableStatesGenerator = new ReachableStatesGenerator(domain.getActionTypes(),
                new ArrayList<>(domain.getEnvironment().getNodes()), startingState, domain.getParameterEstimator());
        TaxiActionType.setReachableStatesGenerator(reachableStatesGenerator);
        reachableStatesGenerator.collectReachableStates();
    }

    private void readSerializedFile(File file) throws IOException, ClassNotFoundException {
        FSTObjectInput in = new FSTObjectInput(new FileInputStream(file));
        reachableStatesGenerator = (ReachableStatesGenerator) in.readObject();
        in.close();
    }



    @Override
    public MeasurableAction chooseAction(TaxiState currSt, List<MeasurableAction> actions) {
        assert currentState.equals(currSt);

        for (MeasurableAction action : actions){
            if (action.getActionId() == this.currentState.getMaxRewardActionId()){
                if (getResultState(action).equals(reachableStatesGenerator.getReachableStates().get(this.currentState.getMaxRewardStateId()))){
                    this.currentState = reachableStatesGenerator.getReachableStates().get(this.currentState.getMaxRewardStateId());
                    return action;
                }
            }
        }

        return actions.get(0);
    }


    @Override
    public boolean tripOffer(TaxiState currSt, Integer trip) {
        assert currentState.equals(currSt);

        TaxiState resultState = getResultTripState(trip);

        TaxiState existingState = reachableStatesGenerator.getState(resultState);
        if (existingState != null){
            if (beneficialTrip(existingState)) {
                currentState = existingState;
                return true;
            }
        } else {
            System.out.println("Trip destination state not found!");
        }

        return false;
    }


    private boolean beneficialTrip(TaxiState resultState){

        int distance = domain.getParameterEstimator().getTaxiTripDistances()
                .get(DistanceGraphUtils.getIntervalStart(currentState.getTimeStamp()))
                .get(currentState.getNodeId()).get(resultState.getNodeId()).intValue();

        double resultStateReward = resultState.getReward();
        double tripReward = TaxiRewardFunction.getTripReward(distance);

        return resultStateReward + tripReward > this.currentState.getReward();
    }


    private TaxiState getResultTripState(Integer trip) {
        int consumption = domain.getParameterEstimator().getTaxiTripConsumptions()
                .get(DistanceGraphUtils.getIntervalStart(currentState.getTimeStamp()))
                .get(currentState.getNodeId()).get(trip).intValue();
        int time = domain.getParameterEstimator().getTaxiTripLengths()
                .get(DistanceGraphUtils.getIntervalStart(currentState.getTimeStamp()))
                .get(currentState.getNodeId()).get(trip).intValue();


        return new TaxiState(trip, currentState.getStateOfCharge() + consumption,
                currentState.getTimeStamp() + time);
    }


    private TaxiState getResultState(MeasurableAction action){
        return new TaxiState(action.getToNodeId(),
                currentState.getStateOfCharge() + action.getConsumption(),
                currentState.getTimeStamp() + action.getLength());
    }
}
