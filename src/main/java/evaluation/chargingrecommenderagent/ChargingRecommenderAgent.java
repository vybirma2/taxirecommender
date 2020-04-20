package evaluation.chargingrecommenderagent;

import domain.TaxiModel;
import domain.TaxiRewardFunction;
import domain.actions.ActionTypes;
import domain.actions.MeasurableAction;
import domain.actions.TaxiActionType;
import domain.states.TaxiState;
import evaluation.Agent;
import jdk.jshell.execution.Util;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import parameterestimation.ParameterEstimator;
import parameterestimation.TaxiTrip;
import utils.DistanceGraphUtils;
import utils.Utils;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ChargingRecommenderAgent extends Agent {

    private ReachableStatesGenerator reachableStatesGenerator;
    private TaxiRewardFunction rewardFunction;
    private ParameterEstimator parameterEstimator;
    private final TaxiState startingState;
    private TaxiState currentState;


    public ChargingRecommenderAgent(TaxiModel taxiModel, ParameterEstimator parameterEstimator, TaxiState startingState) throws IOException, ClassNotFoundException {
        super(taxiModel);
        this.parameterEstimator = parameterEstimator;
        this.startingState = startingState;
        init();
    }

    private void init() throws IOException, ClassNotFoundException {

        File file = new File("data/generatedstates/fN"+ startingState.getNodeId() + "tS" +
                startingState.getTimeStamp() + "sC" + startingState.getStateOfCharge() +"sL" + Utils.SHIFT_LENGTH  + ".fst");


        if(!file.exists()) {
            computeAndSerializeReachableStates(file);
        } else {
            readSerializedFile(file);
        }

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


    private void computeAndSerializeReachableStates(File file) throws IOException {
        reachableStatesGenerator = new ReachableStatesGenerator(this.taxiModel);
        TaxiActionType.setReachableStatesGenerator(reachableStatesGenerator);
        reachableStatesGenerator.performReachabilityFrom(startingState);
        rewardFunction = new TaxiRewardFunction(reachableStatesGenerator.getReachableStates(), parameterEstimator);
        rewardFunction.computeReward();

        FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream(file));
        out.writeObject(reachableStatesGenerator);
        out.close();

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

        int distance = parameterEstimator.getTaxiTripDistances()
                .get(DistanceGraphUtils.getIntervalStart(currentState.getTimeStamp()))
                .get(currentState.getNodeId()).get(resultState.getNodeId()).intValue();

        double resultStateReward = resultState.getReward();
        double tripReward = TaxiRewardFunction.getTripReward(distance);

        return resultStateReward + tripReward > this.currentState.getReward();
    }


    private TaxiState getResultTripState(Integer trip) {
        int consumption = parameterEstimator.getTaxiTripConsumptions()
                .get(DistanceGraphUtils.getIntervalStart(currentState.getTimeStamp()))
                .get(currentState.getNodeId()).get(trip).intValue();
        int time = parameterEstimator.getTaxiTripLengths()
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
