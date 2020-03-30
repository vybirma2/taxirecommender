package domain.states;

import burlap.domain.singleagent.graphdefined.GraphStateNode;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import java.util.*;
import static utils.Utils.*;

/**
 * Class representing state for planning containing information of current node, state of charge, timestamp but also
 * previous actions and states or maximal possible reward achieved by maxReward action which is computed after generating
 * all possible states
 */
public class TaxiGraphState extends GraphStateNode implements Comparable<TaxiGraphState> {

    private int nodeId;
    private int stateOfCharge;
    private int timeStamp;

    private HashMap<Integer, HashMap<Action, TaxiGraphState>> previousActionStatePairs = new HashMap<>();

    private Action maxRewardAction = null;
    private Double maxReward = null;
    private HashMap<Integer, Double> afterTaxiTripStateRewards = new HashMap<>();


    public TaxiGraphState(int nodeId, int stateOfCharge, int timeStamp) {
        super(nodeId);
        keys.add(VAR_NODE);
        keys.add(VAR_STATE_OF_CHARGE);
        keys.add(VAR_TIMESTAMP);

        this.nodeId = nodeId;
        this.stateOfCharge = stateOfCharge;
        this.timeStamp = timeStamp;
    }


    @Override
    public MutableState set(Object variableKey, Object value) {
        if (variableKey instanceof String){
            String key = (String) variableKey;
            switch (key){
                case VAR_NODE:
                    super.set(variableKey, value);
                    this.nodeId = (int)value;
                    return this;
                case VAR_STATE_OF_CHARGE:
                    if (value instanceof Integer){
                        this.stateOfCharge = (int)value;
                        return this;
                    } else {
                        throw new RuntimeException("Invalid value data type " + value.getClass());
                    }
                case VAR_TIMESTAMP:
                    if (value instanceof Integer){
                        this.timeStamp = (int)value;
                        return this;
                    } else {
                        throw new RuntimeException("Invalid value data type " + value.getClass());
                    }
                default:
                    throw new RuntimeException("Invalid key value type " + key);
            }
        } else {
            throw new RuntimeException("Invalid key data type " + variableKey.getClass());
        }
    }


    @Override
    public State copy() {
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
    public void setActionReward(Action action, Double reward) {
        if (maxRewardAction == null || this.maxReward < reward){
            this.maxRewardAction = action;
            this.maxReward = reward;
        }

    }


    /**
     * @param actionId
     * @return returning false if given action is the only one to get to this state
     */
    public boolean isPossibleToDoAction(int actionId){
        return !previousActionStatePairs.containsKey(actionId) || previousActionStatePairs.size() != 1;
    }


    public void addPreviousAction(Action action, int actionId, TaxiGraphState state){
        if (previousActionStatePairs.containsKey(actionId)){
            previousActionStatePairs.get(actionId).put(action, state);
        } else {
            HashMap<Action, TaxiGraphState> map = new HashMap<>();
            map.put(action, state);
            previousActionStatePairs.put(actionId, map);
        }
    }


    public Set<Integer> getPreviousActions(){
        return previousActionStatePairs.keySet();
    }


    public HashMap<Action, TaxiGraphState> getPreviousStatesOfAction(int actionId){
        return previousActionStatePairs.get(actionId);
    }


    public void addAfterTaxiTripStateReward(int toNodeId, double reward){
        this.afterTaxiTripStateRewards.put(toNodeId, reward);
    }


    public Double getAfterTaxiTripStateReward(int toNodeId){
        return this.afterTaxiTripStateRewards.get(toNodeId);
    }


    public Action getMaxRewardAction() {
        return maxRewardAction;
    }


    public boolean isStartingState(){
        return previousActionStatePairs.isEmpty();
    }


    @Override
    public String toString() {
        return "TaxiGraphState{" +
                "stateOfCharge=" + stateOfCharge +
                ", timeStamp=" + timeStamp +
                ", id=" + id +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaxiGraphState that = (TaxiGraphState) o;
        return nodeId == that.nodeId &&
                stateOfCharge == that.stateOfCharge &&
                timeStamp == that.timeStamp;
    }


    @Override
    public int hashCode() {
        return Objects.hash(nodeId, stateOfCharge, timeStamp);
    }


    @Override
    public int compareTo(TaxiGraphState o) {
        return Integer.compare(o.timeStamp, this.timeStamp);
    }
}
