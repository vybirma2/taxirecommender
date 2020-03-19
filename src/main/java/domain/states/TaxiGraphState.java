package domain.states;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.domain.singleagent.graphdefined.GraphStateNode;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import utils.Utils;


import java.util.HashMap;


import static utils.Utils.*;

public class TaxiGraphState extends GraphStateNode implements Comparable {

    private int nodeId;
    private double stateOfCharge;
    private double timeStamp;


    private int previousActionId = Integer.MAX_VALUE;
    private int previousNode = Integer.MAX_VALUE;

    private TaxiGraphState previousState = null;
    private Action previousAction = null;

    private HashMap<Integer, Double> recentlyVisitedNodes = new HashMap<>();


    public TaxiGraphState(int nodeId, double stateOfCharge, double timeStamp) {
        super(nodeId);
        keys.add(VAR_NODE);
        keys.add(VAR_STATE_OF_CHARGE);
        keys.add(VAR_TIMESTAMP);
        keys.add(VAR_PREVIOUS_ACTION);
        keys.add(VAR_PREVIOUS_STATE);

        this.nodeId = id;
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
                    if (value instanceof Double){
                        this.stateOfCharge = (double)value;
                        return this;
                    } else {
                        throw new RuntimeException("Invalid value data type " + value.getClass());
                    }
                case VAR_TIMESTAMP:
                    if (value instanceof Double){
                        this.timeStamp = (double)value;
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
        TaxiGraphState state = new TaxiGraphState(this.nodeId, this.stateOfCharge, this.timeStamp);
        state.set(Utils.VAR_PREVIOUS_ACTION, this.previousAction);
        state.set(Utils.VAR_PREVIOUS_STATE, this.previousState);
        state.setRecentlyVisitedNodes(new HashMap<>(recentlyVisitedNodes));
        return state;
    }

    public HashMap<Integer, Double> getRecentlyVisitedNodes() {
        return recentlyVisitedNodes;
    }

    public void setRecentlyVisitedNodes(HashMap<Integer, Double> recentlyVisitedNodes) {
        this.recentlyVisitedNodes = recentlyVisitedNodes;
    }

    public double getStateOfCharge() {
        return stateOfCharge;
    }


    public double getTimeStamp() {
        return timeStamp;
    }


    public int getNodeId() {
        return nodeId;
    }


    public int getPreviousActionId() {
        return previousActionId;
    }


    public int getPreviousNode() {
        return previousNode;
    }

    public TaxiGraphState getPreviousState() {
        return previousState;
    }

    public Action getPreviousAction() {
        return previousAction;
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
    public int compareTo(Object o) {
        TaxiGraphState state = (TaxiGraphState)o;
        return Double.compare(this.stateOfCharge, state.stateOfCharge);
    }
}
