package domain;

import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.StateUtilities;
import burlap.mdp.core.state.UnknownKeyException;

import java.util.Arrays;
import java.util.List;

import static domain.GraphDomain.*;

public class StateOfCharge implements ObjectInstance, MutableState {

    private int stateOfCharge;

    private String name = CLASS_STATE_OF_CHARGE;

    private final static List<Object> keys = Arrays.asList(VAR_STATE_OF_CHARGE);

    public StateOfCharge(int stateOfCharge) {
        this.stateOfCharge = stateOfCharge;
    }

    public StateOfCharge(int stateOfCharge, String name) {
        this.stateOfCharge = stateOfCharge;
        this.name = name;
    }

    @Override
    public String className() {
        return CLASS_STATE_OF_CHARGE;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public ObjectInstance copyWithName(String objectName) {
        return new StateOfCharge(stateOfCharge, objectName);
    }

    @Override
    public MutableState set(Object varKey, Object value) {
        if (varKey.equals(VAR_STATE_OF_CHARGE)){
            this.stateOfCharge = (int) value;
        } else {
            throw new UnknownKeyException(varKey);
        }
        return this;
    }

    @Override
    public List<Object> variableKeys() {
        return keys;
    }

    @Override
    public Object get(Object varKey) {
        if (varKey.equals(VAR_STATE_OF_CHARGE)){
            return stateOfCharge;
        } else {
            throw new UnknownKeyException(varKey);
        }
    }

    @Override
    public State copy() {
        return new StateOfCharge(stateOfCharge, name);
    }

    @Override
    public String toString() {
        return StateUtilities.stateToString(this);
    }
}
