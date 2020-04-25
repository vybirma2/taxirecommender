package evaluation.chargingrecommenderagent;

import charging.ChargingStation;
import charging.ChargingStationReader;
import domain.TaxiRewardFunction;
import domain.actions.TaxiActionType;
import domain.states.StatePredecessors;
import domain.states.TaxiState;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import parameterestimation.ParameterEstimator;
import utils.Utils;

import java.io.*;
import java.util.*;


public class ReachableStatesGenerator implements Serializable {

    private final List<TaxiActionType> actionTypes;
    private final List<Integer> nodes;

    private HashMap<TaxiState, Integer> visitedStates = new HashMap<>();
    public List<TaxiState> reachableStates = new ArrayList<>();
    private StatePredecessors statePredecessors;
    private ParameterEstimator parameterEstimator;
    private TaxiState startingState ;


    public ReachableStatesGenerator(List<TaxiActionType> actionTypes, List<Integer> nodes, TaxiState startingState, ParameterEstimator parameterEstimator) {
        this.actionTypes = actionTypes;
        this.nodes = nodes;
        this.startingState = startingState;
        this.parameterEstimator = parameterEstimator;
    }

    public void collectReachableStates() throws IOException, ClassNotFoundException {

        File generatedStatesFile = new File("data/generatedstates/" +"visited"+ Utils.DATA_SET_NAME+"fN"+ startingState.getNodeId() + "tS" +
                startingState.getTimeStamp() + "sC" + startingState.getStateOfCharge() +"sL" + Utils.SHIFT_LENGTH  + ".fst");
        File predecessorsFile = new File("data/generatedstates/" +"predecessors"+ Utils.DATA_SET_NAME+"fN"+ startingState.getNodeId() + "tS" +
                startingState.getTimeStamp() + "sC" + startingState.getStateOfCharge() +"sL" + Utils.SHIFT_LENGTH  + ".fst");

        if (!generatedStatesFile.exists()){
            performReachabilitySearch(generatedStatesFile, predecessorsFile);
        } else {
            loadData(generatedStatesFile, predecessorsFile);
        }

    }

    private void performReachabilitySearch(File generatedStatesFile, File predecessorsFile) throws IOException {
        System.out.println("Starting reachability analysis");
        reachableStates.add(startingState);
        visitedStates.put(startingState, startingState.getId());

        for (Integer node :  nodes){
            for (int time = Utils.SHIFT_START_TIME; time <= Utils.SHIFT_START_TIME + Utils.SHIFT_LENGTH; time++){
                for (int charge = 0; charge <= 100; charge++){
                    if (startingState.getNodeId() != node
                            || startingState.getStateOfCharge() != charge
                            || startingState.getTimeStamp() != time){

                        TaxiState state = new TaxiState(node, charge, time);
                        reachableStates.add(state);
                        visitedStates.put(state, state.getId());
                    }
                }
            }
        }

        for (ChargingStation station:  ChargingStationReader.getChargingStations()){
            for (int time = Utils.SHIFT_START_TIME; time <= Utils.SHIFT_START_TIME + Utils.SHIFT_LENGTH; time++){
                for (int charge = 0; charge <= 100; charge++){
                    if (startingState.getNodeId() != station.getRoadNode().getId()
                            || startingState.getStateOfCharge() != charge
                            || startingState.getTimeStamp() != time){

                        TaxiState state = new TaxiState(station.getRoadNode().getId(), charge, time);
                        reachableStates.add(state);
                        visitedStates.put(state, state.getId());
                    }
                }
            }
        }

        System.out.println("Connection creation..." + reachableStates.size());
        statePredecessors = new StatePredecessors(reachableStates.size());
        createStateConnections();
        TaxiRewardFunction rewardFunction = new TaxiRewardFunction(reachableStates, statePredecessors, parameterEstimator);
        System.out.println("Computing reward...");
        rewardFunction.computeReward();
        System.out.println("Reward computed.");

        /*serializeGeneratedStates(generatedStatesFile);

        serializePredecessors(predecessorsFile);*/

        System.out.println("Finished reachability analysis; # states: " + reachableStates.size());
    }

    private void loadData(File generatedFile, File predecessorsFile) throws IOException, ClassNotFoundException {
        loadGenerated(generatedFile);
        loadPredecessors(predecessorsFile);
        reachableStates.forEach(s -> visitedStates.put(s, s.getId()));
    }

    private void loadGenerated(File generatedFile) throws IOException, ClassNotFoundException {
        FSTObjectInput in = new FSTObjectInput(new FileInputStream(generatedFile));
        reachableStates = (ArrayList<TaxiState>) in.readObject();
        in.close();
    }


    private void loadPredecessors(File predecessorFile) throws IOException, ClassNotFoundException {
        FSTObjectInput in = new FSTObjectInput(new FileInputStream(predecessorFile));
        statePredecessors = (StatePredecessors) in.readObject();
        in.close();
    }


    private void serializeGeneratedStates(File file) throws IOException {
        FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream(file));
        out.writeObject(reachableStates);
        out.close();
    }

    private void serializePredecessors(File file) throws IOException {
        FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream(file));
        out.writeObject(statePredecessors);
        out.close();
    }

    private void createStateConnections(){
        for (TaxiState state : reachableStates) {
            addAsPredecessorToSuccessors(state);
        }
    }


    /**
     * @param state current state
     * @return all possible transitions - future states - i.e. generator of new states, if generated state already
     * exist only updates its previous actions and previous states
     */
    public void addAsPredecessorToSuccessors(TaxiState state) {
        for (TaxiActionType a : actionTypes) {
            a.addAsPredecessorToAllReachableStates(state);
        }
    }


    public void addPreviousStateConnection(int successorStateId, int predecessorStateId, int actionId) {
        statePredecessors.addPredecessor(successorStateId, predecessorStateId, actionId);
    }


    public List<TaxiState> getReachableStates() {
        return reachableStates;
    }

    public StatePredecessors getStatePredecessors() {
        return statePredecessors;
    }

    public TaxiState getState(TaxiState state) {
        Integer taxiState = visitedStates.get(state);
        if (taxiState == null){
            return null;
        }
        return reachableStates.get(taxiState);
    }
}
