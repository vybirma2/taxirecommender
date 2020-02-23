package domain;

import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.StateUtilities;
import burlap.mdp.core.state.UnknownKeyException;
import cz.agents.multimodalstructures.nodes.RoadNode;

import java.util.Arrays;
import java.util.List;

import static domain.GraphDomain.*;

public class RoadNodeLocation implements ObjectInstance, MutableState {

    private RoadNode node;

    private String name = CLASS_LOCATION_NODE;

    private final static List<Object> keys = Arrays.asList(VAR_ROAD_NODE);

    public RoadNodeLocation(RoadNode node) {
        this.node = node;
    }

    public RoadNodeLocation(RoadNode node, String name) {
        this.node = node;
        this.name = name;
    }

    @Override
    public String className() {
        return CLASS_LOCATION_NODE;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public ObjectInstance copyWithName(String objectName) {
        return new RoadNodeLocation(node, objectName);
    }

    @Override
    public MutableState set(Object varKey, Object value) {
        if (varKey.equals(VAR_ROAD_NODE)){
            this.node = (RoadNode) value;
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
        if (varKey.equals(VAR_ROAD_NODE)){
            return node;
        } else {
            throw new UnknownKeyException(varKey);
        }
    }

    @Override
    public State copy() {
        return new RoadNodeLocation(node, name);
    }

    @Override
    public String toString() {
        return StateUtilities.stateToString(this);
    }
}
