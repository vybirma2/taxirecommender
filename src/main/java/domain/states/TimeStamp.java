package domain.states;

import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.StateUtilities;
import burlap.mdp.core.state.UnknownKeyException;

import java.util.Arrays;
import java.util.List;

import static domain.Utils.CLASS_TIME;
import static domain.Utils.VAR_TIMESTAMP;

public class TimeStamp implements ObjectInstance, MutableState {

    private int timeStamp;

    private String name = CLASS_TIME;

    private final static List<Object> keys = Arrays.asList(VAR_TIMESTAMP);

    public TimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public TimeStamp(int timeStamp, String name) {
        this.timeStamp = timeStamp;
        this.name = name;
    }

    @Override
    public String className() {
        return CLASS_TIME;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public ObjectInstance copyWithName(String objectName) {
        return new TimeStamp(timeStamp, objectName);
    }

    @Override
    public MutableState set(Object varKey, Object value) {
        if (varKey.equals(VAR_TIMESTAMP)){
            this.timeStamp = (int) value;
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
        if (varKey.equals(VAR_TIMESTAMP)){
            return timeStamp;
        } else {
            throw new UnknownKeyException(varKey);
        }
    }

    @Override
    public State copy() {
        return new TimeStamp(timeStamp, name);
    }

    @Override
    public String toString() {
        return StateUtilities.stateToString(this);
    }
}