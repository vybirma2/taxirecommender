package domain.states;

import utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
