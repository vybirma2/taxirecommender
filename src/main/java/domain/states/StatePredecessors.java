package domain.states;

import domain.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to gather all connections between individual states from a state space reachable by action
 */
public class StatePredecessors implements Serializable {

    private final List<List<List<Integer>>> predecessors = new ArrayList<>();
    private final int numOfStates;

    public StatePredecessors(int numOfStates) {
        this.numOfStates = numOfStates;
        init();
    }

    private void init(){
        for (int i = 0; i < Utils.NUM_OF_ACTION_TYPES; i++){
            List<List<Integer>> actionTypeList = new ArrayList<>();
            predecessors.add(actionTypeList);
            for (int j = 0; j < numOfStates; j++){
                actionTypeList.add(new ArrayList<>());
            }
        }
    }

    public void addPredecessor(int successorStateId, int predecessorStateId, int actionId){
        predecessors.get(actionId).get(successorStateId).add(predecessorStateId);
    }

    public List<Integer> getPreviousStateNodesOfActionInState(int actionId, int state){
        return predecessors.get(actionId).get(state);
    }
}
