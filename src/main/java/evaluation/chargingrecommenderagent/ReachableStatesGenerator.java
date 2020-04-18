package evaluation.chargingrecommenderagent;

import domain.TaxiModel;
import domain.states.TaxiState;

import java.util.*;


public class ReachableStatesGenerator {

    private final TaxiModel taxiModel;
    private final List<TaxiState> reachableStates = new ArrayList<>();
    private final HashMap<TaxiState, TaxiState> reachableStatesMap = new HashMap<>();

    public ReachableStatesGenerator(TaxiModel taxiModel) {
        this.taxiModel = taxiModel;
    }

    public void performReachabilityFrom(TaxiState startingState) {

        System.out.println("Starting reachability analysis");

        LinkedList<TaxiState> openList = new LinkedList<>();
        TaxiState currentState;
        openList.offer(startingState);

        while(true) {
            if (openList.isEmpty()) {
                System.out.println("Finished reachability analysis; # states: " + this.reachableStates.size());
                return;
            }

            currentState = openList.poll();

            this.reachableStates.add(currentState);
            //this.reachableStatesMap.put(currentState, currentState);

            openList.addAll(this.taxiModel.allReachableStatesFromState(currentState));
        }
    }

    public List<TaxiState> getReachableStates() {
        return reachableStates;
    }

    public HashMap<TaxiState, TaxiState> getReachableStatesMap() {
        return /*reachableStatesMap*/null;
    }
}
