package domain.states;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
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


    private boolean changed = false;

    private ArrayList[] previousActionStatePairs = new ArrayList[NUM_OF_ACTION_TYPES];
    private Set<Integer> previousActionsId = new HashSet<>();

    private Action maxRewardAction = null;
    private TaxiGraphState maxNextState = null;
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

    public void setNodeId(int nodeId) {
        super.set(VAR_NODE, nodeId);

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
    public void setActionReward(Action action, Double reward, TaxiGraphState maxNextState) {
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


    public void addPreviousAction(Action action, int actionId, TaxiGraphState state){
        this.setChanged(true);
        if (previousActionStatePairs[actionId] != null){
            previousActionStatePairs[actionId].add(new ActionStatePair(state, action));
        } else {
            ArrayList<ActionStatePair> actionType = new ArrayList<>();
            actionType.add(new ActionStatePair(state, action));
            previousActionStatePairs[actionId] = actionType;
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
        return previousActionStatePairs[actionId];
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


    public int getMaxRewardActionId() {
        if (maxRewardAction == null){
            return Integer.MIN_VALUE;
        } else {
            return ((GraphDefinedDomain.GraphActionType.GraphAction)maxRewardAction).aId;
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
