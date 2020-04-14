package domain.states;

import domain.actions.ActionTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatePredecessors {
    private ArrayList<HashMap<Integer, List<Integer>>> predecessors = new ArrayList<>();


    public StatePredecessors() {
        predecessors.add(new HashMap<>());
        predecessors.add(new HashMap<>());
        predecessors.add(new HashMap<>());
        predecessors.add(new HashMap<>());
        predecessors.add(new HashMap<>());
    }


    public void addNextLocationPredecessor(int stateId, int predecessorId){
        if (predecessors.get(ActionTypes.TO_NEXT_LOCATION.getValue()).containsKey(stateId)){
            predecessors.get(ActionTypes.TO_NEXT_LOCATION.getValue()).get(stateId).add(predecessorId);
        } else {
            List<Integer> statePredecessors = new ArrayList<>();
            statePredecessors.add(predecessorId);
            predecessors.get(ActionTypes.TO_NEXT_LOCATION.getValue()).put(stateId, statePredecessors);
        }
    }


    public void addStayingInLocationPredecessor(int stateId, int predecessorId){
        if (predecessors.get(ActionTypes.STAYING_IN_LOCATION.getValue()).containsKey(stateId)){
            predecessors.get(ActionTypes.STAYING_IN_LOCATION.getValue()).get(stateId).add(predecessorId);
        } else {
            List<Integer> statePredecessors = new ArrayList<>();
            statePredecessors.add(predecessorId);
            predecessors.get(ActionTypes.STAYING_IN_LOCATION.getValue()).put(stateId, statePredecessors);
        }
    }


    public void addGoingChargingPredecessor(int stateId, int predecessorId){
        if (predecessors.get(ActionTypes.GOING_TO_CHARGING_STATION.getValue()).containsKey(stateId)){
            predecessors.get(ActionTypes.GOING_TO_CHARGING_STATION.getValue()).get(stateId).add(predecessorId);
        } else {
            List<Integer> statePredecessors = new ArrayList<>();
            statePredecessors.add(predecessorId);
            predecessors.get(ActionTypes.GOING_TO_CHARGING_STATION.getValue()).put(stateId, statePredecessors);
        }
    }



    public void addChargingPredecessor(int stateId, int predecessorId){
        if (predecessors.get(ActionTypes.CHARGING_IN_CHARGING_STATION.getValue()).containsKey(stateId)){
            predecessors.get(ActionTypes.CHARGING_IN_CHARGING_STATION.getValue()).get(stateId).add(predecessorId);
        } else {
            List<Integer> statePredecessors = new ArrayList<>();
            statePredecessors.add(predecessorId);
            predecessors.get(ActionTypes.CHARGING_IN_CHARGING_STATION.getValue()).put(stateId, statePredecessors);
        }
    }



    public void addPickUpPredecessor(int stateId, int predecessorId){
        if (predecessors.get(ActionTypes.PICK_UP_PASSENGER.getValue()).containsKey(stateId)){
            predecessors.get(ActionTypes.PICK_UP_PASSENGER.getValue()).get(stateId).add(predecessorId);
        } else {
            List<Integer> statePredecessors = new ArrayList<>();
            statePredecessors.add(predecessorId);
            predecessors.get(ActionTypes.PICK_UP_PASSENGER.getValue()).put(stateId, statePredecessors);
        }
    }


    public List<Integer> getPreviousStateNodesOfActionInState(int actionId, int state){
        return predecessors.get(actionId).get(state);
    }

}
