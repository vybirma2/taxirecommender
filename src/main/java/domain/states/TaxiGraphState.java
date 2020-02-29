package domain.states;

import burlap.domain.singleagent.graphdefined.GraphStateNode;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;

import static domain.TaxiRecommenderDomainGenerator.*;

public class TaxiGraphState extends GraphStateNode  {

    private int nodeId;
    private int stateOfCharge;
    private int timeStamp;

    public TaxiGraphState(int nodeId, int stateOfCharge, int timeStamp) {
        super(nodeId);
        keys.add(VAR_NODE);
        keys.add(VAR_STATE_OF_CHARGE);
        keys.add(VAR_TIMESTAMP);

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

    public Integer getStateOfCharge() {
        return stateOfCharge;
    }

    public void setStateOfCharge(Integer stateOfCharge) {
        this.stateOfCharge = stateOfCharge;
    }

    public Integer getTimeStamp() {
        return timeStamp;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public void setTimeStamp(Integer timeStamp) {
        this.timeStamp = timeStamp;
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
