package domain.states;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.domain.singleagent.graphdefined.GraphStateNode;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import utils.Utils;


import java.util.HashMap;
import java.util.Objects;


import static utils.Utils.*;

public class TaxiGraphState extends GraphStateNode implements Comparable {

    public static int id = 0;

    private int stateId;

    private int nodeId;
    private int stateOfCharge;
    private int timeStamp;

    private Integer previousActionId = null;
    private Integer previousNode = null;

    private TaxiGraphState previousState = null;
    private Action previousAction = null;

    private HashMap<Integer, Integer> recentlyVisitedNodes = new HashMap<>();

    private Action maxRewardAction = null;
    private Double maxReward = null;
    private HashMap<Integer, Double> afterTaxiTripStateRewards = new HashMap<>();

    public TaxiGraphState(int stateId, int nodeId, int stateOfCharge, int timeStamp) {
        super(nodeId);
        keys.add(VAR_NODE);
        keys.add(VAR_STATE_OF_CHARGE);
        keys.add(VAR_TIMESTAMP);
        keys.add(VAR_PREVIOUS_ACTION);
        keys.add(VAR_PREVIOUS_STATE);

        this.stateId = stateId;
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
                case VAR_PREVIOUS_ACTION:
                    if (value instanceof GraphDefinedDomain.GraphActionType.GraphAction){
                        this.previousActionId = ((GraphDefinedDomain.GraphActionType.GraphAction)value).aId;
                        this.previousAction = (GraphDefinedDomain.GraphActionType.GraphAction)value;
                        return this;
                    } else if (value == null){
                        this.previousActionId = Integer.MAX_VALUE;
                        this.previousAction = null;
                        return this;
                    } else {
                        throw new RuntimeException("Invalid value data type " + value.getClass());
                    }
                case VAR_PREVIOUS_STATE:
                    if (value instanceof TaxiGraphState){
                        this.previousNode = ((TaxiGraphState)value).nodeId;
                        this.previousState = (TaxiGraphState)value;
                        return this;
                    } else if (value == null){
                        this.previousNode = Integer.MAX_VALUE;
                        this.previousState = null;
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
        TaxiGraphState state = new TaxiGraphState(this.stateId, this.nodeId, this.stateOfCharge, this.timeStamp);
        state.set(Utils.VAR_PREVIOUS_ACTION, this.previousAction);
        state.set(Utils.VAR_PREVIOUS_STATE, this.previousState);
        state.setRecentlyVisitedNodes(new HashMap<>(recentlyVisitedNodes));
        return state;
    }

    public HashMap<Integer, Integer> getRecentlyVisitedNodes() {
        return recentlyVisitedNodes;
    }

    public void setRecentlyVisitedNodes(HashMap<Integer, Integer> recentlyVisitedNodes) {
        this.recentlyVisitedNodes = recentlyVisitedNodes;
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


    public Integer getPreviousActionId() {
        return previousActionId;
    }


    public Integer getPreviousNode() {
        return previousNode;
    }

    public TaxiGraphState getPreviousState() {
        return previousState;
    }

    public Action getPreviousAction() {
        return previousAction;
    }


    public double getReward() {
        if (maxRewardAction == null){
            return 0;
        } else {
            return maxReward;
        }
    }

    public void setActionReward(Action action, Double reward) {
        if (maxRewardAction == null || this.maxReward < reward){
            this.maxRewardAction = action;
            this.maxReward = reward;
        }

    }


    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
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
    public int compareTo(Object o) {
        TaxiGraphState state = (TaxiGraphState)o;
        return Integer.compare(state.timeStamp, this.timeStamp);
    }
}
