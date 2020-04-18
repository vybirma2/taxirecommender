
import domain.TaxiGraphStateModel;
import domain.actions.MeasurableAction;
import domain.actions.TaxiActionType;
import domain.states.TaxiGraphState;

import java.util.*;


public class ReachableStatesGenerator {

    private final TaxiGraphStateModel taxiGraphStateModel;
    private final List<TaxiGraphState> reachableStates = new ArrayList<>();

    public ReachableStatesGenerator(TaxiGraphStateModel taxiGraphStateModel) {
        this.taxiGraphStateModel = taxiGraphStateModel;
    }

    public void performReachabilityFrom(TaxiGraphState startingState) {

        System.out.println("Starting reachability analysis");

        LinkedList<TaxiGraphState> openList = new LinkedList<>();
        TaxiGraphState currentState;
        openList.offer(startingState);

        while(true) {
            if (openList.isEmpty()) {
                System.out.println("Finished reachability analysis; # states: " + this.reachableStates.size());
                return;
            }

            currentState = openList.poll();

            this.reachableStates.add(currentState);

            openList.addAll(this.taxiGraphStateModel.stateTransitions(currentState));
        }
    }

    public List<TaxiGraphState> getReachableStates() {
        return reachableStates;
    }
}
