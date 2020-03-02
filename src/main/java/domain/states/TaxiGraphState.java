package domain.states;

import burlap.domain.singleagent.graphdefined.GraphStateNode;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;

import static domain.Utils.*;

public class TaxiGraphState extends GraphStateNode  {

    private int nodeId;
    private double stateOfCharge;
    private double timeStamp;

    private int previousAction = 132132;
    private int previousNode = 232132;


    public TaxiGraphState(int nodeId, double stateOfCharge, double timeStamp) {
        super(nodeId);
        keys.add(VAR_NODE);
        keys.add(VAR_STATE_OF_CHARGE);
        keys.add(VAR_TIMESTAMP);
        keys.add(VAR_PREVIOUS_ACTION);
        keys.add(VAR_PREVIOUS_NODE);

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
                    if (value instanceof Integer){
                        this.previousAction = (int)value;
                        return this;
                    } else {
                        throw new RuntimeException("Invalid value data type " + value.getClass());
                    }
                case VAR_PREVIOUS_NODE:
                    if (value instanceof Integer){
                        this.previousNode = (int)value;
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


    public double getStateOfCharge() {
        return stateOfCharge;
    }


    public double getTimeStamp() {
        return timeStamp;
    }


    public int getNodeId() {
        return nodeId;
    }


    public int getPreviousAction() {
        return previousAction;
    }


    public int getPreviousNode() {
        return previousNode;
    }


    @Override
    public String toString() {
        return "TaxiGraphState{" +
                "stateOfCharge=" + stateOfCharge +
                ", timeStamp=" + timeStamp +
                ", id=" + id +
                '}';
    }
}
