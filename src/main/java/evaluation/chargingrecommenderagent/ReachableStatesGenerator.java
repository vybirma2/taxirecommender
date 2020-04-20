package evaluation.chargingrecommenderagent;

import domain.TaxiModel;
import domain.actions.TaxiActionType;
import domain.states.TaxiState;

import java.io.Serializable;
import java.util.*;


public class ReachableStatesGenerator implements Serializable {

    private final TaxiModel taxiModel;
    private final HashMap<TaxiState, Integer> visitedStates = new HashMap<>();
    public final List<TaxiState> reachableStates = new ArrayList<>();

    public ReachableStatesGenerator(TaxiModel taxiModel) {
        this.taxiModel = taxiModel;
    }

    public void performReachabilityFrom(TaxiState startingState) {

        System.out.println("Starting reachability analysis");

        LinkedList<TaxiState> openList = new LinkedList<>();
        TaxiState currentState;
        openList.offer(startingState);
        addReachableState(startingState);

        while(true) {
            if (openList.isEmpty()) {
                System.out.println("Finished reachability analysis; # states: " + reachableStates.size());
                return;
            }

            currentState = openList.poll();

            openList.addAll(this.taxiModel.allReachableStatesFromState(currentState));
        }
    }

    public List<TaxiState> getReachableStates() {
        return reachableStates;
    }

    public TaxiState getState(TaxiState state) {
        return getVisitedState(state);
    }

    public boolean alreadyVisited(TaxiState state){
        return visitedStates.containsKey(state);
    }

    public TaxiState getVisitedState(TaxiState state){
        Integer taxiState = visitedStates.get(state);
        if (taxiState == null){
            return null;
        }
        return reachableStates.get(taxiState);
    }

    public void addReachableState(TaxiState state){
        reachableStates.add(state);
        visitedStates.put(state, state.getId());
    }

}
