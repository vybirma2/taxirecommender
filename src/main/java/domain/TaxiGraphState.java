package domain;

import burlap.domain.singleagent.graphdefined.GraphStateNode;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;

import static domain.TaxiRecommenderDomainGenerator.*;

public class TaxiGraphState extends GraphStateNode  {

    private Integer stateOfCharge;
    private Integer timeStamp;

    public TaxiGraphState(int id, Integer stateOfCharge, Integer timeStamp) {
        super(id);
        keys.add(VAR_STATE_OF_CHARGE);
        keys.add(VAR_TIMESTAMP);

        this.stateOfCharge = stateOfCharge;
        this.timeStamp = timeStamp;
    }

    @Override
    public MutableState set(Object variableKey, Object value) {
        if (variableKey instanceof String){
            String key = (String) variableKey;
            switch (key){
                case VAR_NODE:
                    return super.set(variableKey, value);
                case VAR_STATE_OF_CHARGE:
                    if (value instanceof Integer){
                        this.stateOfCharge = (Integer)value;
                    } else {
                        throw new RuntimeException("Invalid value data type " + value.getClass());
                    }
                    break;
                case VAR_TIMESTAMP:
                    if (value instanceof Integer){
                        this.timeStamp = (Integer)value;
                    } else {
                        throw new RuntimeException("Invalid value data type " + value.getClass());
                    }
                    break;
                default:
                    throw new RuntimeException("Invalid key value type " + key);
            }
        } else {
            throw new RuntimeException("Invalid key data type " + variableKey.getClass());
        }
        return null;
    }


    @Override
    public State copy() {
        return new TaxiGraphState(this.id, this.stateOfCharge, this.timeStamp);
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
