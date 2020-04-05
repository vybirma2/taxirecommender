package domain.states;


import domain.actions.MeasurableAction;

import java.util.*;

import static utils.Utils.NUM_OF_ACTION_TYPES;

/**
 * Class representing state for planning containing information of current node, state of charge, timestamp but also
 * previous actions and states or maximal possible reward achieved by maxReward action which is computed after generating
 * all possible states
 */
public class TaxiGraphState implements Comparable<TaxiGraphState> {

    private Integer hash;
    private int nodeId;
    private int stateOfCharge;
    private int timeStamp;


    private boolean changed = false;

    private ArrayList<ArrayList<ActionStatePair>> previousActionStatePairs = new ArrayList<>(NUM_OF_ACTION_TYPES);
    private Set<Integer> previousActionsId = new HashSet<>();

    private MeasurableAction maxRewardAction = null;
    private TaxiGraphState maxNextState = null;
    private Double maxReward = null;
    private HashMap<Integer, Double> afterTaxiTripStateRewards = new HashMap<>();


    public TaxiGraphState(int nodeId, int stateOfCharge, int timeStamp) {

        this.nodeId = nodeId;
        this.stateOfCharge = stateOfCharge;
        this.timeStamp = timeStamp;
        for (int i = 0; i < NUM_OF_ACTION_TYPES; i++){
            previousActionStatePairs.add(null);
        }
    }


    public TaxiGraphState copy() {
        return new TaxiGraphState(this.nodeId, this.stateOfCharge, this.timeStamp);
    }


    public int getStateOfCharge() {
        return stateOfCharge;
    }


    public int getTimeStamp() {
        return timeStamp;
    }


    public int getNodeId() {
        return nodeId;
    }


    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public void setStateOfCharge(int stateOfCharge) {
        this.stateOfCharge = stateOfCharge;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * @return maximal possible reward which were set during reward computation in reward function
     */
    public double getReward() {
        if (maxRewardAction == null){
            return 0;
        } else {
            return maxReward;
        }
    }


    /**
     * If better than before set value, setting new maximum
     * @param action action to do to receive given reward
     * @param reward potentially received reward
     */
    public void setActionReward(MeasurableAction action, Double reward, TaxiGraphState maxNextState) {
        if (maxRewardAction == null || this.maxReward < reward){
            this.maxRewardAction = action;
            this.maxReward = reward;
            this.maxNextState = maxNextState;
        }
    }


    /**
     * @return returning false if given action is the only one to get to this state
     */
    /*public boolean isPossibleToGoToNextLocation(){
        return !previousActionStatePairs.containsKey(ActionTypes.GOING_TO_CHARGING_STATION.getValue())
                || previousActionStatePairs.size() != 1;
    }*/


    public void addPreviousAction(MeasurableAction action, int actionId, TaxiGraphState state){
        this.setChanged(true);
        if (previousActionStatePairs.get(actionId) != null){
            previousActionStatePairs.get(actionId).add(new ActionStatePair(state, action));
        } else {
            ArrayList<ActionStatePair> actionType = new ArrayList<>();
            actionType.add(new ActionStatePair(state, action));
            previousActionStatePairs.set(actionId, actionType);
            previousActionsId.add(actionId);
        }
    }


    public Set<Integer> getPreviousActions(){
        return previousActionsId;
    }


  /*  public ArrayList<ActionStatePair> getPreviousActionsOfType (int type){
        if (previousActionStatePairs.get(type) != null){
            return previousActionStatePairs.get(type);
        } else {
            return null;
        }

    }
*/

    public ArrayList<ActionStatePair> getPreviousStatesOfAction(int actionId){
        return previousActionStatePairs.get(actionId);
    }


    public void addAfterTaxiTripStateReward(int toNodeId, double reward){
        this.afterTaxiTripStateRewards.put(toNodeId, reward);
    }


    public Double getAfterTaxiTripStateReward(int toNodeId){
        return this.afterTaxiTripStateRewards.get(toNodeId);
    }


    public MeasurableAction getMaxRewardAction() {
        return maxRewardAction;
    }


    public int getMaxRewardActionId() {
        if (maxRewardAction == null){
            return Integer.MIN_VALUE;
        } else {
            return maxRewardAction.getActionId();
        }
    }


    public boolean isStartingState(){
        return previousActionsId.isEmpty();
    }


    public boolean isChanged() {
        return changed;
    }


    public void setChanged(boolean changed) {
        this.changed = changed;
    }


    public TaxiGraphState getMaxNextState() {
        return maxNextState;
    }

    @Override
    public String toString() {
        return "TaxiGraphState{" +
                "stateOfCharge=" + stateOfCharge +
                ", timeStamp=" + timeStamp +
                ", nodeId=" + nodeId +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        TaxiGraphState that = (TaxiGraphState) o;
        return nodeId == that.nodeId &&
                stateOfCharge == that.stateOfCharge &&
                timeStamp == that.timeStamp;
    }


    @Override
    public int hashCode() {
        if (hash == null){
            hash = Objects.hash(nodeId, stateOfCharge, timeStamp);
        }
        return hash;
    }


    @Override
    public int compareTo(TaxiGraphState o) {
        return Integer.compare(o.timeStamp, this.timeStamp);
    }
}
